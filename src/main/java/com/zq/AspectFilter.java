//package com.zq;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.slf4j.MDC;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
///**
// * @Title:
// * @author: zhaoqiang
// * @date: 2021/11/23 6:33 下午
// * @Description:
// */
//@Component
//@Aspect
//public class AspectFilter {
//    private final String POINT_CUT = "execution(* com.zq.controller..*(..))";
//    @Pointcut(POINT_CUT)
//    private void pointcut(){}
//
//    @Around(value = POINT_CUT)
//    public Object doAroundAdvice(ProceedingJoinPoint proceedingJoinPoint){
//        try {
//            MDC.put("UUID", UUID.randomUUID().toString());
//            Object obj = proceedingJoinPoint.proceed();
//            MDC.remove("UUID");
//            return obj;
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//        return null;
//    }
//}
