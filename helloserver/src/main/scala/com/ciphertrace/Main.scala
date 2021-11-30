package helloserver.src.main.scala.com.ciphertrace

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]) =
    HelloServer.stream[IO].compile.drain.as(ExitCode.Success)
}
