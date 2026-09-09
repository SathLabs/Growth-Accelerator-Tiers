package dev.satherov.growthacceleratortiers.api;

import net.minecraft.resources.ResourceLocation;

public class GAT {
    
    public static final String MOD_ID = "growthacceleratortiers";
    
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(GAT.MOD_ID, path);
    }
}
