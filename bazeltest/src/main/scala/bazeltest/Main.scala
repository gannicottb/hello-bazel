package bazeltest.src.main.scala.bazeltest
import io.circe.syntax._
import io.circe.generic.auto._

object Main extends App {
  case class Bazel(
                  msg: String
                  )
  val json = Bazel("Hello, Bazel!").asJson
  println(s"${json.noSpaces}")
}