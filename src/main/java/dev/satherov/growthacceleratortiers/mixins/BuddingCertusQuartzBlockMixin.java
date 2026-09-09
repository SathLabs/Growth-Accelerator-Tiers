package dev.satherov.growthacceleratortiers.mixins;

import dev.satherov.growthacceleratortiers.util.BuddingBlockGrowthHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import appeng.core.definitions.AEBlocks;
import appeng.decorative.solid.BuddingCertusQuartzBlock;

@Mixin({ BuddingCertusQuartzBlock.class })
public class BuddingCertusQuartzBlockMixin implements BuddingBlockGrowthHandler {
    @Unique
    private BuddingCertusQuartzBlock growthAcceleratorTiers$this() {
        return (BuddingCertusQuartzBlock) (Object) this;
    }
    
    @Inject(
            method = { "randomTick" },
            at = { @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ) },
            cancellable = true
    )
    private void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (this.growthAcceleratorTiers$checkForAccelerator(state, level, pos, random)) {
            ci.cancel();
        }
    }
    
    @Unique
    public void growthAcceleratorTiers$handleGrowth(ServerLevel level, BlockPos pos, BlockPos growthPos, Direction direction, RandomSource randomSource) {
        if (randomSource.nextInt(5) == 0) {
            BlockPos targetPos = pos.relative(direction);
            BlockState targetState = level.getBlockState(targetPos);
            Block newCluster = null;
            if (this.growthAcceleratorTiers$canClusterGrowAtState(targetState)) {
                newCluster = AEBlocks.SMALL_QUARTZ_BUD.block();
            } else if (targetState.is(AEBlocks.SMALL_QUARTZ_BUD.block())
                    && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
                newCluster = AEBlocks.MEDIUM_QUARTZ_BUD.block();
            } else if (targetState.is(AEBlocks.MEDIUM_QUARTZ_BUD.block())
                    && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
                newCluster = AEBlocks.LARGE_QUARTZ_BUD.block();
            } else if (targetState.is(AEBlocks.LARGE_QUARTZ_BUD.block())
                    && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
                newCluster = AEBlocks.QUARTZ_CLUSTER.block();
            }
            
            if (newCluster == null) {
                return;
            }
            
            BlockState newClusterState = newCluster.defaultBlockState()
                    .setValue(AmethystClusterBlock.FACING, direction)
                    .setValue(AmethystClusterBlock.WATERLOGGED, targetState.getFluidState().getType() == Fluids.WATER);
            level.setBlockAndUpdate(targetPos, newClusterState);
            
            if (this.growthAcceleratorTiers$this() == AEBlocks.FLAWLESS_BUDDING_QUARTZ.block() || randomSource.nextInt(12) != 0) {
                return;
            }
            Block newBlock;
            if (this.growthAcceleratorTiers$this() == AEBlocks.FLAWED_BUDDING_QUARTZ.block()) {
                newBlock = AEBlocks.CHIPPED_BUDDING_QUARTZ.block();
            } else if (this.growthAcceleratorTiers$this() == AEBlocks.CHIPPED_BUDDING_QUARTZ.block()) {
                newBlock = AEBlocks.DAMAGED_BUDDING_QUARTZ.block();
            } else if (this.growthAcceleratorTiers$this() == AEBlocks.DAMAGED_BUDDING_QUARTZ.block()) {
                newBlock = AEBlocks.QUARTZ_BLOCK.block();
            } else {
                throw new IllegalStateException("Unexpected block: " + this.growthAcceleratorTiers$this());
            }
            level.setBlockAndUpdate(pos, newBlock.defaultBlockState());
        }
    }
}
