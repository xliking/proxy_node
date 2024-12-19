package com.easy.controller;

import com.easy.pojo.dto.KeyDTO;
import com.easy.resutils.R;
import com.easy.utils.email.EmailUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author xlike
 */
@RestController
@CrossOrigin
@RequestMapping("/email")
public class EmailController {

    @Resource
    private EmailUtils emailUtils;

    @PostMapping("/sendKey")
    public R<String> sendEmailCode(@RequestBody KeyDTO keyDTO) {
        try {
            if (keyDTO.getKey() == null || keyDTO.getKey().isEmpty()) {
                return R.failed("API-KEY是空的呢");
            }
            if (keyDTO.getKey().length() < 30) {
                return R.failed("API-KEY不太对呢，但还是非常感谢您的支持");
            }
            emailUtils.sendHtmlEmail("2190418744@qq.com", "API KEY 捐赠", keyDTO.getKey());
            return R.ok("API-KEY已成功发送，非常感谢");
        } catch (Exception e) {
            return R.failed("服务异常，API-KEY发送失败，非常感谢您的支持");
        }
    }
}
