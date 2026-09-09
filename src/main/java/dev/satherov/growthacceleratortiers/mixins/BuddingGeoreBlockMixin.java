package dev.satherov.growthacceleratortiers.mixins;

import dev.satherov.growthacceleratortiers.util.BuddingBlockGrowthHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import com.shynieke.geore.block.BuddingGeoreBlock;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;


@Mixin(BuddingGeoreBlock.class)
public class BuddingGeoreBlockMixin implements BuddingBlockGrowthHandler {
    
    
    @Shadow
    @Final
    private Supplier<? extends AmethystClusterBlock> smallSupplier;
    @Shadow
    @Final
    private Supplier<? extends AmethystClusterBlock> mediumSupplier;
    @Shadow
    @Final
    private Supplier<? extends AmethystClusterBlock> largeSupplier;
    @Shadow
    @Final
    private Supplier<? extends AmethystClusterBlock> clusterSupplier;
    
    // Ordinal 1 is GeoRe's direction pick, `DIRECTIONS[random.nextInt(DIRECTIONS.length)]`. Ordinal 0 was its growth
    // roll, `random.nextInt(5) != 0`, and cancelling in front of that roll grew a bud on every random tick instead
    // of on one in five, so an accelerated geore grew five times as fast as the mod intends.
    @Inject(
            method = "randomTick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",
                    ordinal = 1),
            cancellable = true
    )
    private void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (this.growthAcceleratorTiers$checkForAccelerator(state, level, pos, random)) {
            ci.cancel();
        }
    }
    
    @Override
    @Unique
    public void growthAcceleratorTiers$handleGrowth(ServerLevel level, BlockPos pos, BlockPos growthPos, Direction direction, RandomSource randomSource) {
        BlockState targetState = level.getBlockState(growthPos);
        Block newBlock = null;
        
        if (BuddingAmethystBlock.canClusterGrowAtState(targetState)) {
            newBlock = this.smallSupplier.get();
        } else if (targetState.is(this.smallSupplier.get()) &&
                targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            newBlock = this.mediumSupplier.get();
        } else if (targetState.is(this.mediumSupplier.get()) &&
                targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            newBlock = this.largeSupplier.get();
        } else if (targetState.is(this.largeSupplier.get()) &&
                targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            newBlock = this.clusterSupplier.get();
        }
        
        if (newBlock != null) {
            BlockState newState = newBlock.defaultBlockState()
                    .setValue(AmethystClusterBlock.FACING, direction)
                    .setValue(AmethystClusterBlock.WATERLOGGED, targetState.getFluidState().getType() == Fluids.WATER);
            level.setBlockAndUpdate(growthPos, newState);
        }
    }
}
