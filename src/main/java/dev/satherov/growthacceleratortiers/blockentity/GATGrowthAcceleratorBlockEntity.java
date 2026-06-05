package dev.satherov.growthacceleratortiers.blockentity;

import dev.satherov.growthacceleratortiers.block.GATGrowthAcceleratorBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.config.Actionable;
import appeng.api.ids.AETags;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.implementations.blockentities.ICrankable;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.core.AEConfig;

import java.util.EnumSet;
import java.util.Set;

public abstract class GATGrowthAcceleratorBlockEntity extends AENetworkedPoweredBlockEntity implements IPowerChannelState {
    
    protected final int powerPerTick;
    protected final double multiplier;
    
    public GATGrowthAcceleratorBlockEntity(int maxStoredPower, int powerPerTick, double multiplier, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
        this.powerPerTick = powerPerTick;
        this.multiplier = multiplier;
        this.setInternalMaxPower(maxStoredPower);
        this.setPowerSides(this.getGridConnectableSides(this.getOrientation()));
        this.getMainNode().setFlags();
        this.getMainNode().setIdlePowerUsage(powerPerTick);
        this.getMainNode().addService(IGridTickable.class, new IGridTickable() {
            @Override
            public TickingRequest getTickingRequest(IGridNode node) {
                int speed = AEConfig.instance().getGrowthAcceleratorSpeed();
                return new TickingRequest(speed, speed, false);
            }
            
            @Override
            public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
                GATGrowthAcceleratorBlockEntity.this.onTick(ticksSinceLastCall);
                return TickRateModulation.SAME;
            }
        });
    }
    
    protected void onTick(int ticksSinceLastCall) {
        
        var powered = this.isPowered();
        if (powered != this.getBlockState().getValue(GATGrowthAcceleratorBlock.POWERED)) {
            this.markForUpdate();
        }
        
        if (!powered) {
            return;
        }
        
        this.extractAEPower(this.powerPerTick * ticksSinceLastCall, Actionable.MODULATE);
        
        for (var direction : Direction.values()) {
            var adjPos = this.getBlockPos().relative(direction);
            var adjState = this.getLevel().getBlockState(adjPos);
            
            if (!adjState.is(AETags.GROWTH_ACCELERATABLE)) {
                continue;
            }
            
            for (int i = 0; i < this.multiplier; i++) {
                adjState.randomTick((ServerLevel) this.getLevel(), adjPos, this.getLevel().getRandom());
            }
        }
    }
    
    @Override
    public InternalInventory getInternalInventory() {
        return InternalInventory.empty();
    }
    
    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return orientation.getSides(EnumSet.of(RelativeSide.FRONT, RelativeSide.BACK));
    }
    
    @Override
    protected void onOrientationChanged(BlockOrientation orientation) {
        super.onOrientationChanged(orientation);
        this.setPowerSides(this.getGridConnectableSides(this.getOrientation()));
    }
    
    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        if (reason == IGridNodeListener.State.POWER) {
            this.markForUpdate();
        }
    }
    
    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }
    
    @Override
    public boolean isPowered() {
        if (!this.isClientSide()) {
            return this.getMainNode().isPowered() || this.extractAEPower(this.powerPerTick, Actionable.SIMULATE) >= this.powerPerTick;
        }
        
        return this.getBlockState().getValue(GATGrowthAcceleratorBlock.POWERED);
    }
    
    @Override
    public boolean isActive() {
        return this.isPowered();
    }
    
    @org.jetbrains.annotations.Nullable
    public ICrankable getCrankable(Direction direction) {
        if (this.getPowerSides().contains(direction)) {
            return new Crankable();
        }
        return null;
    }
}
