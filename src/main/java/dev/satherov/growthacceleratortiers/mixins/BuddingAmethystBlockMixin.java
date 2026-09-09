package dev.satherov.growthacceleratortiers.mixins;

import dev.satherov.growthacceleratortiers.util.BuddingBlockGrowthHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuddingAmethystBlock.class)
public class BuddingAmethystBlockMixin implements BuddingBlockGrowthHandler {
    
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
            newBlock = Blocks.SMALL_AMETHYST_BUD;
        } else if (targetState.is(Blocks.SMALL_AMETHYST_BUD) &&
                targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            newBlock = Blocks.MEDIUM_AMETHYST_BUD;
        } else if (targetState.is(Blocks.MEDIUM_AMETHYST_BUD) &&
                targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            newBlock = Blocks.LARGE_AMETHYST_BUD;
        } else if (targetState.is(Blocks.LARGE_AMETHYST_BUD) &&
                targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            newBlock = Blocks.AMETHYST_CLUSTER;
        }
        
        if (newBlock != null) {
            BlockState newState = newBlock.defaultBlockState()
                    .setValue(AmethystClusterBlock.FACING, direction)
                    .setValue(AmethystClusterBlock.WATERLOGGED, targetState.getFluidState().getType() == Fluids.WATER);
            level.setBlockAndUpdate(growthPos, newState);
        }
    }
}