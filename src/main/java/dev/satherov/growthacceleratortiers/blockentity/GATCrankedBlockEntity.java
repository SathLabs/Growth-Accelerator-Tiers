package dev.satherov.growthacceleratortiers.blockentity;

import dev.satherov.growthacceleratortiers.block.GATGrowthAcceleratorBlock;
import dev.satherov.growthacceleratortiers.core.GATConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.config.Actionable;
import appeng.api.implementations.blockentities.ICrankable;
import appeng.api.orientation.BlockOrientation;
import appeng.api.util.AECableType;
import appeng.blockentity.misc.CrankBlockEntity;

import java.util.Set;

public class GATCrankedBlockEntity extends GATGrowthAcceleratorBlockEntity {
    
    public static final int MAX_STORED_POWER = GATConfig.instance().getCrankedInternalEnergyMultiplier() * CrankBlockEntity.POWER_PER_CRANK_TURN;
    public static final int POWER_PER_TICK = GATConfig.instance().getCrankedIdlePowerConsumption();
    public static final double MULTIPLIER = GATConfig.instance().getCrankedSpeedMultiplier();
    
    public GATCrankedBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(GATCrankedBlockEntity.MAX_STORED_POWER, GATCrankedBlockEntity.POWER_PER_TICK, GATCrankedBlockEntity.MULTIPLIER, blockEntityType, pos, blockState);
        this.setPowerSides(Set.of());
    }
    
    public ICrankable getCrankable(Direction direction) {
        return new Crankable();
    }
    
    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return Set.of();
    }
    
    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.NONE;
    }
    
    @Override
    public boolean isPowered() {
        if (!this.isClientSide()) {
            // No extraction from the main net only check the interal buffer
            return this.extractAEPower(GATCrankedBlockEntity.POWER_PER_TICK, Actionable.SIMULATE) >= GATCrankedBlockEntity.POWER_PER_TICK;
        }
        
        return this.getBlockState().getValue(GATGrowthAcceleratorBlock.POWERED);
    }
}
