package dev.satherov.growthacceleratortiers.blockentity;

import dev.satherov.growthacceleratortiers.block.GATMonoBlock;
import dev.satherov.growthacceleratortiers.core.GATConfig;
import dev.satherov.growthacceleratortiers.data.PositionAttachment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import appeng.api.ids.AETags;


public abstract class GATMonoBlockEntity extends GATGrowthAcceleratorBlockEntity {
    
    private boolean cache = false;
    private int cooldown = 0;
    
    public GATMonoBlockEntity(int maxStoredPower, int powerPerTick, double multiplier, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(maxStoredPower, powerPerTick, multiplier, blockEntityType, pos, blockState);
    }
    
    @Override
    protected void onTick(int ticksSinceLastCall) {
        if (!(this.getLevel() instanceof ServerLevel level)) {
            if (!this.cache) super.onTick(ticksSinceLastCall);
            return;
        }
        
        boolean conflicted = this.cache;
        boolean check = false;
        
        if (this.cooldown > 0) {
            this.cooldown -= ticksSinceLastCall;
        }
        
        if (this.cooldown <= 0) {
            check = true;
            this.cooldown = 40;
        }
        
        if (check) {
            conflicted = false;
            
            for (Direction dir : Direction.values()) {
                BlockPos relative = this.getBlockPos().relative(dir).immutable();
                BlockState state = level.getBlockState(relative);
                if (!state.is(AETags.GROWTH_ACCELERATABLE)) continue;
                ChunkAccess chunk = level.getChunkAt(relative);
                if (this.getBlockState().getBlock() instanceof GATMonoBlock<?> block) {
                    PositionAttachment data = chunk.getData(block.getAttachmentType());
                    if (data.get(relative) > 1 && GATConfig.instance().enableConflicts()) {
                        conflicted = true;
                        break;
                    }
                }
            }
        }
        
        if (conflicted != this.getBlockState().getValue(GATMonoBlock.CONFLICTED)) {
            level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(GATMonoBlock.CONFLICTED, conflicted));
        }
        
        this.cache = conflicted;
        
        if (!this.cache) super.onTick(ticksSinceLastCall);
    }
}
