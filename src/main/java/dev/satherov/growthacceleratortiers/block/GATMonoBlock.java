package dev.satherov.growthacceleratortiers.block;

import dev.satherov.growthacceleratortiers.blockentity.GATMonoBlockEntity;
import dev.satherov.growthacceleratortiers.data.PositionAttachment;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.attachment.AttachmentType;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.ChunkAccess;

import appeng.core.AEConfig;
import appeng.core.AppEngClient;
import appeng.util.Platform;

import java.util.function.Supplier;

public abstract class GATMonoBlock<T extends GATMonoBlockEntity> extends GATGrowthAcceleratorBlock<T> {
    
    private final Supplier<AttachmentType<PositionAttachment>> attachmentType;
    public static final BooleanProperty CONFLICTED = BooleanProperty.create("conflicted");
    
    public GATMonoBlock(Supplier<AttachmentType<PositionAttachment>> attachmentType) {
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
        
        // LevelChunk#setBlockState calls onRemove and then onPlace on every state change on the server, not only on
        // a real placement, so a POWERED or CONFLICTED toggle ran a -1 and a +1 over the six neighbors for nothing.
        // The onPlace half is not always immediate: NeoForge skips it while Level.captureBlockSnapshots is set and
        // CommonHooks#onPlaceItemIntoWorld replays it once per captured snapshot after the item use, with that
        // snapshot's own old state. Only a change from another block is a placement. 26.1 already guards the same
        // way, so this is parity with that branch and not a fix for drift anyone reproduced.
        if (oldState.is(state.getBlock())) return;
        this.dataUpdate(level, pos, 1);
    }
    
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        // The same call path, so the state toggles land here too and only a change to another block is a removal.
        // 26.1 needs no test of its own because it hooks affectNeighborsAfterRemoval, which only runs on a real
        // removal; 1.21.1 has no such hook and has to look at the new state itself.
        if (!newState.is(state.getBlock())) {
            this.dataUpdate(level, pos, -1);
        }
        
        // Only the counter is guarded. AEBaseEntityBlock#onRemove spawns the block entity's extra drops and vanilla's
        // BlockBehaviour#onRemove takes the block entity off the chunk, both already keyed to the same block test, and
        // when that happens is AE2's and vanilla's business, so the call runs in every case.
        super.onRemove(state, level, pos, newState, isMoving);
    }
    
    private void dataUpdate(LevelAccessor accessor, BlockPos pos, int count) {
        if (accessor.isClientSide()) return;
        for (Direction dir : Direction.values()) {
            BlockPos relative = pos.relative(dir).immutable();
            ChunkAccess chunk = accessor.getChunk(relative);
            PositionAttachment data = chunk.getData(this.getAttachmentType());
            data.put(relative, Math.clamp(data.get(relative) + count, 0, 6));
            chunk.setUnsaved(true);
        }
    }
    
    public Supplier<AttachmentType<PositionAttachment>> getAttachmentType() {
        return this.attachmentType;
    }
    
    @OnlyIn(Dist.CLIENT)
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
        
        if (cga != null && cga.isPowered() && AppEngClient.instance().shouldAddParticles(r)) {
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
