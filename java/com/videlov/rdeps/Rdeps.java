package com.videlov.rdeps;

import org.apache.commons.cli.CommandLine;
import org.jgrapht.Graph;
import org.jgrapht.graph.concurrent.AsSynchronizedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.Method;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Rdeps {
    private final String prefix;
    private final JarFile jarFile;
    private final Queue<CompletableFuture<?>> tasks = new ConcurrentLinkedQueue<>();

    Rdeps(String jarPath, String prefix) throws IOException {
        this.prefix = prefix;
        this.jarFile = new JarFile(jarPath);
    }

    public void work(String targetClass, String methodName, String methodDesc, CommandLine cmd) {
        Graph<String, DefaultEdge> graph =
                new AsSynchronizedGraph<>(new DefaultDirectedGraph<>(DefaultEdge.class));
        var nodeName = String.join(".", targetClass, methodName);
        graph.addVertex(nodeName);
        var targetMethod = new Method(methodName, methodDesc);
        findCallingMethodsInJar(targetClass, targetMethod, graph, nodeName);
        while (!tasks.isEmpty()) {
            tasks.poll().join();
        }

        if (cmd.hasOption("graph")) {
            Output.printAsDot(graph);
        }
        if (cmd.hasOption("leaves")) {
            Output.printLeafNodes(graph);
        }
    }

    public void findCallingMethodsInJar(
            String targetClass,
            Method targetMethod,
            Graph<String, DefaultEdge> graph,
            String parent) {

        List<Caller> cs = new ArrayList<>();
        var cv = new ClassAnalyzer(targetClass, targetMethod, cs);

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            var entry = entries.nextElement();

            if (entry.getName().endsWith(".class") && entry.getName().startsWith(prefix)) {
                try {
                    var stream = new BufferedInputStream(jarFile.getInputStream(entry), 1024);
                    var reader = new ClassReader(stream);
                    reader.accept(cv, 0);
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (cs.isEmpty()) {
            return;
        }
        for (Caller c : cs) {
            tasks.add(
                    CompletableFuture.runAsync(
                            () -> {
                                var name =
                                        String.join(".", c.getClassName(), c.getMethodName())
                                                + ":"
                                                + c.getLine();
                                graph.addVertex(name);
                                findCallingMethodsInJar(
                                        c.getClassName(),
                                        new Method(c.getMethodName(), c.getMethodDesc()),
                                        graph,
                                        name);
                                graph.addEdge(name, parent);
                            }));
        }
    }
}
