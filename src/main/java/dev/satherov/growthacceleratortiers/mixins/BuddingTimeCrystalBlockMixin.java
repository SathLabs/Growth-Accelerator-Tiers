package dev.satherov.growthacceleratortiers.mixins;

import dev.satherov.growthacceleratortiers.util.BuddingBlockGrowthHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import com.direwolf20.justdirethings.common.blocks.resources.TimeCrystalBuddingBlock;
import com.direwolf20.justdirethings.setup.JDTRegistration;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TimeCrystalBuddingBlock.class)
public class BuddingTimeCrystalBlockMixin implements BuddingBlockGrowthHandler {
    
    @Unique
    private TimeCrystalBuddingBlock growthAcceleratorTiers$this() {
        //noinspection DataFlowIssue
        return (TimeCrystalBuddingBlock) (Object) this;
    }
    
    @Inject(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",
                    ordinal = 1
            ),
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
            newBlock = JDTRegistration.TimeCrystalCluster_Small.get();
        } else if (targetState.is(JDTRegistration.TimeCrystalCluster_Small.get()) && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            newBlock = JDTRegistration.TimeCrystalCluster_Medium.get();
        } else if (targetState.is(JDTRegistration.TimeCrystalCluster_Medium.get()) && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            newBlock = JDTRegistration.TimeCrystalCluster_Large.get();
        } else if (targetState.is(JDTRegistration.TimeCrystalCluster_Large.get()) && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            newBlock = JDTRegistration.TimeCrystalCluster.get();
        }
        
        if (newBlock == null) {
            return;
        }
        
        BlockState newState = newBlock.defaultBlockState()
                .setValue(AmethystClusterBlock.FACING, direction)
                .setValue(AmethystClusterBlock.WATERLOGGED, targetState.getFluidState().getType() == Fluids.WATER);
        
        level.setBlockAndUpdate(growthPos, newState);
        
        BlockState buddingState = level.getBlockState(pos);
        if (!buddingState.is(this.growthAcceleratorTiers$this())) {
            return;
        }
        
        if (buddingState.getValue(TimeCrystalBuddingBlock.STAGE) == 3 && level.getRandom().nextFloat() < 0.05F) {
            level.setBlockAndUpdate(pos, buddingState.setValue(TimeCrystalBuddingBlock.STAGE, 0));
            level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.BLOCKS, 1.0F, 0.25F);
        }
    }
}
