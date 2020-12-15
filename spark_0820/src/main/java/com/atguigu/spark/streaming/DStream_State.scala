package com.atguigu.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object DStream_State {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    ssc.checkpoint("./ck")
    val DS: ReceiverInputDStream[String] = ssc.socketTextStream("hadoop102", 111)
    val res: DStream[(String, Int)] = DS.flatMap(_.split(" ")).map((_, 1))

    res.updateStateByKey({
      (cur: Seq[Int], buf: Option[Int]) => {
        val curVal: Int = cur.sum
        val bufVal: Int = buf.getOrElse(0)
        val newBuf = curVal + bufVal
        Option(newBuf)
      }
    })
  }
}
