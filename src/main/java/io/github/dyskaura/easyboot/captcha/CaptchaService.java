package io.github.dyskaura.easyboot.captcha;

import io.github.dyskaura.easyboot.cache.KeyValueStore;
import io.github.dyskaura.easyboot.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaptchaService {

    private static final String PREFIX = "captcha:";
    private static final int EXPIRES_IN = 180;
    private final SecureRandom random = new SecureRandom();
    private final KeyValueStore keyValueStore;

    public CaptchaResponse create() {
        String id = UUID.randomUUID().toString();
        String code = randomCode();
        keyValueStore.put(PREFIX + id, code.toLowerCase(), Duration.ofSeconds(EXPIRES_IN));
        return new CaptchaResponse(id, toDataUri(code), EXPIRES_IN);
    }

    public void verify(String id, String code) {
        if (id == null || code == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "请输入验证码");
        }
        String expected = keyValueStore.get(PREFIX + id)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "验证码已过期"));
        keyValueStore.delete(PREFIX + id);
        if (!expected.equalsIgnoreCase(code.trim())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "验证码错误");
        }
    }

    private String randomCode() {
        String alphabet = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
        StringBuilder result = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            result.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return result.toString();
    }

    private String toDataUri(String code) {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="140" height="48">
                  <rect width="100%%" height="100%%" rx="8" fill="#f3f4f6"/>
                  <path d="M5 38 L135 10 M8 12 L130 40" stroke="#cbd5e1" stroke-width="1"/>
                  <text x="70" y="33" text-anchor="middle" font-family="monospace"
                        font-size="27" font-weight="700" letter-spacing="7" fill="#1f2937">%s</text>
                </svg>
                """.formatted(code);
        String base64 = Base64.getEncoder().encodeToString(svg.getBytes(StandardCharsets.UTF_8));
        return "data:image/svg+xml;base64," + base64;
    }
}
