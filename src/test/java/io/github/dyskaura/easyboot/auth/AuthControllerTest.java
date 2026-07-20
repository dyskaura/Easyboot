package io.github.dyskaura.easyboot.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.dyskaura.easyboot.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void cleanUser() {
        userRepository.findByUsername("student").ifPresent(userRepository::delete);
    }

    @Test
    void shouldRegisterAndUseToken() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "student", "123456", "大学生用户", "student@example.com"
        );

        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("student"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(response).path("data").path("accessToken").asText();
        assertThat(token).isNotBlank();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("大学生用户"));
    }

    @Test
    void shouldRejectInvalidRegisterRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("a", "1", "", "wrong-email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void shouldLoginWithCaptcha() throws Exception {
        String captchaJson = mockMvc.perform(get("/api/captcha"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var captchaData = objectMapper.readTree(captchaJson).path("data");
        String svg = new String(Base64.getDecoder().decode(
                captchaData.path("image").asText().split(",", 2)[1]
        ), StandardCharsets.UTF_8);
        Matcher matcher = Pattern.compile("fill=\"#1f2937\">([^<]+)</text>").matcher(svg);
        assertThat(matcher.find()).isTrue();

        LoginRequest request = new LoginRequest(
                "admin", "EasyBoot123", captchaData.path("captchaId").asText(), matcher.group(1)
        );
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());
    }
}
