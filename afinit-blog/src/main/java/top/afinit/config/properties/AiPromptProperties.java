package top.afinit.config.properties;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai.prompts")
public class AiPromptProperties {
    private Resource safetyAuditPath;
    private String safetyAuditPrompt;

    @PostConstruct
    public void init() {
        try {
            if (safetyAuditPath != null) {
                safetyAuditPrompt = safetyAuditPath.getContentAsString(StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load safety audit prompt file", e);
        }
    }

}
