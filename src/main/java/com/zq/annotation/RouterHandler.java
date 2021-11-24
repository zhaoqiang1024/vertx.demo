package com.zq.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author zhaoqiang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RouterHandler {

    String value();
    int order() default 0;
}
