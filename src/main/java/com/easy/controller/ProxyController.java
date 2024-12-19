package com.easy.controller;

import com.easy.exception.BusinessException;
import com.easy.pojo.dto.ProxyDTO;
import com.easy.resutils.R;
import com.easy.utils.proxy.GetXuiProxyUtil;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Administrator
 */
@RestController
@CrossOrigin
public class ProxyController {

    @GetMapping("/api/test")
    public R<String> test() {
        return R.ok("测试成功");
    }

    @PostMapping("/api")
    public R<List<String>> proxy(@RequestBody ProxyDTO proxyDTO) {
        if (proxyDTO.getUrl() == null || proxyDTO.getUrl().trim().isEmpty()) {
            throw new BusinessException("URL不能为空");
        }
        String[] urlList = proxyDTO.getUrl().split(",");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<String> resUrlList = new ArrayList<>();
        // 使用CompletableFuture并行化处理
        for (String url : urlList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                String res;
                try {
                    res = GetXuiProxyUtil.loginAndGetUrl(url, proxyDTO.getUsername(), proxyDTO.getPassword());
                    if (res != null) {
                        String trimmed = res.substring(1, res.length() - 1);
                        String[] stringUrls = trimmed.split(",");
                        synchronized (resUrlList) {
                            resUrlList.addAll(Arrays.asList(stringUrls));
                        }
                    }
                } catch (Exception e) {
                    synchronized (resUrlList) {
                        resUrlList.add("URL：" + url + " 获取失败 ： " + e.getMessage());
                    }
                }
            });
            futures.add(future);
        }
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            throw new BusinessException("处理过程中发生异常：" + e.getMessage());
        }

        return R.ok(resUrlList);
    }


}
