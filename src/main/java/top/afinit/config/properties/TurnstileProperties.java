package top.afinit.config.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cloudflare.turnstile")
public class TurnstileProperties {
    /**
     * 验证接口URL
     */
    private String verifyUrl;

    /**
     * 后端校验私钥
     */
    private String secretKey;
}
