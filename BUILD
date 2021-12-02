# This is the project level BUILD file, which describes each binary built from packages in this workspace

load("@io_bazel_rules_scala//scala:scala.bzl", "scala_binary")

scala_binary(
    name = "App",
    main_class = "bazeltest.src.main.scala.bazeltest.Main",
    scalacopts = [
        "-language:higherKinds"
    ],
    deps = [
        "//bazeltest",
    ],
)

scala_binary(
    name = "HelloServer",
    main_class = "helloserver.src.main.scala.com.ciphertrace.Main",
    deps = [
        "//helloserver",
    ],
)
