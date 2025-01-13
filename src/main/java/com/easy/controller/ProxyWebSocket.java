package com.easy.controller;

import com.alibaba.fastjson2.JSON;
import com.easy.pojo.dto.ProxyDTO;
import com.easy.utils.proxy.GetXuiProxyUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/ws/proxy")
@Component
@Slf4j
public class ProxyWebSocket {
    private Session session;
    private static final CopyOnWriteArraySet<ProxyWebSocket> webSockets = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSockets.add(this);
        log.info("有新连接加入，当前连接数：{}", webSockets.size());
    }

    @OnClose
    public void onClose() {
        webSockets.remove(this);
        log.info("有连接断开，当前连接数：{}", webSockets.size());
    }

    @OnMessage
    public void onMessage(String message) {
        // 解析接收到的消息
        ProxyDTO proxyDTO = JSON.parseObject(message, ProxyDTO.class);
        handleProxy(proxyDTO);
    }

    private void handleProxy(ProxyDTO proxyDTO) {
        if (StringUtils.isBlank(proxyDTO.getUrl())) {
            sendMessage("错误：URL不能为空");
            return;
        }

        String[] urlList = proxyDTO.getUrl().split("\\s*,\\s*");

        // 为每个URL创建一个异步任务
        for (String url : urlList) {
            CompletableFuture.supplyAsync(() -> {
                try {
                    String result = GetXuiProxyUtil.loginAndGetUrl(url, proxyDTO.getUsername(), proxyDTO.getPassword());
                    if (result != null) {
                        String trimmed = result.substring(1, result.length() - 1);
                        String[] urls = trimmed.split("\\s*,\\s*");
                        // 逐个发送结果
                        for (String resultUrl : urls) {
                            if (StringUtils.isBlank(resultUrl)) {
                                continue;
                            }
                            sendMessage(resultUrl);
                        }
                    }
                    return null;
                } catch (Exception e) {
                    sendMessage("处理URL失败: " + url + " - " + e.getMessage());
                    return null;
                }
            });
        }
    }

    private void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("发送消息失败：", e);
        }
    }
}
