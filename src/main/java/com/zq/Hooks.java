//package com.zq;
//
//import org.reactivestreams.Publisher;
//import org.reactivestreams.Subscription;
//import org.slf4j.MDC;
//import org.springframework.stereotype.Component;
//import reactor.core.CoreSubscriber;
//import reactor.core.publisher.Operators;
//import reactor.util.context.Context;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@Component
//@SuppressWarnings("all")
//public final class Hooks {
//
//  private Function<? super Publisher<Object>, ? extends Publisher<Object>> mdcHook = Operators.lift((scannable, coreSubscriber) -> new CoreSubscriber() {
//    @Override
//    public void onSubscribe(Subscription s) {
//      coreSubscriber.onSubscribe(s);
//    }
//
//    @Override
//    public void onNext(Object o) {
//      Context context = coreSubscriber.currentContext();
//      if (!context.isEmpty()) {
//        MDC.setContextMap(context.stream().collect(Collectors.toMap(entry -> entry.getKey().toString(), entry -> entry.getValue().toString())));
//      } else {
//        MDC.clear();
//      }
//      coreSubscriber.onNext(o);
//    }
//
//    @Override
//    public void onError(Throwable throwable) {
//      coreSubscriber.onError(throwable);
//    }
//
//    @Override
//    public void onComplete() {
//      coreSubscriber.onComplete();
//    }
//
//    @Override
//    public Context currentContext() {
//      return coreSubscriber.currentContext();
//    }
//  });
//
//
//  @PostConstruct
//  public void setHook() {
//    reactor.core.publisher.Hooks.onEachOperator("UUID", mdcHook);
//  }
//
//  @PreDestroy
//  public void resetHook() {
//    reactor.core.publisher.Hooks.resetOnEachOperator("UUID");
//  }
//}