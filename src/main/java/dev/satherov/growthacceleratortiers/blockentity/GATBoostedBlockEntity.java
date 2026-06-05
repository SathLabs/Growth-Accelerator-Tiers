package dev.satherov.growthacceleratortiers.blockentity;

import dev.satherov.growthacceleratortiers.core.GATConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.blockentity.misc.CrankBlockEntity;


public class GATBoostedBlockEntity extends GATMonoBlockEntity {
    
    public static final int MAX_STORED_POWER = GATConfig.instance().getBoostedInternalEnergyMultiplier() * CrankBlockEntity.POWER_PER_CRANK_TURN;
    public static final int POWER_PER_TICK = GATConfig.instance().getBoostedIdlePowerConsumption();
    public static final double MULTIPLIER = GATConfig.instance().getBoostedSpeedMultiplier();
    
    public GATBoostedBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(GATBoostedBlockEntity.MAX_STORED_POWER, GATBoostedBlockEntity.POWER_PER_TICK, GATBoostedBlockEntity.MULTIPLIER, blockEntityType, pos, blockState);
    }
}
