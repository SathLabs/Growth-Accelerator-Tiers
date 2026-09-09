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

import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.blocks.BlockBuddingEntro;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ BlockBuddingEntro.class })
public class BuddingEntroBlockMixin implements BuddingBlockGrowthHandler {
    @Unique
    private BlockBuddingEntro growthAcceleratorTiers$this() {
        return (BlockBuddingEntro) (Object) this;
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
        if (randomSource.nextInt(3) == 0) {
            BlockPos targetPos = pos.relative(direction);
            BlockState targetState = level.getBlockState(targetPos);
            Block newCluster;
            if (this.growthAcceleratorTiers$canClusterGrowAtState(targetState)) {
                newCluster = EAESingletons.ENTRO_BUD_SMALL;
            } else {
                newCluster = BlockBuddingEntro.canClusterGrow(targetState, direction);
            }
            
            if (newCluster != null) {
                BlockState newClusterState = newCluster.defaultBlockState().setValue(AmethystClusterBlock.FACING, direction).setValue(AmethystClusterBlock.WATERLOGGED, targetState.getFluidState().getType() == Fluids.WATER);
                level.setBlockAndUpdate(targetPos, newClusterState);
                if (randomSource.nextInt(10) == 0) {
                    Block newBlock = this.growthAcceleratorTiers$this().degradeBudding();
                    level.setBlockAndUpdate(pos, newBlock.defaultBlockState());
                }
            }
        }
    }
}
