load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library")

scala_library(
    name = "helloserver",
    srcs = glob(["src/main/scala/com/ciphertrace/*.scala"]),
    plugins = [
        "@third_party//com/olegpy:better_monadic_for",
        "@third_party//org/typelevel:kind_projector_2_13_7",
    ],
    unused_dependency_checker_mode = "error",
    visibility = [
        "//visibility:public",
    ],
    deps = [
        "@third_party//io/circe:circe_generic",
        "@third_party//org/http4s:http4s_circe",
        "@third_party//org/http4s:http4s_dsl",
        "@third_party//org/http4s:http4s_ember_client",
        "@third_party//org/http4s:http4s_ember_server",
        "@third_party//org/typelevel:cats_effect",
    ],
)
