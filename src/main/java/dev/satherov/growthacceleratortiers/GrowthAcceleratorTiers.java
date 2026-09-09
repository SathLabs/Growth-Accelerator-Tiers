package dev.satherov.growthacceleratortiers;

import dev.satherov.growthacceleratortiers.api.GAT;
import dev.satherov.growthacceleratortiers.command.GATCommands;
import dev.satherov.growthacceleratortiers.core.GATConfig;
import dev.satherov.growthacceleratortiers.core.GATCreativeTab;
import dev.satherov.growthacceleratortiers.core.definitions.GATAttachmentTypes;
import dev.satherov.growthacceleratortiers.core.definitions.GATBlockEntities;
import dev.satherov.growthacceleratortiers.core.definitions.GATBlocks;
import dev.satherov.growthacceleratortiers.core.definitions.GATItems;
import dev.satherov.growthacceleratortiers.core.events.GATPlayerInteractEvent;
import dev.satherov.growthacceleratortiers.core.events.GATTooltipEvent;
import dev.satherov.growthacceleratortiers.init.GATInitCapabilityProviders;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;

@Mod(GAT.MOD_ID)
public class GrowthAcceleratorTiers {
    public GrowthAcceleratorTiers(IEventBus modEventBus, ModContainer modContainer) {
        
        GATConfig.register(modContainer);
        
        GATBlocks.DR.register(modEventBus);
        GATItems.DR.register(modEventBus);
        GATBlockEntities.DR.register(modEventBus);
        GATAttachmentTypes.register(modEventBus);
        
        NeoForge.EVENT_BUS.addListener(GATPlayerInteractEvent::onPlayerUseBlockEvent);
        NeoForge.EVENT_BUS.addListener(GATTooltipEvent::registerTooltips);
        NeoForge.EVENT_BUS.addListener(GrowthAcceleratorTiers::onRegisterCommands);
        
        modEventBus.addListener(GATInitCapabilityProviders::register);
        modEventBus.addListener((RegisterEvent event) -> {
            if (event.getRegistryKey() == Registries.CREATIVE_MODE_TAB) {
                GATCreativeTab.init(BuiltInRegistries.CREATIVE_MODE_TAB);
            }
        });
        
        if (FMLEnvironment.dist.isClient()) {
            Client.registerConfigScreen(modContainer);
        }
    }
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        GATCommands.register(event.getDispatcher());
    }
    
    static class Client {
        
        public static void registerConfigScreen(ModContainer modContainer) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }
}