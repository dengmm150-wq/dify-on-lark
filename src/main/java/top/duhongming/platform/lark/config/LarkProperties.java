package top.duhongming.platform.lark.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 飞书属性
 *
 * @author duhongming
 * @see
 * @since 1.0.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Configuration
@ConfigurationProperties(prefix = "platform.lark")
@ConditionalOnProperty(prefix = "platform.lark", name = "appId")
public class LarkProperties {
    private String appId;
    private String appName;
    private String appSecret;
    private String verificationToken;
    private String encryptKey;
    private String cardTemplateId;
}
