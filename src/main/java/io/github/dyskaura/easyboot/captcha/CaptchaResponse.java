package io.github.dyskaura.easyboot.captcha;

public record CaptchaResponse(String captchaId, String image, int expiresIn) {
}
