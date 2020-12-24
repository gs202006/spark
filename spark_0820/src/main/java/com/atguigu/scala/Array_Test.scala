package com.atguigu.scala

import org.json.Test

@Test
object Array_Test {
  def main(args: Array[String]): Unit = {

    val arrs: Array[Array[Int]] = Array.ofDim[Int](3, 2)
    arrs.foreach(
      arr => {
        println(arr.mkString(" "))
      }
    )
    val list1 = List(12, 3, 4, 5, 3)
    val list2 = List(3, 4, 5, 7, 7,4,5,45,6)
    println(list1.zip(list2))
val iterator = list2.sliding(3,3)
    while (iterator.hasNext){
      println(iterator.next())

    }

  }
}





