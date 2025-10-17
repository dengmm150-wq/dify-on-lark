package top.duhongming;

import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 在飞书上使用Dify应用
 *
 * @author duhongming
 * @see
 * @since 1.0.0
 */
@SpringBootApplication
public class DifyOnLarkApplication implements CommandLineRunner {
    @Resource(name = "wsClient")
    private com.lark.oapi.ws.Client client;

    public static void main(String[] args) {
        SpringApplication.run(DifyOnLarkApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        client.start();
    }

}
