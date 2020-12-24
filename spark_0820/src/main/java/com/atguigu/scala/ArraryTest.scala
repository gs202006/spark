package com.atguigu.scala
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

    val arr8: Array[Int] = Array.fill[Int](5)(-1)
    println(arr8.mkString("  "))
    arr8.foreach(println)
  }

  @Test
  def arrBuff: Unit = {

  }

  @Test
  def listReduce: Unit = {
    val list1 = List(1, 2, 3, 4)
    println(list1.reduce(_ + _))
    println(list1.reduce(_ - _))
    //(b,Int)=>b
    println(list1.reduceLeft(_ - _))
    //reversed.reduceLeft[B]((x, y) => op(y, x))
    println(list1.reduceRight(_ - _))

  }

  @Test
  def listFold: Unit = {
    val list1 = List(1, 2, 3, 4, 5)
    //折叠

    println(list1.fold(6)(_ + _))
    println(list1.fold(6)(_ - _))
    //底层Left
    println(list1.foldLeft(0)(_ - _))
    //reversed.foldLeft(z)((x, y) => op(y, x))
    println(list1.foldRight(0)(_ - _))
  }

  @Test
  def listFold_Scan: Unit = {
    val list = List(1, 2, 3, 4)
    println(list.fold(0)(_ + _))
    println(list.scan(0)(_ + _))
    println(list.scanLeft(0)(_ + _))
    println(list.scanRight(0)(_ + _))

  }

  @Test
  def mapFoldLeft: Unit ={

  }
}



