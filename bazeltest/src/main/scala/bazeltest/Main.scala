package bazeltest

import io.circe.syntax._
import io.circe.generic.auto._
import cats.effect.{IO, IOApp}
import cats.effect.std.Random
import cats.implicits._

import scala.concurrent.duration._

object Main extends IOApp.Simple {

  // This section compiles because kind-projector is available
  def baz[T[_, _]] = ()
  baz[Tuple3[Int, *, *]]

  case class Bazel(
      msg: String
  )

  def sleepPrint(word: String, name: String, rand: Random[IO]): IO[Unit] =
    for {
      delay <- rand.betweenInt(200, 700)
      _ <- IO.sleep(delay.millis)
      json = Bazel(s"$word, $name").asJson
      _ <- IO.println(json.noSpaces)
    } yield ()

  val run: IO[Unit] =
    for {
      rand <- Random.scalaUtilRandom[IO]

      name <- IO.pure("Daniel")

      english <- sleepPrint("Hello", name, rand).foreverM.start
      french <- sleepPrint("Bonjour", name, rand).foreverM.start
      spanish <- sleepPrint("Hola", name, rand).foreverM.start

      _ <- IO.sleep(5.seconds)
      _ <- english.cancel >> french.cancel >> spanish.cancel
      (x, y) <- IO.pure((1, 2)) // This requires better-monadic-for
      _ <- IO.println(s"$x and $y")
      _ <- IO.println("demo complete")
    } yield ()
}
