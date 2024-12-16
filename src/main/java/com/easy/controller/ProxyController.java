package com.easy.controller;

import com.easy.exception.BusinessException;
import com.easy.pojo.dto.ProxyDTO;
import com.easy.resutils.R;
import com.easy.utils.proxy.GetXuiProxyUtil;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        List<String> resUrlList = new ArrayList<>();
        for (String url : urlList) {
            String res;
            try {
                res = GetXuiProxyUtil.loginAndGetUrl(url, proxyDTO.getUsername(), proxyDTO.getPassword());
            } catch (Exception e) {
                resUrlList.add("URL：" + url + " 获取失败 ： " + e.getMessage());
                continue;
            }
            try {
                if (res != null) {
                    String trimmed = res.substring(1, res.length() - 1);
                    String[] stringUrls = trimmed.split(",");
                    resUrlList.addAll(Arrays.asList(stringUrls));
                }
            } catch (Exception e) {
                resUrlList.add("URL：" + url + " 数据结构处理出错");
            }
        }
        return R.ok(resUrlList);
    }


}
