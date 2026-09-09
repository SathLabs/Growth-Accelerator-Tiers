package dev.satherov.growthacceleratortiers.api.ids;

import dev.satherov.growthacceleratortiers.api.GAT;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public class GATCreativeTabIds {
    
    public static final ResourceKey<CreativeModeTab> MAIN = GATCreativeTabIds.create("main");
    
    private static ResourceKey<CreativeModeTab> create(String path) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, GAT.rl(path));
    }
}
