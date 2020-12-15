package com.atguigu.spark.pro

import java.text.SimpleDateFormat

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}

object Pro_LastHourAdClick {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SSkfk")
    val ssc = new StreamingContext(sparkConf, Seconds(3))


    //3.定义Kafka参数
    val kafkaPara: Map[String, Object] = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "hadoop102:9092,hadoop103:9092,hadoop104:9092",
      ConsumerConfig.GROUP_ID_CONFIG -> "sparkstreaing",
      "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer"
    )

    //4.读取Kafka数据创建DStream
    val kafkaDStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Set("sparkstreaing"), kafkaPara))

    //5.将每条消息的KV取出
    val valueDStream: DStream[String] = kafkaDStream.map(record => record.value())
    val reduceDS: DStream[(Long, Int)] = valueDStream.map(
      line => {
        (line.split(" ")(0).toLong / 10000 * 10000, 1)
      }
    ).reduceByKeyAndWindow(
      (x: Int, y: Int) => x + y
      , Minutes(1), Seconds(9)
    )
    reduceDS
    val sdf = new SimpleDateFormat("yyyy-MM-dd")

    reduceDS.foreachRDD({
      rdd=>{
        rdd.sortBy(_._1)
      }
    })
    //7.开启任务
    ssc.start()
    ssc.awaitTermination()


  }
}
