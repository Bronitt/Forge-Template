package com.example.modid.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.Opcodes;

public class ExampleTransformer implements IClassTransformer, Opcodes {

    private static final String HOOKS = Hooks.class.getName().replace(".", "/");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        return basicClass;
    }

    public static class Hooks {}

}
