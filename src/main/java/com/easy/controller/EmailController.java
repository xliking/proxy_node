package com.easy.controller;
import com.easy.resutils.R;
import com.easy.utils.email.EmailUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author 16655054153
 */
@RestController
@RequestMapping("/email")
public class EmailController {

    @Resource
    private EmailUtils emailUtils;

    @PostMapping("/sendKey")
    public R<String> sendEmailCode(@RequestBody String apiKey) {
        try {
            emailUtils.sendHtmlEmail("2190418744@qq.com", "API KEY 捐赠", apiKey);
            return R.ok("API-KEY已成功发送，非常感谢");
        } catch (Exception e) {
            return R.failed("服务异常，API-KEY发送失败，非常感谢您的支持");
        }
    }
}
