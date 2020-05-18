package com.videlov.rdeps;

import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Options options = new Options();
        options.addOption(
                Option.builder("j")
                        .longOpt("target-jar")
                        .hasArg(true)
                        .desc("path to .jar archive to scan")
                        .required(true)
                        .build());
        options.addOption(
                Option.builder("c")
                        .longOpt("class")
                        .hasArg(true)
                        .desc(
                                "class containing the target method in the format: com.example.MyClass")
                        .required(true)
                        .build());
        options.addOption(
                Option.builder("m")
                        .longOpt("method")
                        .hasArg(true)
                        .desc(
                                "method to search for in the format: methodName(java.lang.String, double)")
                        .required(true)
                        .build());
        options.addOption(
                Option.builder("r")
                        .longOpt("return")
                        .hasArg(true)
                        .desc(
                                "return type of the target method in the format com.example.ReturnType or void")
                        .required(true)
                        .build());
        options.addOption(
                Option.builder("f")
                        .longOpt("filter")
                        .hasArg(true)
                        .desc("filter prefix for classes to be searched in the format: com.example")
                        .required(false)
                        .build());
        options.addOption(
                Option.builder("g")
                        .longOpt("graph")
                        .hasArg(false)
                        .desc("prints the graph of reverse dependencies in a dot format")
                        .required(false)
                        .build());
        options.addOption(
                Option.builder("l")
                        .longOpt("leaves")
                        .hasArg(false)
                        .desc("prints the leaf nodes of the reverse dependency graph")
                        .required(false)
                        .build());

        try {
            DefaultParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            String jarPath = cmd.getOptionValue("j");
            String targetClass = cmd.getOptionValue("c").replaceAll("\\.", "/");
            String targetMethod = cmd.getOptionValue("m").replaceAll("\\.", "/");
            String methodName = targetMethod.split("[()]")[0].replaceAll("\\\\", "");
            List<String> paramTypes = Arrays.asList(targetMethod.split("[()]")[1].split("\\s+"));
            String returnType = cmd.getOptionValue("r").replaceAll("\\.", "/");
            String prefix;
            if (cmd.hasOption("f")) {
                prefix = cmd.getOptionValue("f").replaceAll("\\.", "/");
            } else {
                prefix = "";
            }
            String desc = TypeDescriptors.parseFrom(paramTypes, returnType);
            Rdeps d = new Rdeps(jarPath, prefix);
            d.work(targetClass, methodName, desc, cmd);
        } catch (ParseException e) {
            System.err.println("Error parsing command line arguments.");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(
                    "To generate the reverse dependencies a method provide the path to the jar archive to analyze as well as the target class and method.",
                    options);
            System.exit(1);
        }
    }
}
