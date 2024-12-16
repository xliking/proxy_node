package com.easy.utils.proxy;

import com.alibaba.fastjson2.JSONObject;
import com.easy.exception.BusinessException;
import com.easy.pojo.dto.ProxyDTO;
import com.easy.utils.okhttp3.OkHttpUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author Administrator
 */
public class GetXuiProxyUtil {



    /**
     * 调用用户提供的login方法。
     * @return Session Cookie，如果登录失败或未获取到则返回null
     */
    public static String loginAndGetUrl(String url,String username, String password) {
        // 判断url是不是合法的http的url
        if(!isHttpUrl(url)){
            throw new BusinessException("URL不合法");
        }
        // 如果url最后面是 / 就去掉
        if(url.endsWith("/")){
            url = url.substring(0, url.length()-1);
        }
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("username", Optional.ofNullable(username).filter(s ->!s.isEmpty()).orElse("admin"));
        bodyMap.put("password", Optional.ofNullable(password).filter(s ->!s.isEmpty()).orElse("admin"));
        bodyMap.put("LoginSecret", "");
        RequestBody formRequestBody = OkHttpUtils.createFormRequestBody(bodyMap);
        try {
            String reUrl = url + "/login";
            Response response = OkHttpUtils.post(reUrl, formRequestBody);
            String respBody = OkHttpUtils.getResponseBodyAsString(response);
            if (respBody != null) {
                JSONObject resObj = JSONObject.parseObject(respBody);
                if ("false".equals(resObj.get("success"))) {
                    throw new BusinessException("登录失败");
                }
            }
            String session = OkHttpUtils.getCookie(response, "session");
            return getNodeList(url, session);
        } catch (IOException e) {
            throw new BusinessException("未知原因，登录请求失败");
        }
    }

    public static boolean isHttpUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            String protocol = url.getProtocol();
            return "http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol);
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private static String getNodeList(String url, String session) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Cookie", "session=" + session);
        List<String> link = null;
        try {
            url = url + "/panel/inbound/list";
            Response response = OkHttpUtils.post(url, headerMap, new RequestBody() {
                @Nullable
                @Override
                public MediaType contentType() {
                    return null;
                }

                @Override
                public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {

                }
            });
            String bodyAsString = OkHttpUtils.getResponseBodyAsString(response);
            if (response.code() == 200 && bodyAsString != null) {
                JSONObject jsonObject = JSONObject.parseObject(bodyAsString);
                if ("false".equals(jsonObject.getString("success"))) {
                    throw new BusinessException("获取节点列表失败");
                }
            }
            URL ur = new URL(url);
            String path = ur.getHost();
            link = GetProxyUrlUtil.getLink(bodyAsString, path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return link.toString();
    }


}

