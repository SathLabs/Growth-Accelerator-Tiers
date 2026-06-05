package dev.satherov.growthacceleratortiers.datagen.providers.loot;

import dev.satherov.growthacceleratortiers.api.GAT;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GATBlockDropProvider extends BlockLootSubProvider {
    
    public GATBlockDropProvider(HolderLookup.Provider providers) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), providers);
    }
    
    @Override
    protected void generate() {
        for (var block : this.getKnownBlocks()) {
            this.add(block, this.defaultBuilder(block));
        }
    }
    
    private LootTable.Builder defaultBuilder(Block block) {
        LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(block);
        LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry)
                .when(ExplosionCondition.survivesExplosion());
        
        return LootTable.lootTable().withPool(pool);
    }
    
    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK
                .stream()
                .filter(entry -> entry.getLootTable().isPresent() && entry.getLootTable().get().identifier().getNamespace().equals(GAT.MOD_ID))
                .toList();
    }
}
