# This is the project level BUILD file, which describes a single binary built from the bazeltest package.

load("@io_bazel_rules_scala//scala:scala.bzl", "scala_binary")

scala_binary(
    name = "App",
    main_class = "bazeltest.src.main.scala.bazeltest.Main",
    scalacopts = [
        "-language:higherKinds",
        "-Ypartial-unification",
    ],
    deps = [
        "//bazeltest",
    ],
)
