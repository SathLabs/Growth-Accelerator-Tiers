package dev.satherov.growthacceleratortiers.datagen.providers.models;

import dev.satherov.growthacceleratortiers.api.GAT;
import dev.satherov.growthacceleratortiers.core.definitions.GATItems;

import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.data.PackOutput;

import appeng.core.definitions.ItemDefinition;

public class GATItemModelProvider extends ItemModelProvider {
    
    public GATItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, GAT.MOD_ID, existingFileHelper);
    }
    
    @Override
    protected void registerModels() {
        this.flatSingleLayer(GATItems.DIRECTIONAL_MODIFIER, "directional_modifier");
    }
    
    private ItemModelBuilder flatSingleLayer(ItemDefinition<?> item, String texture) {
        String id = item.id().getPath();
        return this.singleTexture(
                id,
                this.mcLoc("item/generated"),
                "layer0",
                GAT.rl("item/" + texture));
    }
}
