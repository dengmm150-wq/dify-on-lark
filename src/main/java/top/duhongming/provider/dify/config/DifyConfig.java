package top.duhongming.provider.dify.config;

import io.github.imfangs.dify.client.DifyClient;
import io.github.imfangs.dify.client.DifyClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.duhongming.platform.lark.config.LarkProperties;

/**
 * Dify配置
 *
 * @author duhongming
 * @see
 * @since 1.0.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DifyConfig {
    private final DifyProperties difyProperties;

    @Bean("difyClient")
    public DifyClient difyClient(LarkProperties larkProperties) {
        return DifyClientFactory.createClient(difyProperties.getUrl(), difyProperties.getAuth());
    }
}
