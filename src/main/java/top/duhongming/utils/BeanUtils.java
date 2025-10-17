package top.duhongming.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring Bean工具
 *
 * @author duhongming
 * @see
 * @since 1.0.0
 */
@Slf4j
@Component
public class BeanUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    // 静态方法：获取所有Bean
    public static void printAllBeans() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext未初始化");
        }

        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            log.info("Bean名称：" + beanName + "，实例：" + bean);
        }
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return (T) applicationContext.getBean(beanName, clazz);
    }

    public static <T> T getBean(Class<T> clazz) {
        return (T) applicationContext.getBean(clazz);
    }
}