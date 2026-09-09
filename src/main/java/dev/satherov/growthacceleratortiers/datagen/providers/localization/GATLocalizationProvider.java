package dev.satherov.growthacceleratortiers.datagen.providers.localization;

import dev.satherov.growthacceleratortiers.core.annotations.NothingNull;
import dev.satherov.growthacceleratortiers.core.definitions.GATBlocks;
import dev.satherov.growthacceleratortiers.core.definitions.GATItems;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import appeng.datagen.providers.IAE2DataProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

@NothingNull
public class GATLocalizationProvider implements IAE2DataProvider {
    
    private final Map<String, String> localizations = new HashMap<>();
    
    private final DataGenerator generator;
    
    private boolean wasSaved = false;
    
    public GATLocalizationProvider(DataGenerator generator) {
        this.generator = generator;
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        
        this.add("itemGroup.growthacceleratortiers", "Growth Accelerator Tiers");
        
        for (var block : GATBlocks.getBlocks()) {
            this.add("block.growthacceleratortiers." + block.id().getPath(), block.getEnglishName());
        }
        for (var item : GATItems.getItems()) {
            this.add("item.growthacceleratortiers." + item.id().getPath(), item.getEnglishName());
        }
        
        
        this.add("key.growthacceleratortiers.modifier", "Modifier");
        this.add("key.growthacceleratortiers.categories", "Growth Accelerator Tiers");
        
        this.add("tooltip.growthacceleratortiers.cranked_growth_accelerator", "Does not connect to a network, only works when powered by the Wooden Crank");
        this.add("tooltip.growthacceleratortiers.boosted_growth_accelerator", "Faster than your average Growth Accelerator");
        this.add("tooltip.growthacceleratortiers.directional_growth_accelerator", "Forces buds to grow in a specific direction, up by default");
        this.add("tooltip.growthacceleratortiers.change_direction", "Right click with a directional modifier to change the direction");
        
        this.add("tooltip.growthacceleratortiers.conflicted", "Accelerator conflict");
        
        this.add("direction.growthacceleratortiers", "Direction");
        
        this.add("direction.growthacceleratortiers.up", "Up");
        this.add("direction.growthacceleratortiers.down", "Down");
        this.add("direction.growthacceleratortiers.north", "North");
        this.add("direction.growthacceleratortiers.south", "South");
        this.add("direction.growthacceleratortiers.east", "East");
        this.add("direction.growthacceleratortiers.west", "West");
        this.add("direction.growthacceleratortiers.opposite", "Opposite");
        
        this.add("config.jade.plugin_growthacceleratortiers.boosted_growth_accelerator", "Boosted Growth Accelerator");
        this.add("config.jade.plugin_growthacceleratortiers.directional_growth_accelerator", "Directional Growth Accelerator");
        
        this.add("command.growthacceleratortiers.info", "Found the following data on this block:");
        this.add("command.growthacceleratortiers.modify", "Modified the following data on this block:");
        this.add("command.growthacceleratortiers.no_data", "No data found on the block");
        this.add("command.growthacceleratortiers.boosted_position", "Boosted Data: %s");
        this.add("command.growthacceleratortiers.directional_position", "Directional Data: %s");
        
        this.add("growthacceleratortiers.configuration.general", "General");
        this.add("growthacceleratortiers.configuration.enable_conflicts", "Enable Conflicts");
        
        this.add("growthacceleratortiers.configuration.boosted_growth_accelerator", "Boosted Growth Accelerator");
        this.add("growthacceleratortiers.configuration.boosted_internal_energy_multiplier", "Boosted Internal Energy Multiplier");
        this.add("growthacceleratortiers.configuration.boosted_idle_power_consumption", "Boosted Idle Power Consumption");
        this.add("growthacceleratortiers.configuration.boosted_speed_multiplier", "Boosted Speed Multiplier");
        
        this.add("growthacceleratortiers.configuration.cranked_growth_accelerator", "Cranked Growth Accelerator");
        this.add("growthacceleratortiers.configuration.cranked_internal_energy_multiplier", "Cranked Internal Energy Multiplier");
        this.add("growthacceleratortiers.configuration.cranked_idle_power_consumption", "Cranked Idle Power Consumption");
        this.add("growthacceleratortiers.configuration.cranked_speed_multiplier", "Cranked Speed Multiplier");
        
        this.add("growthacceleratortiers.configuration.directional_growth_accelerator", "Directional Growth Accelerator");
        this.add("growthacceleratortiers.configuration.directional_internal_energy_multiplier", "Directional Internal Energy Multiplier");
        this.add("growthacceleratortiers.configuration.directional_idle_power_consumption", "Directional Idle Power Consumption");
        this.add("growthacceleratortiers.configuration.directional_speed_multiplier", "Directional Speed Multiplier");
        
        return this.save(cache, this.localizations);
    }
    
    public void add(String key, String text) {
        Preconditions.checkState(!this.wasSaved, "Cannot add more translations after they were already saved");
        var previous = this.localizations.put(key, text);
        if (previous != null) {
            throw new IllegalStateException("Localization key " + key + " is already translated to: " + previous);
        }
    }
    
    private CompletableFuture<?> save(CachedOutput cache, Map<String, String> localizations) {
        this.wasSaved = true;
        
        var path = this.generator.getPackOutput().getOutputFolder().resolve("assets/growthacceleratortiers/lang/en_us.json");
        
        var sorted = new TreeMap<>(localizations);
        var jsonLocalization = new JsonObject();
        for (var entry : sorted.entrySet()) {
            jsonLocalization.addProperty(entry.getKey(), entry.getValue());
        }
        
        return DataProvider.saveStable(cache, jsonLocalization, path);
    }
    
    @Override
    public String getName() {
        return "GAT Localization (en_us)";
    }
}
