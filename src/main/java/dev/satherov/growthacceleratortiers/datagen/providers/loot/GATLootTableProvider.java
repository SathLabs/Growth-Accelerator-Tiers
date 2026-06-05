package dev.satherov.growthacceleratortiers.datagen.providers.loot;

import dev.satherov.growthacceleratortiers.core.annotations.NothingNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@NothingNull
public class GATLootTableProvider extends LootTableProvider {
    
    private static final List<SubProviderEntry> SUB_PROVIDERS = List.of(
            new SubProviderEntry(GATBlockDropProvider::new, LootContextParamSets.BLOCK)
    );
    
    public GATLootTableProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(packOutput, Set.of(), GATLootTableProvider.SUB_PROVIDERS, provider);
    }
}
