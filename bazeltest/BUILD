load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library")

scala_library(
    name = "bazeltest",
    srcs = glob(["src/main/scala/bazeltest/*.scala"]),
    visibility = ["//:__pkg__"],
    deps = [
        "@third_party//3rdparty/jvm/io/circe:circe_core",
        "@third_party//3rdparty/jvm/io/circe:circe_generic",
        "@third_party//3rdparty/jvm/org/typelevel:cats_core",
        "@third_party//3rdparty/jvm/org/typelevel:cats_effect",
        "@third_party//3rdparty/jvm/org/typelevel:cats_effect_kernel",
        "@third_party//3rdparty/jvm/org/typelevel:cats_effect_std",
        "@third_party//3rdparty/jvm/org/typelevel:cats_kernel",
    ],
)
