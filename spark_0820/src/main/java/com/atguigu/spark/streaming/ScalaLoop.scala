object ScalaLoop {
  def main(args: Array[String]): Unit = {






    for ( i <- Range(1,5) ) { // 范围集合
      print("i =" + i+"  " )
    }
    println("++++++++++++++++++++++++++")

    for ( i <- 1 to 5 ) { // 包含5
      print("i =" + i+"  " )
    }
    println("++++++++++++++++++++++++++")

    for ( i <- 1 until 5 ) { // 不包含5
      print("i =" + i +"  ")
    }

    println("++++++++++++++++++++++++++")
    def fun1(): String = {
      "zhangsan"
    }
    val a = fun1
    val b = fun1 _
    println(a)
    println(b)

    def fun2( i:Int ): Int = {
      i * 2
    }
    def fun22( f : Int => Int ): Int = {
      f(10)
    }
    println(fun22(fun2))


    def fun( s : String ): Unit = {
      println(s)
    }
    def test( f : (String)=>Unit ): Unit = {
      f("lisi")
    }
    test( fun _ )
    fun("11")

    test( fun )



  }
}
