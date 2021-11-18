# Motivation

Learn about how Bazel works, specifically for building Scala apps.

# Approach

Build a tiny app and get Bazel working with it.

I tried to follow two guides
1) https://scalac.io/blog/set-up-bazel-build-tool-for-scala-project/
2) https://github.com/avibryant/bazel-scala-example

But both are fairly outdated (2 years and 4 years old, respectively).

So I'm mostly patching things together from [rules_scala Getting Started](https://github.com/bazelbuild/rules_scala#getting-started)

The chief blocker I've run into is that I'm on Fedora 35, which ships with gcc 11. AFAIK, the current stable Bazel release (4.2.1) is incompatible with gcc 11, as described in [this issue](https://github.com/bazelbuild/bazel/issues/12702)

Specifically, the error it spits out is:
```
ERROR: /home/brandon/.cache/bazel/_bazel_brandon/c30a88b022d22c518b273d3fb7fb5211/external/io_bazel_rules_scala/third_party/java_tools/ijar/BUILD:11:11: Compiling third_party/java_tools/ijar/mapped_file_unix.cc failed: (Exit 1): gcc failed: error executing command /usr/bin/gcc -U_FORTIFY_SOURCE -fstack-protector -Wall -Wunused-but-set-parameter -Wno-free-nonheap-object -fno-omit-frame-pointer -g0 -O2 '-D_FORTIFY_SOURCE=1' -DNDEBUG -ffunction-sections ... (remaining 26 argument(s) skipped)

Use --sandbox_debug to see verbose messages from the sandbox
external/io_bazel_rules_scala/third_party/java_tools/ijar/mapped_file_unix.cc: In constructor 'devtools_ijar::MappedOutputFile::MappedOutputFile(const char*, size_t)':
external/io_bazel_rules_scala/third_party/java_tools/ijar/mapped_file_unix.cc:115:21: error: 'numeric_limits' is not a member of 'std'
  115 |                std::numeric_limits<size_t>::max());
      |                     ^~~~~~~~~~~~~~
external/io_bazel_rules_scala/third_party/java_tools/ijar/mapped_file_unix.cc:115:42: error: expected primary-expression before '>' token
  115 |                std::numeric_limits<size_t>::max());
      |                                          ^
external/io_bazel_rules_scala/third_party/java_tools/ijar/mapped_file_unix.cc:115:45: error: '::max' has not been declared; did you mean 'std::max'?
  115 |                std::numeric_limits<size_t>::max());
      |                                             ^~~
      |                                             std::max
In file included from /usr/lib/gcc/x86_64-redhat-linux/11/../../../../include/c++/11/algorithm:62,
                 from external/io_bazel_rules_scala/third_party/java_tools/ijar/mapped_file_unix.cc:21:
/usr/lib/gcc/x86_64-redhat-linux/11/../../../../include/c++/11/bits/stl_algo.h:3467:5: note: 'std::max' declared here
 3467 |     max(initializer_list<_Tp> __l, _Compare __comp)
      |     ^~~
Target //:App failed to build
```

I installed gcc@10 with brew, but have had no luck getting bazel to use it. I added `alias gcc="gcc-10"` to my .zshrc, and `gcc --version` does spit out 10.x, but the error remains. I think the alias won't work because `usr/bin/gcc` is called directly.

I tried going the other way, updating my bazel to a version that's patched the issue with gcc11. I did this by adding a .bazelversion file listing the latest tag.

This got me past the gcc error, and into this one:
```
ERROR: /home/brandon/.cache/bazel/_bazel_brandon/c30a88b022d22c518b273d3fb7fb5211/external/io_bazel_rules_scala/third_party/dependency_analyzer/src/main/BUILD:6:39: in scala_library_for_plugin_bootstrapping rule @io_bazel_rules_scala//third_party/dependency_analyzer/src/main:scala_version: 
Traceback (most recent call last):
        File "/home/brandon/.cache/bazel/_bazel_brandon/c30a88b022d22c518b273d3fb7fb5211/external/io_bazel_rules_scala/scala/private/rules/scala_library.bzl", line 137, column 22, in _scala_library_for_plugin_bootstrapping_impl
                return run_phases(
        File "/home/brandon/.cache/bazel/_bazel_brandon/c30a88b022d22c518b273d3fb7fb5211/external/io_bazel_rules_scala/scala/private/phases/api.bzl", line 45, column 23, in run_phases
                return _run_phases(ctx, builtin_customizable_phases, target = None)
        File "/home/brandon/.cache/bazel/_bazel_brandon/c30a88b022d22c518b273d3fb7fb5211/external/io_bazel_rules_scala/scala/private/phases/api.bzl", line 77, column 32, in _run_phases
                new_provider = function(ctx, current_provider)
        File "/home/brandon/.cache/bazel/_bazel_brandon/c30a88b022d22c518b273d3fb7fb5211/external/io_bazel_rules_scala/scala/private/phases/phase_compile.bzl", line 47, column 34, in phase_compile_library_for_plugin_bootstrapping
                return _phase_compile_default(ctx, p, args)
        File "/home/brandon/.cache/bazel/_bazel_brandon/c30a88b022d22c518b273d3fb7fb5211/external/io_bazel_rules_scala/scala/private/phases/phase_compile.bzl", line 105, column 26, in _phase_compile_default
                return _phase_compile(
        File "/home/brandon/.cache/bazel/_bazel_brandon/c30a88b022d22c518b273d3fb7fb5211/external/io_bazel_rules_scala/scala/private/phases/phase_compile.bzl", line 130, column 28, in _phase_compile
                out = _compile_or_empty(
        File "/home/brandon/.cache/bazel/_bazel_brandon/c30a88b022d22c518b273d3fb7fb5211/external/io_bazel_rules_scala/scala/private/phases/phase_compile.bzl", line 224, column 38, in _compile_or_empty
                source_jar = _pack_source_jar(ctx, scala_srcs, in_srcjars)
        File "/home/brandon/.cache/bazel/_bazel_brandon/c30a88b022d22c518b273d3fb7fb5211/external/io_bazel_rules_scala/scala/private/phases/phase_compile.bzl", line 306, column 36, in _pack_source_jar
                return java_common.pack_sources(
Error in pack_sources: in call to pack_sources(), parameter 'output_jar' is deprecated and will be removed soon. It may be temporarily re-enabled by setting --incompatible_java_common_parameters=false
```

In theory, adding `--incompatible_java_common_parameters=false` to the build call should allow me to get past this as well. But that flag isn't listed in the [Bazel docs](https://docs.bazel.build/versions/main/command-line-reference.html#build) and besides, all it does it is take us back to the gcc error, somehow.

`bazel build --incompatible_java_common_parameters=false :App`

Hmm maybe I'm supposed to use bazelisk instead of bazel?

`bazelisk build :App`

Nah, same result.

[This issue specifically talks about the output_jar problem](https://github.com/bazelbuild/bazel/issues/12373)
[Allegedly rules_scala is up to date with it](https://github.com/bazelbuild/rules_scala/pull/1314)

Wait, success.

.bazelversion set to 6.0.0-pre.20211101.2, and using rules_scala_version = "17791a18aa966cdf2babb004822e6c70a7decc76", commenting out the sha

