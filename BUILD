# This is the project level BUILD file, which describes each binary built from packages in this workspace

load("@io_bazel_rules_scala//scala:scala.bzl", "scala_binary")
load("@io_bazel_rules_docker//scala:image.bzl", "scala_image")

scala_binary(
    name = "App",
    main_class = "bazeltest.src.main.scala.bazeltest.Main",
    scalacopts = [
        "-language:higherKinds",
    ],
    deps = [
        "//bazeltest",
    ],
)

scala_image(
    name = "AppImage",
    main_class = "bazeltest.src.main.scala.bazeltest.Main",
    deps = ["//bazeltest"],
)

scala_binary(
    name = "HelloServer",
    main_class = "helloserver.src.main.scala.com.ciphertrace.Main",
    deps = [
        "//helloserver",
    ],
)

scala_image(
    name = "HelloServerImage",
    main_class = "helloserver.src.main.scala.com.ciphertrace.Main",
    deps = [
        "//helloserver",
    ],
)
