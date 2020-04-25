package com.videlov.rdeps;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.dot.DOTExporter;

public class Output {
    private Output() {}

    public static void printAsDot(Graph<String, DefaultEdge> g) {
        var de =
                new DOTExporter<String, DefaultEdge>(
                        x -> {
                            StringBuilder sb = new StringBuilder("\"");
                            String s = x;
                            s = replaceLast(s, "/", "\\\\n");
                            s = s.replace("/", ".");
                            s = replaceLast(s, "[.]", "\\\\n");
                            sb.append(s);
                            sb.append("\"");
                            return sb.toString();
                        });
        de.exportGraph(g, System.out);
    }

    private static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    public static void printLeafNodes(Graph<String, DefaultEdge> graph) {
        graph.vertexSet().stream()
                .filter(v -> graph.inDegreeOf(v) == 0)
                .forEach(System.out::println);
    }
}
