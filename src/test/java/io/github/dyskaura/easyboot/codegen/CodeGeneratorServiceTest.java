package io.github.dyskaura.easyboot.codegen;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

class CodeGeneratorServiceTest {

    @Test
    void shouldGenerateFiveJavaFiles() throws Exception {
        GeneratorRequest request = new GeneratorRequest(
                "com.example.course", "Course", "biz_course",
                List.of(
                        new GeneratorField("name", "String", true, "课程名称"),
                        new GeneratorField("credit", "Integer", true, "学分")
                )
        );
        byte[] result = new CodeGeneratorService().generate(request);

        int count = 0;
        String entity = "";
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(result), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                count++;
                if (entry.getName().endsWith("/Course.java")) {
                    entity = new String(zip.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
        }
        assertThat(count).isEqualTo(5);
        assertThat(entity).contains("@Table(name = \"biz_course\")", "private String name");
    }
}
