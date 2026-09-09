package dev.satherov.growthacceleratortiers.core.events;

import dev.satherov.growthacceleratortiers.core.definitions.GATBlocks;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class GATTooltipEvent {
    
    @SubscribeEvent
    public static void registerTooltips(ItemTooltipEvent event) {
        
        if (event.getItemStack().is(GATBlocks.CRANKED_GROWTH_ACCELERATOR.asItem())) {
            event.getToolTip().add(Component.translatable("tooltip.growthacceleratortiers.cranked_growth_accelerator").withStyle(ChatFormatting.GRAY));
        } else if (event.getItemStack().is(GATBlocks.BOOSTED_GROWTH_ACCELERATOR.asItem())) {
            event.getToolTip().add(Component.translatable("tooltip.growthacceleratortiers.boosted_growth_accelerator").withStyle(ChatFormatting.GRAY));
        } else if (event.getItemStack().is(GATBlocks.DIRECTIONAL_GROWTH_ACCELERATOR.asItem())) {
            event.getToolTip()
                    .add(Component.translatable("tooltip.growthacceleratortiers.directional_growth_accelerator").withStyle(ChatFormatting.GRAY)
                            .append("\n")
                            .append(Component.translatable("tooltip.growthacceleratortiers.change_direction")).withStyle(ChatFormatting.GRAY));
        }
        
    }
}
