package com.zq.flink;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple3;

/**
 * @Title:
 * @Description:
 * @author: zhaoqiang
 * @date: 2021/3/31 5:09 下午
 */
public class Tp99AggregateFun<IN,ACC,R> implements AggregateFunction<Tuple1<Integer>, Tuple3<String, Integer, Integer>, Double> {


    @Override
    public Tuple3<String, Integer, Integer> createAccumulator() {
        return new Tuple3<>("TP99", 0, 0);
    }

    @Override
    public Tuple3<String, Integer,Integer> add(Tuple1<Integer> value, Tuple3<String, Integer, Integer> acc) {
        return new Tuple3<>("TP99", value.f0 + acc.f1, acc.f2 + 1);
    }

    @Override
    public Double getResult(Tuple3<String, Integer, Integer> acc) {
        return new Double(acc.f1)/acc.f2;
    }

    @Override
    public Tuple3<String, Integer, Integer> merge(Tuple3<String, Integer, Integer> acc1, Tuple3<String, Integer, Integer> acc2) {
        return new Tuple3<>("TP99", acc1.f1 + acc2.f1, acc1.f2 + acc2.f2);
    }
}
