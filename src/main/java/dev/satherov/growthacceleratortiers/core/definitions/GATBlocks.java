package dev.satherov.growthacceleratortiers.core.definitions;

import dev.satherov.growthacceleratortiers.api.GAT;
import dev.satherov.growthacceleratortiers.api.ids.GATBlockIds;
import dev.satherov.growthacceleratortiers.block.GATBoostedBlock;
import dev.satherov.growthacceleratortiers.block.GATCrankedBlock;
import dev.satherov.growthacceleratortiers.block.GATDirectionalBlock;
import dev.satherov.growthacceleratortiers.core.annotations.NothingNull;

import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import com.google.common.base.Preconditions;

import org.jetbrains.annotations.Nullable;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.MainCreativeTab;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@NothingNull
public class GATBlocks {
    
    public static final DeferredRegister.Blocks DR = DeferredRegister.createBlocks(GAT.MOD_ID);
    
    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();
    
    public static final BlockDefinition<GATCrankedBlock> CRANKED_GROWTH_ACCELERATOR = GATBlocks.block("Cranked Growth Accelerator", GATBlockIds.CRANKED_GROWTH_ACCELERATOR, GATCrankedBlock::new);
    public static final BlockDefinition<GATDirectionalBlock> DIRECTIONAL_GROWTH_ACCELERATOR = GATBlocks.block("Directional Growth Accelerator", GATBlockIds.DIRECTIONAL_GROWTH_ACCELERATOR, GATDirectionalBlock::new);
    public static final BlockDefinition<GATBoostedBlock> BOOSTED_GROWTH_ACCELERATOR = GATBlocks.block("Boosted Growth Accelerator", GATBlockIds.BOOSTED_GROWTH_ACCELERATOR, GATBoostedBlock::new);
    
    private static <T extends Block> BlockDefinition<T> block(String englishName, ResourceLocation id, Supplier<T> blockSupplier) {
        return GATBlocks.block(englishName, id, blockSupplier, null);
    }
    
    private static <T extends Block> BlockDefinition<T> block(String englishName, ResourceLocation id, Supplier<T> blockSupplier, @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        Preconditions.checkArgument(id.getNamespace().equals(GAT.MOD_ID));
        
        // Create block and matching item
        var deferredBlock = GATBlocks.DR.register(id.getPath(), blockSupplier);
        var deferredItem = GATItems.DR.register(id.getPath(), () -> {
            var block = deferredBlock.get();
            var itemProperties = new Item.Properties();
            if (itemFactory != null) {
                var item = itemFactory.apply(block, itemProperties);
                if (item == null) {
                    throw new IllegalArgumentException("BlockItem factory for " + id + " returned null");
                }
                return item;
            } else if (block instanceof AEBaseBlock) {
                return new AEBaseBlockItem(block, itemProperties);
            } else {
                return new BlockItem(block, itemProperties);
            }
        });
        
        var itemDef = new ItemDefinition<>(englishName, deferredItem);
        MainCreativeTab.add(itemDef);
        BlockDefinition<T> definition = new BlockDefinition<>(englishName, deferredBlock, itemDef);
        
        GATBlocks.BLOCKS.add(definition);
        
        return definition;
        
    }
    
    public static List<BlockDefinition<?>> getBlocks() {
        return GATBlocks.BLOCKS;
    }
}
