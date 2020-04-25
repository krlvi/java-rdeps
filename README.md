# java-rdeps

Given a Java method, `java-rdeps` evaluates its transitive closure of reverse dependencies. It prints the resulting call graph to standard out in DOT format.

The functionality of `java-rdeps` is similar to the "Method Hierarchy" tool available in the IntelliJ IDE, with the major difference being that `java-rdeps` eagerly computes the entire call tree.

This tool is used for visualizing or measuring the usage of methods within a codebase, aiding refactoring efforts.

## Usage
This project is built with [Bazel](https://bazel.build). To build this tool, perform `bazel build rdeps`. To run the application through Bazel, perform `bazel run rdeps --` followed by the appropriate arguments as outlined below:
```
To generate the reverse dependencies a method provide the path to
the jar archive to analyze as well as the target class and method.
 -c,--class <arg>        class containing the target method in the format:
                         com.example.MyClass
 -f,--filter <arg>       filter prefix for classes to be searched in the
                         format: com.example
 -g,--graph              prints the graph of reverse dependencies in a dot
                         format
 -j,--target-jar <arg>   path to .jar archive to scan
 -l,--leaves             prints the leaf nodes of the reverse dependency
                         graph
 -m,--method <arg>       method to search for in the format:
                         methodName(java.lang.String, double)
 -r,--return <arg>       return type of the target method in the format
                         com.example.ReturnType or void
```
## Example
Below is an example usage of the `rdeps` tool, searching for the transitive usage of Guava's `com.google.common.hash.Hasher.putByte(byte)` within the library itself.
```
$ java -jar rdeps.jar \
    --target-jar ~/guava-29.0-jre.jar \
    --class com.google.common.hash.Hasher \
    --method "putByte(byte)" \
    --return com.google.common.hash.Hasher \
    --graph

strict digraph G {
  "com.google.common.hash\nHasher\nputByte";
  "com.google.common.hash\nAbstractCompositeHashFunction$1\nputByte:78";
  "com.google.common.hash\nHasher\nputByte:55";
  "com.google.common.hash\nHashingInputStream\nread:57";
  "com.google.common.hash\nAbstractCompositeHashFunction$1\nputByte:72";
  "com.google.common.hash\nHashingOutputStream\nwrite:53";
  "com.google.common.hash\nAbstractCompositeHashFunction$1\nputByte:78" -> "com.google.common.hash\nHasher\nputByte";
  "com.google.common.hash\nHashingInputStream\nread:57" -> "com.google.common.hash\nHasher\nputByte";
  "com.google.common.hash\nHasher\nputByte:55" -> "com.google.common.hash\nHasher\nputByte";
  "com.google.common.hash\nAbstractCompositeHashFunction$1\nputByte:72" -> "com.google.common.hash\nAbstractCompositeHashFunction$1\nputByte:78";
  "com.google.common.hash\nHashingOutputStream\nwrite:53" -> "com.google.common.hash\nHasher\nputByte";
}
```

This of course composes well with other tools, for instance using the Graphviz `dot` a user can generate a visual representation of the graph.

```
$ java -jar rdeps.jar \
    --target-jar ~/guava-29.0-jre.jar \
    --class com.google.common.hash.Hasher \
    --method "putByte(byte)" \
    --return com.google.common.hash.Hasher \
    --graph \
 | dot -Tpng > putByte.png
```
The command produces the following visualization:
![putByte graph](./examples/putByte.png?raw=true)

## Performance
As per the current implementation, calculating the full transitive closure of a method within a .jar archive has asymptotic complexity of `O(nm)` where `n` is the total number of methods in the jar and `m` is the number of transitive usages of the target method.

This means that if you are analyzing a particularly large .jar, or the target method has an extreme number of indirect usages, `rdeps` may take minutes to complete.

In such cases, consider using the `--filter` flag which is used to limit the search space to classes which match the package prefix provided.
