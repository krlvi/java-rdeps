java_binary(
    name = "rdeps",
    srcs = glob(["**/*.java"]),
    main_class = "com.videlov.rdeps.Main",
    visibility = ["//visibility:public"],
    deps = [
        "@maven//:commons_cli_commons_cli",
        "@maven//:org_jgrapht_jgrapht_core",
        "@maven//:org_jgrapht_jgrapht_io",
        "@maven//:org_ow2_asm_asm",
        "@maven//:org_ow2_asm_asm_commons",
    ],
)
