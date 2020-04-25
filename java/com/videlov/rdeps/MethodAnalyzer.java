package com.videlov.rdeps;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;

import java.util.List;

public class MethodAnalyzer extends MethodVisitor {
    private final String targetClass;
    private final Method targetMethod;
    private final Caller sourceMethod;
    private final List<Caller> callers;
    private boolean callsTarget;
    private int line;

    public MethodAnalyzer(
            Caller method, String targetClass, Method targetMethod, List<Caller> callers) {
        super(Opcodes.ASM8);
        this.sourceMethod = method;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.callers = callers;
    }

    @Override
    public void visitMethodInsn(
            int opcode, String owner, String name, String desc, boolean isInterface) {
        if (owner.equals(targetClass)
                && name.equals(targetMethod.getName())
                && desc.equals(targetMethod.getDescriptor())) {
            callsTarget = true;
        }
    }

    @Override
    public void visitCode() {
        callsTarget = false;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.line = line;
    }

    @Override
    public void visitEnd() {
        if (callsTarget) {
            sourceMethod.setLine(line);
            callers.add(sourceMethod);
        }
    }
}
