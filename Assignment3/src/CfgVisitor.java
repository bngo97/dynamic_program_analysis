import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;
//import org.objectweb.asm.*;
//import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class CfgVisitor extends ClassVisitor {

    public static void main(String[] args) throws IOException {
        byte[] code = Files.readAllBytes(new File("tst/TestForLoop.class").toPath());
        ClassReader reader = new ClassReader(code);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);
        MethodNode main = classNode.methods.get(1);
        InsnList instructions = main.instructions;
        Set<Label> jumpedToLabels = getJumpedToLabels(instructions);

        AbstractInsnNode prevInstruction = null;
        for (int m_i = 0; m_i < instructions.size(); m_i++) {
            AbstractInsnNode instruction = instructions.get(m_i);
            if(instruction instanceof LabelNode) {
                LabelNode labelNode = (LabelNode) instruction;
                Label label = labelNode.getLabel();
                System.out.println(label);
                prevInstruction = instruction;
            } else if(instruction instanceof JumpInsnNode) {
                JumpInsnNode node = (JumpInsnNode) instruction;
                LabelNode labelNode = node.label;
                if(node.getOpcode() == Opcodes.GOTO) {
                    System.out.println("GO TO " +  labelNode.getLabel());
                } else {
                    System.out.println("JUMP TO " + labelNode.getLabel());
                }
                prevInstruction = instruction;
            }
        }

        System.out.println();
        System.out.println("-----------------------------------");
        System.out.println();
        CfgVisitor cv = new CfgVisitor();
        reader.accept(cv, 0);
    }

    public static Set<Label> getJumpedToLabels(InsnList instructions) {
        Set<Label> jumpedToLabels = new HashSet<>();
        for(int m_i = 0; m_i < instructions.size(); m_i++) {
            AbstractInsnNode instruction = instructions.get(m_i);
            if(instruction instanceof JumpInsnNode) {
                JumpInsnNode node = (JumpInsnNode) instruction;
                LabelNode labelNode = node.label;
                jumpedToLabels.add(labelNode.getLabel());
            }
        }
        return jumpedToLabels;
    }


    public CfgVisitor() {
        super(Opcodes.ASM5);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if(!name.equals("<init>")) {
            mv = new CfgMethodVisitorV3(name);
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
