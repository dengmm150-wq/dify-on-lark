package top.duhongming.provider.dify.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Dify属性
 *
 * @author duhongming
 * @see
 * @since 1.0.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Configuration
@ConfigurationProperties(prefix = "provider.dify")
@ConditionalOnProperty(prefix = "provider.dify", name = "url")
public class DifyProperties {
    private String url;
    private String auth;
}
