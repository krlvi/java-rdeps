package com.videlov.rdeps;

public class Caller {
    private final String className;
    private final String methodName;
    private final String methodDesc;
    private final String source;
    private int line;

    public Caller(String className, String methodName, String methodDesc, String source) {
        this.className = className;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.source = source;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public String getSource() {
        return source;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
