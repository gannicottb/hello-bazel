load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library")
load("@io_bazel_rules_scala//scala:scala.bzl", "scala_binary")
load("@io_bazel_rules_docker//scala:image.bzl", "scala_image")

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

# Binary and Docker image for helloserver
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
