package com.atguigu.scala

import scala.collection.mutable.ArrayBuffer

import org.junit.Test

class ArraryTest {

  @Test
  def arrarDim: Unit = {
    val arrs: Array[Array[Int]] = Array.ofDim[Int](4, 4)
    arrs.foreach(
      arr => {
        println(arr.mkString(" "))
      }
    )
  }

  @Test
  def arrConcat: Unit = {
    val arr1 = Array(1, 2, 3, 4)
    val arr2 = Array(11, 2, 3, 4)
    val arr3: Array[Int] = Array.concat(arr1, arr2)
    println(arr3.mkString("  "))
  }

  @Test
  def arrFill: Unit = {

    val arr8:Array[Int] = Array.fill[Int](5)(-1)
    println(arr8.mkString("  "))
    arr8.foreach(println)
  }
@Test
  def arrBuff: Unit ={
 new ArrayBuffer
}


}
