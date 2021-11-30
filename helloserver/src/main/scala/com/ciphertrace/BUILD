load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library")

scala_library(
    name = "helloserver",
    srcs = glob(["src/main/scala/com/ciphertrace/*.scala"]),
    plugins = [],
#    visibility = [
#        "//:__pkg__",
#        "//3rdparty/jvm:__subpackages__"
#    ],
    visibility = [
        "//visibility:public",
    ],
    deps = [
        "@third_party//3rdparty/jvm/co/fs2:fs2_core",
        "@third_party//3rdparty/jvm/io/circe:circe_core",
        "@third_party//3rdparty/jvm/io/circe:circe_generic",
        "@third_party//3rdparty/jvm/org/typelevel:cats_core",
        "@third_party//3rdparty/jvm/org/typelevel:cats_effect",
        "@third_party//3rdparty/jvm/org/typelevel:cats_effect_kernel",
        "@third_party//3rdparty/jvm/org/typelevel:cats_effect_std",
        "@third_party//3rdparty/jvm/org/typelevel:cats_kernel",
        "@third_party//3rdparty/jvm/org/http4s:http4s_ember_client",
        "@third_party//3rdparty/jvm/org/http4s:http4s_ember_server",
        "@third_party//3rdparty/jvm/org/http4s:http4s_circe",
        "@third_party//3rdparty/jvm/org/http4s:http4s_dsl",
        "@third_party//3rdparty/jvm/org/http4s:http4s_core",
        "@third_party//3rdparty/jvm/org/http4s:http4s_client",
        "@third_party//3rdparty/jvm/org/http4s:http4s_server",
        "@third_party//3rdparty/jvm/com/comcast:ip4s_core",
    ],
)