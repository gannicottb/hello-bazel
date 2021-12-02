load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library")

scala_library(
    name = "bazeltest",
    srcs = glob(["src/main/scala/bazeltest/*.scala"]),
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
