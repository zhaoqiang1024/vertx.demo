package com.zq.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Title:
 * @author: zhaoqiang
 * @date: 2021/11/24 3:07 下午
 * @Description:
 */
public class SpringContextHolder implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.context=applicationContext;
    }

    public static <T> T getBean(Class<T> clazz){
        assertContextInjected();
        return context.getBean(clazz);
    }

    /**
     * 检查ApplicationContext不为空.
     */
    private static void assertContextInjected() {
        if (context == null) {
            throw new IllegalStateException("applicationContext属性未注入, 请在applicationContext.xml中定义SpringContextHolder或在SpringBoot启动类中注册SpringContextHolder.");
        }
    }

}
