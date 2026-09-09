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

import cn.dancingsnow.neoecoae.all.NEBlocks;
import cn.dancingsnow.neoecoae.blocks.BuddingEnergizedCrystalBlock;

@Mixin(BuddingEnergizedCrystalBlock.class)
public class BuddingEnergizedCrystalBlockMixin implements BuddingBlockGrowthHandler {
    
    @Unique
    private BuddingEnergizedCrystalBlock growthAcceleratorTiers$this() {
        //noinspection DataFlowIssue
        return (BuddingEnergizedCrystalBlock) (Object) this;
    }
    
    @Inject(
            method = "randomTick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/Util;getRandom([Ljava/lang/Object;Lnet/minecraft/util/RandomSource;)Ljava/lang/Object;"),
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
        Block smallBud = NEBlocks.SMALL_ENERGIZED_CRYSTAL_BUD.get();
        Block mediumBud = NEBlocks.MEDIUM_ENERGIZED_CRYSTAL_BUD.get();
        Block largeBud = NEBlocks.LARGE_ENERGIZED_CRYSTAL_BUD.get();
        Block cluster = NEBlocks.ENERGIZED_CRYSTAL_CLUSTER.get();
        
        BlockState targetState = level.getBlockState(growthPos);
        Block newBlock = null;
        
        if (BuddingEnergizedCrystalBlock.canClusterGrowAtState(targetState)) {
            newBlock = smallBud;
        } else if (targetState.is(smallBud) && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            newBlock = mediumBud;
        } else if (targetState.is(mediumBud) && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            newBlock = largeBud;
        } else if (targetState.is(largeBud) && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            newBlock = cluster;
        }
        
        if (newBlock == null) {
            return;
        }
        
        BlockState newState = newBlock.defaultBlockState()
                .setValue(AmethystClusterBlock.FACING, direction)
                .setValue(AmethystClusterBlock.WATERLOGGED, targetState.getFluidState().getType() == Fluids.WATER);
        
        level.setBlockAndUpdate(growthPos, newState);
        
        Block self = this.growthAcceleratorTiers$this();
        if (self == NEBlocks.FLAWLESS_BUDDING_ENERGIZED_CRYSTAL.get() || randomSource.nextInt(12) != 0) {
            return;
        }
        
        Block flawed = NEBlocks.FLAWED_BUDDING_ENERGIZED_CRYSTAL.get();
        Block chipped = NEBlocks.CHIPPED_BUDDING_ENERGIZED_CRYSTAL.get();
        Block damaged = NEBlocks.DAMAGED_BUDDING_ENERGIZED_CRYSTAL.get();
        Block degraded = null;
        if (self == flawed) {
            degraded = chipped;
        } else if (self == chipped) {
            degraded = damaged;
        } else if (self == damaged) {
            degraded = NEBlocks.ENERGIZED_CRYSTAL_BLOCK.get();
        }
        
        if (degraded == null) {
            return;
        }
        
        level.setBlockAndUpdate(pos, degraded.defaultBlockState());
    }
}
