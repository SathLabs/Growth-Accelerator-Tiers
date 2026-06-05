package dev.satherov.growthacceleratortiers.block;

import lombok.Getter;

import dev.satherov.growthacceleratortiers.blockentity.GATMonoBlockEntity;
import dev.satherov.growthacceleratortiers.data.PositionAttachment;

import net.neoforged.neoforge.attachment.AttachmentType;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.ChunkAccess;

import appeng.core.AEConfig;
import appeng.util.Platform;

import java.util.function.Supplier;

@Getter
public abstract class GATMonoBlock<T extends GATMonoBlockEntity> extends GATGrowthAcceleratorBlock<T> {
    
    public static final BooleanProperty CONFLICTED = BooleanProperty.create("conflicted");
    private final Supplier<AttachmentType<PositionAttachment>> attachmentType;
    
    public GATMonoBlock(Properties properties, Supplier<AttachmentType<PositionAttachment>> attachmentType) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(GATMonoBlock.CONFLICTED, false));
        this.attachmentType = attachmentType;
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(GATMonoBlock.CONFLICTED);
    }
    
    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (oldState.is(state.getBlock())) return;
        this.dataUpdate(level, pos, 1);
    }
    
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        this.dataUpdate(level, pos, -1);
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
    
    private void dataUpdate(LevelAccessor accessor, BlockPos pos, int count) {
        for (Direction dir : Direction.values()) {
            BlockPos relative = pos.relative(dir).immutable();
            ChunkAccess chunk = accessor.getChunk(relative);
            PositionAttachment data = chunk.getData(this.getAttachmentType());
            data.put(relative, Math.clamp(data.get(relative) + count, 0, 6));
            chunk.markUnsaved();
        }
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource r) {
        if (!state.getValue(GATMonoBlock.CONFLICTED)) {
            super.animateTick(state, level, pos, r);
            return;
        }
        
        if (!AEConfig.instance().isEnableEffects()) {
            return;
        }
        
        final T cga = this.getBlockEntity(level, pos);
        
        if (cga != null && cga.isPowered()) {
            final double d0 = r.nextFloat() - 0.5F;
            final double d1 = r.nextFloat() - 0.5F;
            
            var up = cga.getTop();
            var forward = cga.getFront();
            var west = Platform.crossProduct(forward, up);
            
            double rx = 0.5 + pos.getX();
            double ry = 0.5 + pos.getY();
            double rz = 0.5 + pos.getZ();
            
            rx += up.getStepX() * d0;
            ry += up.getStepY() * d0;
            rz += up.getStepZ() * d0;
            
            final int x = pos.getX();
            final int y = pos.getY();
            final int z = pos.getZ();
            
            double dz = 0;
            double dx = 0;
            BlockPos pt = null;
            
            switch (r.nextInt(4)) {
                case 0 -> {
                    dx = 0.6;
                    dz = d1;
                    pt = new BlockPos(x + west.getStepX(), y + west.getStepY(), z + west.getStepZ());
                }
                case 1 -> {
                    dx = d1;
                    dz += 0.6;
                    pt = new BlockPos(x + forward.getStepX(), y + forward.getStepY(), z + forward.getStepZ());
                }
                case 2 -> {
                    dx = d1;
                    dz = -0.6;
                    pt = new BlockPos(x - forward.getStepX(), y - forward.getStepY(), z - forward.getStepZ());
                }
                case 3 -> {
                    dx = -0.6;
                    dz = d1;
                    pt = new BlockPos(x - west.getStepX(), y - west.getStepY(), z - west.getStepZ());
                }
                default -> throw new IllegalStateException();
            }
            
            if (!level.getBlockState(pt).isAir()) {
                return;
            }
            
            rx += dx * west.getStepX();
            ry += dx * west.getStepY();
            rz += dx * west.getStepZ();
            
            rx += dz * forward.getStepX();
            ry += dz * forward.getStepY();
            rz += dz * forward.getStepZ();
            
            Minecraft.getInstance().particleEngine.createParticle(DustParticleOptions.REDSTONE, rx, ry, rz, 0.0D, 0.0D, 0.0D);
        }
    }
}
