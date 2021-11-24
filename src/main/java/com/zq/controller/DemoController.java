package com.zq.controller;

import com.zq.annotation.RouterHandler;
import com.zq.annotation.RouterMapping;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * @Title:
 * @author: zhaoqiang
 * @date: 2021/11/23 5:22 下午
 * @Description:
 */
@Component
@RouterHandler("demo")
public class DemoController extends BaseRestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);

    @RouterMapping(value = "test")
    public Handler<RoutingContext> test(){
        return ctx->{
            LOGGER.info("{}",MDC.getMDCAdapter().getCopyOfContextMap());
            fireJsonResponse(ctx,"hello world");
        };
    }
}
