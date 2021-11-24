package com.zq.flink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * @Title:
 * @Description:
 * @author: zhaoqiang
 * @date: 2021/3/31 6:04 下午
 */
public class StringFlatMapFun implements FlatMapFunction<String, Tuple2<String,Integer>> {
    @Override
    public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
        String[] tokens = s.toLowerCase().split(" ");
        for (String token : tokens) {
            collector.collect(new Tuple2<>(token,1));
        }
    }
}
