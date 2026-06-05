package dev.satherov.growthacceleratortiers.datagen;

import dev.satherov.growthacceleratortiers.api.GAT;
import dev.satherov.growthacceleratortiers.datagen.providers.localization.GATLocalizationProvider;
import dev.satherov.growthacceleratortiers.datagen.providers.loot.GATLootTableProvider;
import dev.satherov.growthacceleratortiers.datagen.providers.models.GATModelProvider;
import dev.satherov.growthacceleratortiers.datagen.providers.recipe.GATRecipeProvider;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = GAT.MOD_ID)
public class GATDataGenerators {
    
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        
        var generator = event.getGenerator();
        var registries = event.getLookupProvider();
        var pack = generator.getVanillaPack(true);
        
        pack.addProvider(packOutput -> new GATLootTableProvider(packOutput, registries));
        pack.addProvider(GATModelProvider::new);
        pack.addProvider(packOutput -> new GATRecipeProvider.Runner(packOutput, registries));
        pack.addProvider(_ -> new GATLocalizationProvider(generator));
    }
}
