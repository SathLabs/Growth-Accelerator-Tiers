package dev.satherov.growthacceleratortiers.block;

import dev.satherov.growthacceleratortiers.blockentity.GATGrowthAcceleratorBlockEntity;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.block.AEBaseBlock;
import appeng.block.AEBaseEntityBlock;
import appeng.client.render.effects.ParticleTypes;
import appeng.core.AEConfig;
import appeng.core.AppEngClient;
import appeng.util.Platform;

public abstract class GATGrowthAcceleratorBlock<T extends GATGrowthAcceleratorBlockEntity> extends AEBaseEntityBlock<T> {
    
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    
    public GATGrowthAcceleratorBlock() {
        super(AEBaseBlock.metalProps());
        this.registerDefaultState(this.defaultBlockState().setValue(GATGrowthAcceleratorBlock.POWERED, false));
    }
    
    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, T be) {
        return currentState.setValue(GATGrowthAcceleratorBlock.POWERED, be.isPowered());
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(GATGrowthAcceleratorBlock.POWERED);
    }
    
    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.facing();
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource r) {
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
            
            Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.LIGHTNING, rx, ry, rz, 0.0D, 0.0D,
                    0.0D);
        }
    }
    
}
