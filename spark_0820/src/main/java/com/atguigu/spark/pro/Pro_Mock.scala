package com.atguigu.spark.pro

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import scala.collection.mutable.ListBuffer
import scala.util.Random

object Pro_Mock {
  def main(args: Array[String]): Unit = {
    val topic = "sparkstreaing"

    // 创建配置对象
    val prop = new java.util.Properties()
    // 添加配置
    prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop102:9092")
    prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](prop)

    while ( true ) {
      // 向Kafka发送的模拟数据对象
      for ( data <- mockData() ) {
        val record = new ProducerRecord[String, String](topic, data)
        producer.send(record)
        println(data)
      }

      Thread.sleep(2000)
    }
  }
  def mockData() = {
    val areas = List("华北", "华南", "华中")
    val cities = List("东京", "上海", "深圳")
    val list = ListBuffer[String]()
    for ( i <- 1 to new Random().nextInt(50) ) {
      val area = areas(new Random().nextInt(3))
      val city = cities(new Random().nextInt(3))
      val userid = new Random().nextInt(6) + 1
      val adid = new Random().nextInt(6) + 1

      val dataString = s"${System.currentTimeMillis()} ${area} ${city} ${userid} ${adid}"
      list.append(dataString)
    }
    list
  }
}
