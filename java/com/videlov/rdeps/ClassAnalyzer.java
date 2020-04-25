package com.videlov.rdeps;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;

import java.util.List;

public class ClassAnalyzer extends ClassVisitor {
    private final String targetClass;
    private final Method targetMethod;
    private final List<Caller> callers;

    private String source;
    private String className;

    public ClassAnalyzer(String targetClass, Method targetMethod, List<Caller> callers) {
        super(Opcodes.ASM8, new ClassWriter(Opcodes.ASM8));
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.callers = callers;
    }

    @Override
    public void visit(
            int version,
            int access,
            String name,
            String signature,
            String superName,
            String[] interfaces) {
        className = name;
    }

    @Override
    public void visitSource(String source, String debug) {
        this.source = source;
    }

    @Override
    public MethodVisitor visitMethod(
            int access, String name, String desc, String signature, String[] exceptions) {
        return new MethodAnalyzer(
                new Caller(className, name, desc, source), targetClass, targetMethod, callers);
    }
}
