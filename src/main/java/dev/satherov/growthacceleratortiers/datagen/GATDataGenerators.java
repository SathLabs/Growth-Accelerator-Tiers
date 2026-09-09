package dev.satherov.growthacceleratortiers.datagen;

import dev.satherov.growthacceleratortiers.api.GAT;
import dev.satherov.growthacceleratortiers.datagen.providers.localization.GATLocalizationProvider;
import dev.satherov.growthacceleratortiers.datagen.providers.loot.GATLootTableProvider;
import dev.satherov.growthacceleratortiers.datagen.providers.models.GATBlockModelProvider;
import dev.satherov.growthacceleratortiers.datagen.providers.models.GATItemModelProvider;
import dev.satherov.growthacceleratortiers.datagen.providers.recipe.GATRecipeProvider;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = GAT.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class GATDataGenerators {
    
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        
        var generator = event.getGenerator();
        var registries = event.getLookupProvider();
        var localization = new GATLocalizationProvider(generator);
        var pack = generator.getVanillaPack(true);
        var existingFileHelper = event.getExistingFileHelper();
        
        pack.addProvider(packOutput -> new GATLootTableProvider(packOutput, registries));
        
        // Models
        pack.addProvider(packOutput -> new GATBlockModelProvider(packOutput, existingFileHelper));
        pack.addProvider(packOutput -> new GATItemModelProvider(packOutput, existingFileHelper));
        pack.addProvider(packOutput -> new GATRecipeProvider(packOutput, registries));
        
        pack.addProvider(packOutput -> localization);
    }
}
