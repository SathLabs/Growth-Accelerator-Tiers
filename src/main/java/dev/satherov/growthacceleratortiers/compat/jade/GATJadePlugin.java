package dev.satherov.growthacceleratortiers.compat.jade;

import dev.satherov.growthacceleratortiers.api.GAT;
import dev.satherov.growthacceleratortiers.block.GATBoostedBlock;
import dev.satherov.growthacceleratortiers.block.GATDirectionalBlock;
import dev.satherov.growthacceleratortiers.block.GATGrowthAcceleratorBlock;
import dev.satherov.growthacceleratortiers.block.GATMonoBlock;
import dev.satherov.growthacceleratortiers.core.annotations.NothingNull;
import dev.satherov.growthacceleratortiers.core.definitions.GATBlocks;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import appeng.core.definitions.BlockDefinition;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

import java.util.function.BiConsumer;

@WailaPlugin
public class GATJadePlugin implements IWailaPlugin {
    
    private static void directional(ITooltip tooltip, BlockAccessor accessor) {
        if (!(accessor.getBlock() instanceof GATDirectionalBlock)) return;
        GATDirectionalBlock.Directions direction = accessor.getBlockState().getValue(GATDirectionalBlock.DIRECTION);
        boolean conflicted = accessor.getBlockState().getValue(GATMonoBlock.CONFLICTED);
        tooltip.add(Component
                .translatable("direction.growthacceleratortiers")
                .append(Component.literal(": "))
                .append(Component.translatable("direction.growthacceleratortiers." + direction.name().toLowerCase()))
                .withStyle(ChatFormatting.GRAY)
        );
        if (conflicted) {
            tooltip.add(Component.translatable("tooltip.growthacceleratortiers.conflicted").withStyle(ChatFormatting.RED));
        }
    }
    
    private static void boosted(ITooltip tooltip, BlockAccessor accessor) {
        if (!(accessor.getBlock() instanceof GATBoostedBlock)) return;
        boolean conflicted = accessor.getBlockState().getValue(GATMonoBlock.CONFLICTED);
        if (conflicted) {
            tooltip.add(Component.translatable("tooltip.growthacceleratortiers.conflicted").withStyle(ChatFormatting.RED));
        }
    }
    
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(GATComponentProvider.BOOSTED, GATGrowthAcceleratorBlock.class);
        registration.registerBlockComponent(GATComponentProvider.DIRECTIONAL, GATDirectionalBlock.class);
    }
    
    @NothingNull
    enum GATComponentProvider implements IBlockComponentProvider {
        DIRECTIONAL(GATBlocks.DIRECTIONAL_GROWTH_ACCELERATOR, GATJadePlugin::directional),
        BOOSTED(GATBlocks.BOOSTED_GROWTH_ACCELERATOR, GATJadePlugin::boosted),
        ;
        
        final BlockDefinition<? extends GATGrowthAcceleratorBlock<?>> holder;
        final BiConsumer<ITooltip, BlockAccessor> appender;
        
        GATComponentProvider(
                BlockDefinition<? extends GATGrowthAcceleratorBlock<?>> holder,
                BiConsumer<ITooltip, BlockAccessor> appender
        ) {
            this.holder = holder;
            this.appender = appender;
        }
        
        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            this.appender.accept(tooltip, accessor);
        }
        
        @Override
        public Identifier getUid() {
            return GAT.rl(this.holder.id().getPath());
        }
        
        @Override
        public int getDefaultPriority() {
            return 1234;
        }
    }
}
