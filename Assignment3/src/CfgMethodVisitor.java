import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

import java.util.*;

public class CfgMethodVisitor extends MethodVisitor {

    String methodName;
    Set<Label> jumpedToLabels;
    Map<Label, BasicBlock> blocks;
    List<Instruction> instructions;

    public CfgMethodVisitor(String name) {
        super(Opcodes.ASM5);
        methodName = name;
        jumpedToLabels = new HashSet<>();
        blocks = new HashMap<>();
        instructions = new ArrayList<>();
    }

    @Override
    public void visitLabel(Label label) {
        instructions.add(new LabelInstruction(label));
    }

    @Override
    public void visitJumpInsn(int i, Label label) {
        instructions.add(new JumpInstruction(i, label));
        jumpedToLabels.add(label);
    }

    @Override
    public void visitTableSwitchInsn(int i, int i1, Label label, Label... labels) {
        instructions.add(new TableSwitchInstruction(i, i1, label, labels));
        for(Label l : labels ) {
            jumpedToLabels.add(l);
        }
        jumpedToLabels.add(label);
    }

    @Override
    public void visitEnd() {
        blockIdx = 0;
        for(Instruction instruction : instructions) {
            if(instruction instanceof LabelInstruction) {
                LabelInstruction labelInstruction = (LabelInstruction) instruction;
                visitLabel(labelInstruction);
            } else if(instruction instanceof JumpInstruction) {
                JumpInstruction jumpInstruction = (JumpInstruction) instruction;
                visitJump(jumpInstruction);
            } else if(instruction instanceof TableSwitchInstruction) {
                TableSwitchInstruction switchInstruction = (TableSwitchInstruction) instruction;
                visitSwitch(switchInstruction);
            }
            prevInstruction = instruction;
        }
        List<BasicBlock> blockList = new ArrayList<>(blocks.values());
        Collections.sort(blockList, (b1, b2) -> b1.blockId - b2.blockId);
        for(BasicBlock block : blockList) {
            List<BasicBlock> connections = new ArrayList<>(block.connections);
            Collections.sort(connections, (b1, b2) -> b1.blockId - b2.blockId);
            System.out.println(block + ": " + connections);
        }
    }

    BasicBlock prevBlock;
    Instruction prevInstruction;
    int blockIdx;

    public void visitLabel(LabelInstruction instruction) {
        Label label = instruction.label;
        if(!jumpedToLabels.contains(label) && prevInstruction instanceof LabelInstruction) {
            return;
        }
        if(!blocks.containsKey(label)) {
            BasicBlock block = new BasicBlock(label);
            blocks.put(label, block);
        }
        BasicBlock block = blocks.get(label);
        block.blockId = blockIdx++;
        if(prevBlock != null && !((prevInstruction instanceof JumpInstruction) && ((JumpInstruction)prevInstruction).opcode == Opcodes.GOTO)) {
            prevBlock.connections.add(block);
        }
        prevBlock = block;
    }

    public void visitJump(JumpInstruction instruction) {
        Label label = instruction.label;
        if(!blocks.containsKey(label)) {
            BasicBlock block = new BasicBlock(label);
            block.jumpedTo = true;
            blocks.put(label, block);
        }
        if(prevBlock != null) {
            prevBlock.connections.add(blocks.get(label));
        }
    }

    public void visitSwitch(TableSwitchInstruction instruction) {
        if(prevBlock == null) {
            return;
        }
        for(Label label : instruction.labels) {
            if(!blocks.containsKey(label)) {
                blocks.put(label, new BasicBlock(label));
            }
            BasicBlock block = blocks.get(label);
            block.jumpedTo = true;
            prevBlock.connections.add(blocks.get(label));
        }
        Label label = instruction.label;
        if(!blocks.containsKey(label)) {
            blocks.put(label, new BasicBlock(label));
        }
        BasicBlock block = blocks.get(label);
        block.jumpedTo = true;
        prevBlock.connections.add(blocks.get(label));
    }
}
