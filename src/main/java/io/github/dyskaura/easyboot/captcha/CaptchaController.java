package io.github.dyskaura.easyboot.captcha;

import io.github.dyskaura.easyboot.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    @GetMapping
    public ApiResponse<CaptchaResponse> create() {
        return ApiResponse.success(captchaService.create());
    }
}
