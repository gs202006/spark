package com.atguigu.spark.pro

import java.sql.{Connection, PreparedStatement, ResultSet}
import java.text.SimpleDateFormat

import com.atguigu.bigdata.spark.streaming.JdbcUtil
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.ListBuffer

object SSkfk {
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


    //过滤kfk数据
    val blacklist: ListBuffer[String] = ListBuffer[String]()
    val reduceDS: DStream[((String, String, String), Int)] = valueDStream.transform(
      //获取黑名单列表
      rdd => {
        val connection: Connection = JdbcUtil.getConnection
        val pst: PreparedStatement = connection.prepareStatement("select userid from black_list")
        val res: ResultSet = pst.executeQuery()
        while (res.next()) {
          blacklist.append(res.getString(1))
        }
        res.close()
        pst.close()
        connection.close()

        //过滤kfk数据
        val restData: RDD[String] = rdd.filter(
          line => {
            val datas: Array[String] = line.split(" ")
            val usid: String = datas(3)
            !blacklist.contains(usid)
          }
        )
        //对不在黑名单的数据进行统计((天，user，ad),1)
        //格式化时间 yyyy-MM-dd
        val sdf = new SimpleDateFormat("yyyy-MM-dd")
        val reduceRdd: RDD[((String, String, String), Int)] = restData.map(
          line => {
            val strs: Array[String] = line.split(" ")
            val day: String = sdf.format(new java.util.Date(strs(0).toLong))
            val user = strs(3)
            val ad = strs(4)
            ((day, user, ad), 1)
          }
        ).reduceByKey(_ + _)
        reduceRdd
      }


    )
    reduceDS.print()
    reduceDS.foreachRDD(
      rdd => {
        //用rddforeach遍历获取connection太耗费资源  用分区遍历
        //一个分区建立一个链接比较合适
        rdd.foreachPartition({
          itr => {
            val connection: Connection = JdbcUtil.getConnection
            itr.foreach {
              //是否超过阈值
              case ((day, user, ad), sum) => {
                if (sum >= 30) {
                  //超过加入到黑名单 阈值设为30  ON DUPLICATE KEY UPDATE userid=?防止sql注入
                  JdbcUtil.executeUpdate(connection, "insert into black_list(userid) values(?)  ON DUPLICATE KEY UPDATE userid=?", Array(user, user))
                } else {
                  //未超的过的统计
                  val pstat = connection.prepareStatement(
                    """
                      | select
                      |    count
                      | from user_ad_count
                      | where dt = ? and userid = ? and adid = ?
                    """.stripMargin)
                  pstat.setString(1, day)
                  pstat.setString(2, user)
                  pstat.setString(3, ad)
                  val res: ResultSet = pstat.executeQuery()
                  if (res.next()) {
                    if (res.getInt(1) + sum >= 30) {
                      JdbcUtil.executeUpdate(connection, "insert into black_list(userid) values(?)  ON DUPLICATE KEY UPDATE userid=?", Array(user, user))
                    } else {
                      JdbcUtil.executeUpdate(connection,"update user_ad_count set count =? where dt =? and userid =? and adid =?",Array((res.getInt(1)+sum),day, user, ad))
                    }
                  }else{
                    JdbcUtil.executeUpdate(connection,"insert into user_ad_count (dt, userid, adid, count) values ( ?, ?, ?, ? )",Array(day, user, ad,sum))
                  }
                }
              }
            }
          }
        })
      }
    )

    valueDStream.print()

    //7.开启任务
    ssc.start()
    ssc.awaitTermination()


  }
}
