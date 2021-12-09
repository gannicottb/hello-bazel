# Motivation

Learn about how Bazel works, specifically for building Scala apps.
[Bazel IJ plugin docs](https://ij.bazel.build/)
[scala_binary rules docs (scalac opts)](https://github.com/bazelbuild/rules_scala/blob/master/docs/scala_binary.md)
[example sbt-to-bazel repo](https://github.com/stripe-archive/sbt-bazel)
[migrating to bazel](https://medium.com/wix-engineering/migrating-to-bazel-from-maven-or-gradle-5-crucial-questions-you-should-ask-yourself-f23ac6bca070)

# TODO
* Why does helloserver fail on my Mac?
* Customize docker images (translate _some_ tweaks from existing Dockerfiles)
* Push docker images to local repo/external repo
* Determine the right approach for transitive dep declaration 
* Share scala version between bazel-deps and WORKSPACE
* Scala 3
* Find equivalent of addCompilerPlugin
  * In order to apply project wide, must [Reimplement via toolchain](https://github.com/bazelbuild/rules_scala/blob/master/docs/scala_toolchain.md)
* Evaluate twitter/multiversion to allow for more flexible versioning [multiversion github](https://github.com/twitter/bazel-multiversion)
  * We might not need this right away for clusterrisk, but we'd get into trouble when we tried to onboard the rest of the Scala apps

# TODONE
* Build Docker image via Bazel rules
* What is the difference between scala_library, scala_binary, scala_toolchain?
  * scala_toolchain defines global build configuration for all Scala targets
  * scala_library defines a module
  * scala_binary outputs an executable script that runs a .jar - you have to provide a main_class
* scalafmt
  * Done via Intellij plugin and appropriate .scalafmt.conf. Not really a Bazel issue.
* Find equivalent of addCompilerPlugin
  * Solved for a single target via [undocumented plugins attribute](https://github.com/bazelbuild/rules_scala/blob/master/test/src/main/scala/scalarules/test/compiler_plugin/BUILD.bazel)

# Demo
Install bazelisk.

Run bazel-deps script to generate BUILD files for our dependencies: 

`scripts/update_dependencies.sh`

Build the :App target with Bazel: 

`bazel build //bazeltest:App`

Run the :App target with Bazel: 

`bazel run //bazeltest:App`

You can do the same with //helloserver:HelloServer to see an http4s server in action.

Package either target in a Docker image and then run with:

`bazel run //bazeltest:AppImage` or `bazel run //helloserver:HelloServerImage`

OR (recommended)

[Install bazel plugin for IJ.](https://plugins.jetbrains.com/plugin/8609-bazel)

You can then set up a run configuration like:
* target expression = //:App
* Bazel command = run

# Updating dependencies
To update dependencies, make changes to dependencies.yaml then run `scripts/update_dependencies.sh`

ALSO update your deps in the relevant BUILD file!

# Initial Approach

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

[This issue specifically talks about the output_jar problem](https://github.com/bazelbuild/bazel/issues/12373)

[Allegedly rules_scala is up to date with it](https://github.com/bazelbuild/rules_scala/pull/1314)

Wait, success.

.bazelversion set to 6.0.0-pre.20211101.2, and using rules_scala_version = "17791a18aa966cdf2babb004822e6c70a7decc76", commenting out the sha

# Add compiler plugin

[Scala-lang docs](https://docs.scala-lang.org/overviews/plugins/index.html)
> addCompilerPlugin performs multiple actions. It adds the JAR to the classpath (the compilation classpath only, not the runtime classpath) via libraryDependencies, and it also customizes scalacOptions to enable the plugin using -Xplugin.

So first step is to download the jar as normal. Going with better-monadic-for (which I think is unneeded in scala 3, but will work as an example)

Added to dependencies.yaml, and then loaded to a specific target via 
```
 plugins = [
        "@third_party//3rdparty/jvm/com/olegpy:better_monadic_for",
    ],
```
NOTE: this `plugins` key is undocumented!

Which works, but IDEA of course doesn't know about it and will flag the code as invalid.

# Listing deps

The default transitivity setting (runtimeDeps) in dependencies.yaml led to (imo) excessive tracking down of transitive deps.

For example, if you need cats-effect, you end up needing cats-core, cats-kernel, cats-kernel-effect, cats-effect-std, etc, all explicitly declared in dependencies and asked for in the app that wanted cats-effect. 

This might have correctness benefits but it's a hard sell. Basically, bazel-deps will find the extra things we need, but with `transitivity: runtimeDeps` then it's impossible to make them available on the classpath without promoting them to an explicit include in two files (dependencies.yaml and BUILD)

From [bazel docs](https://docs.bazel.build/versions/main/build-ref.html#actual_and_declared_dependencies)
> What this means for BUILD file writers is that every rule must explicitly declare all of its actual direct dependencies to the build system, and no more. Failure to observe this principle causes undefined behavior: the build may fail, but worse, the build may depend on some prior operations, or upon transitive declared dependencies the target happens to have. The build tool attempts aggressively to check for missing dependencies and report errors, but it is not possible for this checking to be complete in all cases.
> You need not (and should not) attempt to list everything indirectly imported, even if it is "needed" by A at execution time.
> Dependencies should be restricted to direct dependencies (dependencies needed by the sources listed in the rule). Do not list transitive dependencies.

From [java_library docs](https://docs.bazel.build/versions/main/be/java.html)
> `exports`: Exported libraries.
> Listing rules here will make them available to parent rules, as if the parents explicitly depended on these rules. This is not true for regular (non-exported) deps.
> Summary: a rule X can access the code in Y if there exists a dependency path between them that begins with a deps edge followed by zero or more exports edges. Let's see some examples to illustrate this.
> Assume A depends on B and B depends on C. In this case C is a transitive dependency of A, so changing C's sources and rebuilding A will correctly rebuild everything. However A will not be able to use classes in C. To allow that, either A has to declare C in its deps, or B can make it easier for A (and anything that may depend on A) by declaring C in its (B's) exports attribute.
> The closure of exported libraries is available to all direct parent rules. Take a slightly different example: A depends on B, B depends on C and D, and also exports C but not D. Now A has access to C but not to D. Now, if C and D exported some libraries, C' and D' respectively, A could only access C' but not D'.
> Important: an exported rule is not a regular dependency. Sticking to the previous example, if B exports C and wants to also use C, it has to also list it in its own deps.

I'm assuming that the `exports` attribute in dependencies.yaml eventually boils down to something analogous to this.
Ah, here it is. [scala_rules docs](https://github.com/bazelbuild/rules_scala/blob/master/docs/scala_library.md)
> `exports`: List of labels, optional
> List of targets to add to the dependencies of those that depend on this target. Similar to the `java_library` parameter of the same name. Use this sparingly as it weakens the precision of the build graph. These must be jvm targets (scala_library, java_library, java_import, etc...)

Still not totally sure if there's a happy medium between
 * Setting transitivity in dependencies.yaml to `exports` which works like magic but maybe(?) is too broad
 * Setting transitivity in dependencies.yaml to `runtimeDeps` which ends up requiring promoting most if not all transitive deps to be full dependencies

Ultimately the first option is far more ergonomic, but I think what it does is list each dependency's dependencies as `exports`, which the docs say to use sparingly.
We're basically using it as much as possible in the first option. Which is probably why everything just works :P
At the same time, the docs for `runtime_deps` (as an attribute on scala_library) have this to say:
> List of other libraries to put on the classpath only at runtime. This is rarely needed in Scala. These must be jvm targets (scala_library, java_library, java_import, etc...)

Discussion on this subject perhaps ongoing here [in this issue](https://github.com/johnynek/bazel-deps/issues/301)