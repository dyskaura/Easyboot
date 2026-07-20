package io.github.dyskaura.easyboot.captcha;

import io.github.dyskaura.easyboot.cache.InMemoryKeyValueStore;
import io.github.dyskaura.easyboot.common.BusinessException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;

class CaptchaServiceTest {

    private final CaptchaService service = new CaptchaService(new InMemoryKeyValueStore());

    @Test
    void shouldCreateAndVerifyCaptchaOnlyOnce() {
        CaptchaResponse response = service.create();
        String svg = new String(Base64.getDecoder().decode(
                response.image().substring(response.image().indexOf(',') + 1)
        ), StandardCharsets.UTF_8);
        Matcher matcher = Pattern.compile("fill=\"#1f2937\">([^<]+)</text>").matcher(svg);
        assertThat(matcher.find()).isTrue();
        String code = matcher.group(1);

        assertThatCode(() -> service.verify(response.captchaId(), code)).doesNotThrowAnyException();
        assertThatThrownBy(() -> service.verify(response.captchaId(), code))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("过期");
    }
}
