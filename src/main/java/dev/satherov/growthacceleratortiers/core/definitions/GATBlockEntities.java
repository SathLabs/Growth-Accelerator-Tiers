package dev.satherov.growthacceleratortiers.core.definitions;

import dev.satherov.growthacceleratortiers.api.GAT;
import dev.satherov.growthacceleratortiers.api.ids.GATBlockIds;
import dev.satherov.growthacceleratortiers.blockentity.GATBoostedBlockEntity;
import dev.satherov.growthacceleratortiers.blockentity.GATCrankedBlockEntity;
import dev.satherov.growthacceleratortiers.blockentity.GATDirectionalBlockEntity;

import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.google.common.base.Preconditions;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.DeferredBlockEntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GATBlockEntities {
    
    public static final DeferredRegister<BlockEntityType<?>> DR = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, GAT.MOD_ID);
    private static final List<DeferredBlockEntityType<?>> BLOCK_ENTITY_TYPES = new ArrayList<>();
    public static final DeferredBlockEntityType<GATCrankedBlockEntity> CRANKED_GROWTH_ACCELERATOR = GATBlockEntities.create(
            GATBlockIds.CRANKED_GROWTH_ACCELERATOR.getPath(), GATCrankedBlockEntity.class,
            GATCrankedBlockEntity::new, GATBlocks.CRANKED_GROWTH_ACCELERATOR
    );
    
    public static final DeferredBlockEntityType<GATDirectionalBlockEntity> DIRECTIONAL_GROWTH_ACCELERATOR = GATBlockEntities.create(
            GATBlockIds.DIRECTIONAL_GROWTH_ACCELERATOR.getPath(), GATDirectionalBlockEntity.class,
            GATDirectionalBlockEntity::new, GATBlocks.DIRECTIONAL_GROWTH_ACCELERATOR
    );
    
    public static final DeferredBlockEntityType<GATBoostedBlockEntity> BOOSTED_GROWTH_ACCELERATOR = GATBlockEntities.create(
            GATBlockIds.BOOSTED_GROWTH_ACCELERATOR.getPath(), GATBoostedBlockEntity.class,
            GATBoostedBlockEntity::new, GATBlocks.BOOSTED_GROWTH_ACCELERATOR
    );
    
    
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> List<BlockEntityType<? extends T>> getSubclassesOf(Class<T> baseClass) {
        var result = new ArrayList<BlockEntityType<? extends T>>();
        for (var type : GATBlockEntities.BLOCK_ENTITY_TYPES) {
            if (baseClass.isAssignableFrom(type.getBlockEntityClass())) {
                result.add((BlockEntityType<? extends T>) type.get());
            }
        }
        return result;
    }
    
    
    public static List<BlockEntityType<?>> getImplementorsOf(Class<?> iface) {
        var result = new ArrayList<BlockEntityType<?>>();
        for (var type : GATBlockEntities.BLOCK_ENTITY_TYPES) {
            if (iface.isAssignableFrom(type.getBlockEntityClass())) {
                result.add(type.get());
            }
        }
        return result;
    }
    
    @SafeVarargs
    @SuppressWarnings("unchecked")
    private static <T extends AEBaseBlockEntity> DeferredBlockEntityType<T> create(String shortId, Class<T> entityClass, GATBlockEntities.BlockEntityFactory<T> factory, BlockDefinition<? extends AEBaseEntityBlock<?>>... blockDefinitions) {
        Preconditions.checkArgument(blockDefinitions.length > 0);
        
        var deferred = GATBlockEntities.DR.register(shortId, () -> {
            AtomicReference<BlockEntityType<T>> typeHolder = new AtomicReference<>();
            BlockEntityType.BlockEntitySupplier<T> supplier = (blockPos, blockState) -> factory.create(typeHolder.get(),
                    blockPos, blockState);
            
            var blocks = Arrays.stream(blockDefinitions)
                    .map(BlockDefinition::block)
                    .toArray(AEBaseEntityBlock[]::new);
            
            var type = BlockEntityType.Builder.of(supplier, blocks).build(null);
            typeHolder.setPlain(type); // Makes it available to the supplier used above
            
            AEBaseBlockEntity.registerBlockEntityItem(type, blockDefinitions[0].asItem());
            
            // If the block entity classes implement specific interfaces, automatically register them
            // as tickers with the blocks that create that entity.
            BlockEntityTicker<T> serverTicker = null;
            if (ServerTickingBlockEntity.class.isAssignableFrom(entityClass)) {
                serverTicker = (level, pos, state, entity) -> {
                    ((ServerTickingBlockEntity) entity).serverTick();
                };
            }
            BlockEntityTicker<T> clientTicker = null;
            if (ClientTickingBlockEntity.class.isAssignableFrom(entityClass)) {
                clientTicker = (level, pos, state, entity) -> {
                    ((ClientTickingBlockEntity) entity).clientTick();
                };
            }
            
            for (var block : blocks) {
                AEBaseEntityBlock<T> baseBlock = (AEBaseEntityBlock<T>) block;
                baseBlock.setBlockEntity(entityClass, type, clientTicker, serverTicker);
            }
            
            return type;
        });
        
        var result = new DeferredBlockEntityType<>(entityClass, deferred);
        GATBlockEntities.BLOCK_ENTITY_TYPES.add(result);
        return result;
    }
    
    @FunctionalInterface
    interface BlockEntityFactory<T extends AEBaseBlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }
}
