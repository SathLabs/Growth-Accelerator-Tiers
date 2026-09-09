package dev.satherov.growthacceleratortiers.util;

import dev.satherov.growthacceleratortiers.block.GATDirectionalBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public interface BuddingBlockGrowthHandler {
    
    default boolean growthAcceleratorTiers$checkForAccelerator(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        for (Direction dir : Direction.values()) {
            BlockPos checkPos = pos.relative(dir);
            BlockState checkState = level.getBlockState(checkPos);
            
            if (checkState.getBlock() instanceof GATDirectionalBlock) {
                Direction direction = checkState.getValue(GATDirectionalBlock.DIRECTION).getDirection(checkState.getValue(BlockStateProperties.FACING));
                BlockPos growthPos = pos.relative(direction);
                
                this.growthAcceleratorTiers$handleGrowth(level, pos, growthPos, direction, randomSource);
                return true;
            }
        }
        return false;
    }
    
    default boolean growthAcceleratorTiers$canClusterGrowAtState(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER) && state.getFluidState().getAmount() == 8;
    }
    
    void growthAcceleratorTiers$handleGrowth(ServerLevel level, BlockPos pos, BlockPos growthPos, Direction direction, RandomSource randomSource);
}