package com.atguigu.scala

object scala {



  def main(args: Array[String]): Unit = {
    def fun(): String = {
      "zhangsan"
    }


    def test( name : ()=>String ): Unit = {

      println(name())
    }
    test(fun _)
    //test(fun())


  }

}
// 将函数作为整体对象来使用
// 函数类型 ： Function0 & In=>Out
//val f = fun _
// 如果明确变量的类型，那么可以不使用下划线将函数作为整体使用
//val f : ()=>String = fun
//val str: String = f()

// 将函数作为对象赋值给一个变量，那么这个变量就可以任意使用
// 将这个变量作为参数传递给其他的函数