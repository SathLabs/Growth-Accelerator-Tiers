package dev.satherov.growthacceleratortiers.core.events;

import dev.satherov.growthacceleratortiers.block.GATDirectionalBlock;
import dev.satherov.growthacceleratortiers.item.GATDirectionalModifier;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;

public class GATPlayerInteractEvent {
    
    @SubscribeEvent
    public static void onPlayerUseBlockEvent(PlayerInteractEvent.RightClickBlock event) {
        
        if (event.getEntity().getMainHandItem().getItem() instanceof GATDirectionalModifier) {
            
            if (event.getLevel() instanceof ServerLevel level && event.getEntity() instanceof ServerPlayer player) {
                
                if (level.getBlockState(event.getPos()).getBlock() instanceof GATDirectionalBlock) {
                    
                    BlockPos pos = event.getPos();
                    BlockState state = level.getBlockState(pos);
                    
                    GATDirectionalBlock.Directions currentDir = state.getValue(GATDirectionalBlock.DIRECTION);
                    GATDirectionalBlock.Directions nextDir = GATPlayerInteractEvent.getNextDirection(currentDir);
                    
                    level.setBlock(pos, state.setValue(GATDirectionalBlock.DIRECTION, nextDir), 3);
                    player.displayClientMessage(Component.literal(GATPlayerInteractEvent.getName(nextDir)).withStyle(ChatFormatting.AQUA), true);
                }
            }
        }
    }
    
    private static GATDirectionalBlock.Directions getNextDirection(GATDirectionalBlock.Directions current) {
        GATDirectionalBlock.Directions[] values = GATDirectionalBlock.Directions.values();
        int nextIndex = (current.ordinal() + 1) % values.length;
        return values[nextIndex];
    }
    
    
    private static String getName(GATDirectionalBlock.Directions dir) {
        String name = dir.name().toLowerCase(Locale.ROOT);
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
}
