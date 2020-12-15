package com.atguigu.spark.pro

import java.sql.Connection
import java.text.SimpleDateFormat

import com.atguigu.bigdata.spark.streaming.JdbcUtil
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Pro_AdClickAnalysis {
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

    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    val reduceDS: DStream[((String,String ,String, String), Int)] = valueDStream.map({
      line => {
        val strs: Array[String] = line.split(" ")
        val day: String = sdf.format(new java.util.Date(strs(0).toLong))
        val area = strs(1)
        val city = strs(2)
        val ad = strs(4)
        ((day, area,city,ad), 1)
      }
    }).reduceByKey(_ + _)
    reduceDS.print()
    reduceDS.foreachRDD(
      rdd =>{
        rdd.foreachPartition(
          itertor =>{
            val connection: Connection = JdbcUtil.getConnection
            itertor.foreach { case ((day,area,city,ad), count) => {
                JdbcUtil.executeUpdate(connection,"insert into area_city_ad_count (dt,area,city,adid,count) values (?,?,?,?,?) on duplicate key update count =count+? ",
                Array(day,area,city,ad,count,count))
            }

            }
            connection.close()
          }
        )
      }
    )
   // valueDStream.print()

    //7.开启任务
    ssc.start()
    ssc.awaitTermination()


  }
}
