package top.duhongming.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BeanUtilsTest {

    private ApplicationContext mockContext;
    private TestBean testBean;
    private ApplicationContext originalContext;

    @BeforeEach
    void setUp() {
        mockContext = mock(ApplicationContext.class);
        testBean = new TestBean("test");

        // 保存原始的ApplicationContext
        try {
            Field field = BeanUtils.class.getDeclaredField("applicationContext");
            field.setAccessible(true);
            originalContext = (ApplicationContext) field.get(null);
            // 设置mock的ApplicationContext
            field.set(null, mockContext);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ApplicationContext", e);
        }
    }

    @AfterEach
    void tearDown() {
        // 恢复原始的ApplicationContext，确保测试之间不会相互影响
        try {
            Field field = BeanUtils.class.getDeclaredField("applicationContext");
            field.setAccessible(true);
            field.set(null, originalContext);
        } catch (Exception e) {
            // 忽略清理错误
        }
    }

    @Test
    void testGetBeanWithNameAndClass() {
        String beanName = "testBean";
        when(mockContext.getBean(beanName, TestBean.class)).thenReturn(testBean);

        TestBean result = BeanUtils.getBean(beanName, TestBean.class);

        assertNotNull(result);
        assertEquals("test", result.getValue());
        verify(mockContext).getBean(beanName, TestBean.class);
    }

    @Test
    void testGetBeanWithClass() {
        when(mockContext.getBean(TestBean.class)).thenReturn(testBean);

        TestBean result = BeanUtils.getBean(TestBean.class);

        assertNotNull(result);
        assertEquals("test", result.getValue());
        verify(mockContext).getBean(TestBean.class);
    }

    @Test
    void testPrintAllBeansWhenContextInitialized() {
        String[] beanNames = {"bean1", "bean2", "bean3"};
        when(mockContext.getBeanDefinitionNames()).thenReturn(beanNames);
        when(mockContext.getBean("bean1")).thenReturn(new Object());
        when(mockContext.getBean("bean2")).thenReturn(new Object());
        when(mockContext.getBean("bean3")).thenReturn(new Object());

        // 这个方法主要打印日志，我们只需要验证它不会抛出异常
        assertDoesNotThrow(BeanUtils::printAllBeans);

        verify(mockContext).getBeanDefinitionNames();
    }

    // 辅助类用于测试
    static class TestBean {
        private String value;

        public TestBean(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}

