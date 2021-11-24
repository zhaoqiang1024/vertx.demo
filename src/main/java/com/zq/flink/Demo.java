package com.zq.flink;


import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.time.Duration;

/**
 * @Title:
 * @Description:
 * @author: zhaoqiang
 * @date: 2021/3/30 8:05 下午
 */
public class Demo {
    public static void main(String[] args) throws Exception {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        DataStream<Tuple1<Integer>> dataStream = env.socketTextStream("127.0.0.1", 9999)
//                .keyBy((x)->x)
//                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
//                .aggregate(new Tp99AggregateFun());
//        env.execute();
        WatermarkStrategy<String> strategy = WatermarkStrategy
                .<String>forBoundedOutOfOrderness(Duration.ofSeconds(3500))
                .withTimestampAssigner((event, timestamp) -> Long.parseLong(event.split(" ")[0]));
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> text = env.socketTextStream("localhost",9999,"\n");
        DataStream<String> dataStream = text.assignTimestampsAndWatermarks(strategy);
        dataStream.flatMap(new StringFlatMapFun()).keyBy((x)->x.f0)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5))).sum(1).print();
        env.execute("StreamingJavaApp");
    }
}
