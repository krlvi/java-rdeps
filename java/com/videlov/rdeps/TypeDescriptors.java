package com.videlov.rdeps;

import java.util.List;

public class TypeDescriptors {
    private TypeDescriptors() {}

    public static String parseFrom(List<String> params, String ret) {
        var sb = new StringBuilder();
        sb.append("(");
        for (String p : params) {
            sb.append(toTypeDesc(p));
        }
        sb.append(")");
        sb.append(toTypeDesc(ret));
        return sb.toString().replaceAll("[\\\\,]", "");
    }

    private static String toTypeDesc(String t) {
        switch (t.replaceAll("\\\\", "")) {
            case "void":
                return "V";
            case "boolean":
                return "Z";
            case "char":
                return "C";
            case "byte":
                return "B";
            case "short":
                return "S";
            case "int":
                return "I";
            case "float":
                return "F";
            case "long":
                return "J";
            case "double":
                return "D";
            default:
                return "L" + t + ";";
        }
    }
}
