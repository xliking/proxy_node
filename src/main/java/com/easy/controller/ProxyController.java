package com.easy.controller;

import com.easy.exception.BusinessException;
import com.easy.pojo.dto.ProxyDTO;
import com.easy.resutils.R;
import com.easy.utils.proxy.GetXuiProxyUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
        String[] urlList = proxyDTO.getUrl().split("\\s*,\\s*");
        List<CompletableFuture<String>> futures = Arrays.stream(urlList)
                .map(url -> CompletableFuture.supplyAsync(() -> GetXuiProxyUtil.loginAndGetUrl(url, proxyDTO.getUsername(), proxyDTO.getPassword()))
                        .handle((result, exception) -> exception != null ? "" : result)) // Handle exception here
                .toList();
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        CompletableFuture<List<String>> resultFuture = allFutures.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .filter(str -> !str.isEmpty())
                        .map(res -> res.substring(1, res.length() - 1))
                        .flatMap(trimmed -> Arrays.stream(trimmed.split("\\s*,\\s*")))
                        .collect(Collectors.toList())
        );
        List<String> resUrlList = resultFuture.join();
        return R.ok(resUrlList);
    }


}
