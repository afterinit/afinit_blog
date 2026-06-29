package top.afinit.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cloudflare.ai")
public class CloudflareModerProperties {
    private String url;
    private String apiToken;
}
