package com.atguigu.scala

import org.json.Test

@Test
object Array_Test {
  def main(args: Array[String]): Unit = {

  val arrs: Array[Array[Int]] = Array.ofDim[Int](3,2)
    arrs.foreach(
      arr=>{
        println(arr.mkString(" "))
      }
    )
  }
}
