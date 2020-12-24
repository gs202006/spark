package com.atguigu.sparkpractice

import scala.collection.mutable
import org.junit.Test

import scala.io.Source

@Test
class MapTest {
  @Test
  def mapFoldLeft: Unit = {
    val map1 = mutable.Map(("a", 1), ("b", 1))
    val map2 = mutable.Map(("a", 3), ("c", 4))

    val foldedMap = map1.foldLeft(map2)(
      (map, kv) => {
        val k = kv._1
        val v = kv._2
        map.updated(k, map.getOrElse(k, 0) + v)

      }
    )
    println(foldedMap)
  }

  @Test
  def listMap: Unit = {
    val list = List(1, 2, 3, 4)
    val list1 = List("gaosen", "songqingshu", "you")

    println(list.map(_ * 2))
    println(list1.map(_.substring(0, 1).toUpperCase))

  }

  //扁平化
  @Test
  def flattenTest: Unit = {
    val list = List(List(1, 2, 3, 4), List(13, 4), List(2, 4, 4))
    println(list.flatten)
  }

  @Test
  def filterTest: Unit = {
    val list = List(1, 2, 3, 4)
    println(list.filter(_ % 2 != 0))


    val list1 = List("hadoop", "hive", "hbae", "spark")
    println(list1.filter(_.contains("h")))

  }

  @Test
  def groupBy: Unit = {
    val list = List(1, 2, 3, 4)
    val list1 = List("hadoop", "hive", "hbae", "spark", "scala")
    println(list.groupBy(_ % 2))

    println(list1.groupBy(_.substring(0, 1)))
  }

  @Test
  def sortBy: Unit = {
    val list = List(1, 2, 3, 4)
    println(list.sortBy(s => s))
  }

  @Test
  def sortByDefine: Unit = {
    val user1 = new User()
    user1.salary = 100
    user1.age = 20
    val user2 = new User()
    user2.salary = 100
    user2.age = 20
    val user3 = new User()
    user3.salary = 100
    user3.age = 20

    val users = List(user1, user2, user3)
    println(users.sortBy(_.salary))
    val lis = users.sortWith(
      (user1, user2) => {
        if (user1.salary < user2.salary) {
          true
        } else if (user1.salary == user2.salary) {
          user1.age > user2.age
        } else {
          false
        }
      }

    )
    println(lis)
  }


  @Test
  def worldCount: Unit = {
    val source = Source.fromFile("D:\\MyWork\\Workspace\\spark_0820\\input\\word.txt")
    val list = source.getLines().toList.flatMap(_.split(" "))
    val countMap = list.groupBy(s => s).map(
      t => {
        (t._1, t._2.size)
      }
    )
    val listSortTuples = countMap.toList.sortBy(_._2)(Ordering.Int.reverse)
    println(listSortTuples)
    source.close()
  }

  @Test
  def worldCout2: Unit = {
    println(1)
  }


  @Test
  def matchTest: Unit ={

  }
















  class User {
    var salary: Int = _
    var age: Int = _

    override def toString: String = {

      s"User[${salary},${age}]"
    }
  }

}