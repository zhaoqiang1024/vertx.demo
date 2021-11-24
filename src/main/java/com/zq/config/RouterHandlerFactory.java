package com.zq.config;

import com.zq.annotation.RouterHandler;
import com.zq.annotation.RouterMapping;
import com.zq.annotation.RouterMethod;
import com.zq.controller.DemoController;
import com.zq.util.ReflectionUtil;
import com.zq.util.VertxHolder;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vertx.core.http.HttpHeaders.*;

/**
 * Router 对象创建
 */
public class RouterHandlerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(RouterHandlerFactory.class);

    // 需要扫描注册的Router路径
    private static volatile Reflections reflections;

    // 默认api前缀
    private static final String GATEWAY_PREFIX = "/";

    private volatile String gatewayPrefix = GATEWAY_PREFIX;


    public RouterHandlerFactory(String routerScanAddress) {
        Objects.requireNonNull(routerScanAddress, "The router package address scan is empty.");
        reflections = ReflectionUtil.getReflections(routerScanAddress);
    }

    public RouterHandlerFactory(List<String> routerScanAddresses) {
        Objects.requireNonNull(routerScanAddresses, "The router package address scan is empty.");
        reflections = ReflectionUtil.getReflections(routerScanAddresses);
    }

    public RouterHandlerFactory(String routerScanAddress, String gatewayPrefix) {
        Objects.requireNonNull(routerScanAddress, "The router package address scan is empty.");
        reflections = ReflectionUtil.getReflections(routerScanAddress);
        this.gatewayPrefix = gatewayPrefix;
    }

    /**
     * 开始扫描并注册handler
     */
    public Router createRouter() {
        Router router = Router.router(VertxHolder.getVertxInstance());
        router.route().handler(ctx -> {
            LOGGER.debug("The HTTP service request address information ===>path:{}, uri:{}, method:{}",
                    ctx.request().path(), ctx.request().absoluteURI(), ctx.request().method());
            ctx.response().headers().add(CONTENT_TYPE, "application/json; charset=utf-8");
            ctx.response().headers().add(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            ctx.response().headers().add(ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS, PUT, DELETE, HEAD");
            ctx.response().headers().add(ACCESS_CONTROL_ALLOW_HEADERS,
                    "X-PINGOTHER, Origin,Content-Type, Accept, X-Requested-With, Dev, Authorization, Version, Token");
            ctx.response().headers().add(ACCESS_CONTROL_MAX_AGE, "1728000");
            ctx.next();
        });
        Set<HttpMethod> method = new HashSet<HttpMethod>() {{
            add(HttpMethod.GET);
            add(HttpMethod.POST);
            add(HttpMethod.OPTIONS);
            add(HttpMethod.PUT);
            add(HttpMethod.DELETE);
            add(HttpMethod.HEAD);
        }};
        /* 添加跨域的方法 **/
        router.route().handler(CorsHandler.create("*").allowedMethods(method));
        router.route().handler(BodyHandler.create());

        try {
            Set<Class<?>> handlers = reflections.getTypesAnnotatedWith(RouterHandler.class);
            Comparator<Class<?>> comparator = (c1, c2) -> {
                RouterHandler routeHandler1 = c1.getAnnotation(RouterHandler.class);
                RouterHandler routeHandler2 = c2.getAnnotation(RouterHandler.class);
                return Integer.compare(routeHandler2.order(), routeHandler1.order());
            };
            List<Class<?>> sortedHandlers = handlers.stream().sorted(comparator).collect(Collectors.toList());
            for (Class<?> handler : sortedHandlers) {
                try {
                    registerNewHandler(router, handler);
                } catch (Exception e) {
                    LOGGER.error("Error register {}", handler,e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Manually Register Handler Fail，Error details：" + e.getMessage());
        }
        return router;
    }

    private void registerNewHandler(Router router, Class<?> handler) throws Exception {
        StringBuilder root = new StringBuilder(gatewayPrefix);
        //不以/开头则填充
        if (root.indexOf(GATEWAY_PREFIX)!=0) {
            root.insert(0,GATEWAY_PREFIX);
        }
        RouterHandler routeHandler = handler.getAnnotation(RouterHandler.class);
        root.append(routeHandler.value());
        //不以/结尾则填充
        if (root.lastIndexOf(GATEWAY_PREFIX) == root.length() - 1) {
            root.delete(root.length() - 1, root.length());
        }
        //从spring中获取到bean的实例，避免二次实例化带来内存开销
        Object instance = SpringContextHolder.getBean(handler);
        Method[] methods = handler.getMethods();
        Comparator<Method> comparator = (m1, m2) -> {
            RouterMapping mapping1 = m1.getAnnotation(RouterMapping.class);
            RouterMapping mapping2 = m2.getAnnotation(RouterMapping.class);
            return Integer.compare(mapping2.order(), mapping1.order());
        };

        List<Method> methodList = Stream.of(methods).filter(
                method -> method.isAnnotationPresent(RouterMapping.class)
        ).sorted(comparator).collect(Collectors.toList());
        for (Method method : methodList) {
            if (method.isAnnotationPresent(RouterMapping.class)) {
                RouterMapping mapping = method.getAnnotation(RouterMapping.class);
                RouterMethod routeMethod = mapping.method();
                String routeUrl = mapping.value();
                //如果二级地址以/开头则剪掉第一位，因为root已自带
                if (!routeUrl.startsWith("/")) {
                    routeUrl = GATEWAY_PREFIX + routeUrl;
                }
                //合并controller地址
                String url = root + routeUrl;
                if(url.endsWith("/")){
                    url = url.substring(0,url.length()-1);
                }
                Handler<RoutingContext> methodHandler = (Handler<RoutingContext>) method.invoke(instance);
                LOGGER.debug("Register New Handler -> {}:{}", routeMethod, url);
                Route route;
                switch (routeMethod) {
                    case POST:
                        route = router.post(url);
                        break;
                    case PUT:
                        route = router.put(url);
                        break;
                    case DELETE:
                        route = router.delete(url);
                        break;
                    case GET:
                        route = router.get(url);
                        break;
                    case OPTIONS:
                        route = router.options(url);
                        break;
                    case PATCH:
                        route = router.patch(url);
                        break;
                    case TRACE:
                        route = router.trace(url);
                        break;
                    default:
                        //默认ROUTER级别
                        route = router.route(url);
                }
//                if (StringUtils.isNotBlank(mineType)) {
//                    route.consumes(mineType);
//                }
                route.handler(methodHandler);
            }
        }
    }
}
