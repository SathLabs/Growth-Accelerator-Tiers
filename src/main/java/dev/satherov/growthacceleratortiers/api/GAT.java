package dev.satherov.growthacceleratortiers.api;

import net.minecraft.resources.Identifier;

public class GAT {
    
    public static final String MOD_ID = "growthacceleratortiers";
    
    public static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(GAT.MOD_ID, path);
    }
}
