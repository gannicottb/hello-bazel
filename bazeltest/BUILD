load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library")
load("@io_bazel_rules_scala//scala:scala.bzl", "scala_binary")
load("@io_bazel_rules_docker//scala:image.bzl", "scala_image")


scala_library(
    name = "bazeltest",
    srcs = glob(["src/main/scala/**/*.scala"]),
    plugins = [
        "@third_party//com/olegpy:better_monadic_for",
        "@third_party//org/typelevel:kind_projector_2_13_7",
    ],
    unused_dependency_checker_mode = "error",
    visibility = ["//:__pkg__"],
    deps = [
        "@third_party//io/circe:circe_core",
        "@third_party//io/circe:circe_generic",
        "@third_party//org/typelevel:cats_core",
        "@third_party//org/typelevel:cats_effect",
    ],
)

# Binary and Docker image for bazeltest.Main, which I'm calling "App" for some reason
scala_binary(
    name = "App",
    main_class = "bazeltest.Main",
    scalacopts = [
        "-language:higherKinds",
    ],
    deps = [
        "//bazeltest",
    ],
)

scala_image(
    name = "AppImage",
    main_class = "bazeltest.Main",
    deps = ["//bazeltest"],
)