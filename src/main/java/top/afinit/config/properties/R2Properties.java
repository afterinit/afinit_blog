package top.afinit.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "r2")
public class R2Properties {
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String bucketName;
    private String publicDomain;

}
