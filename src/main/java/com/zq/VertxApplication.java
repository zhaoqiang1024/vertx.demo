package com.zq;

import com.zq.config.RouterHandlerFactory;
import com.zq.util.VertxHolder;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.tracing.TracingOptions;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.ext.web.Router;
import io.vertx.tracing.zipkin.ZipkinTracingOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @Title:
 * @author: zhaoqiang
 * @date: 2021/11/24 10:48 上午
 * @Description:
 */
@Component
public class VertxApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxApplication.class);
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("开始启动vertx服务");
        VertxOptions options = new VertxOptions();
        options.setEventLoopPoolSize(5);
        options.setWorkerPoolSize(200);
        options.setTracingOptions(new ZipkinTracingOptions().setServiceName("demo service"));
        LOGGER.info("初始化vertx系统配置:{}",options);
        Vertx vertx = Vertx.vertx(options);
        LOGGER.info("初始化vertx实例");
        VertxHolder.init(vertx);
        //默认扫描当前类所在包及以下包
        String basePackage = this.getClass().getPackage().getName();
        //创建路由规则
        Router router = new RouterHandlerFactory(basePackage).createRouter();
        //启动vertx服务端口，与spring端口区分开
        HttpServerOptions serverOptions = new HttpServerOptions();
        serverOptions.setTracingPolicy(TracingPolicy.ALWAYS);
        HttpServer server = VertxHolder.getVertxInstance().createHttpServer(serverOptions);
        server.requestHandler(router);
        server.listen(8081);
        LOGGER.info("启动vertx服务完成");
    }
}
