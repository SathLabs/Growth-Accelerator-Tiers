package dev.satherov.growthacceleratortiers.datagen.providers.recipe;

import dev.satherov.growthacceleratortiers.core.definitions.GATBlocks;
import dev.satherov.growthacceleratortiers.core.definitions.GATItems;

import net.neoforged.neoforge.common.Tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.datagen.providers.tags.ConventionTags;

import java.util.concurrent.CompletableFuture;

public class GATRecipeProvider extends RecipeProvider {
    public GATRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
    
    @Override
    protected void buildRecipes(RecipeOutput output) {
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GATBlocks.CRANKED_GROWTH_ACCELERATOR)
                .pattern("DCD")
                .pattern("BAB")
                .pattern("DCD")
                .define('A', ItemTags.PLANKS)
                .define('B', Tags.Items.INGOTS_IRON)
                .define('C', AEBlocks.CRANK)
                .define('D', Tags.Items.COBBLESTONES)
                .unlockedBy("has_crank", RecipeProvider.has(AEBlocks.CRANK))
                .save(output);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GATBlocks.DIRECTIONAL_GROWTH_ACCELERATOR)
                .pattern("ECE")
                .pattern("BAB")
                .pattern("DFD")
                .define('A', AEBlocks.GROWTH_ACCELERATOR)
                .define('B', Tags.Items.GEMS_DIAMOND)
                .define('C', AEItems.FLUIX_PEARL)
                .define('D', Tags.Items.INGOTS_NETHERITE)
                .define('E', AEItems.SPEED_CARD)
                .define('F', AEItems.FORMATION_CORE)
                .unlockedBy("had_accelerator", RecipeProvider.has(AEBlocks.GROWTH_ACCELERATOR))
                .save(output);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GATBlocks.BOOSTED_GROWTH_ACCELERATOR)
                .pattern("ECE")
                .pattern("BAB")
                .pattern("DFD")
                .define('A', AEBlocks.GROWTH_ACCELERATOR)
                .define('B', Tags.Items.GEMS_EMERALD)
                .define('C', AEItems.FLUIX_PEARL)
                .define('D', Tags.Items.INGOTS_NETHERITE)
                .define('E', AEItems.SPEED_CARD)
                .define('F', AEItems.ANNIHILATION_CORE)
                .unlockedBy("had_accelerator", RecipeProvider.has(AEBlocks.GROWTH_ACCELERATOR))
                .save(output);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GATItems.DIRECTIONAL_MODIFIER)
                .pattern(" B ")
                .pattern(" AB")
                .pattern("C  ")
                .define('A', ConventionTags.WRENCH)
                .define('B', AEItems.FLUIX_PEARL)
                .define('C', ConventionTags.CERTUS_QUARTZ)
                .unlockedBy("has_wrench", RecipeProvider.has(ConventionTags.WRENCH))
                .save(output);
    }
}
