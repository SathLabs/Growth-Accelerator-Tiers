package dev.satherov.growthacceleratortiers.datagen.providers.recipe;

import dev.satherov.growthacceleratortiers.core.definitions.GATBlocks;
import dev.satherov.growthacceleratortiers.core.definitions.GATItems;

import net.neoforged.neoforge.common.Tags;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import appeng.core.ConventionTags;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

import java.util.concurrent.CompletableFuture;

public class GATRecipeProvider extends RecipeProvider {
    
    private final HolderGetter<Item> itemGetter;
    
    protected GATRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.itemGetter = registries.lookupOrThrow(Registries.ITEM);
    }
    
    @Override
    protected void buildRecipes() {
        ShapedRecipeBuilder.shaped(this.itemGetter, RecipeCategory.MISC, GATBlocks.CRANKED_GROWTH_ACCELERATOR)
                .pattern("DCD")
                .pattern("BAB")
                .pattern("DCD")
                .define('A', ItemTags.PLANKS)
                .define('B', Tags.Items.INGOTS_IRON)
                .define('C', AEBlocks.CRANK)
                .define('D', Tags.Items.COBBLESTONES)
                .unlockedBy("has_crank", this.has(AEBlocks.CRANK))
                .save(this.output);
        
        ShapedRecipeBuilder.shaped(this.itemGetter, RecipeCategory.MISC, GATBlocks.DIRECTIONAL_GROWTH_ACCELERATOR)
                .pattern("ECE")
                .pattern("BAB")
                .pattern("DFD")
                .define('A', AEBlocks.GROWTH_ACCELERATOR)
                .define('B', Tags.Items.GEMS_DIAMOND)
                .define('C', AEItems.FLUIX_PEARL)
                .define('D', Tags.Items.INGOTS_NETHERITE)
                .define('E', AEItems.SPEED_CARD)
                .define('F', AEItems.FORMATION_CORE)
                .unlockedBy("had_accelerator", this.has(AEBlocks.GROWTH_ACCELERATOR))
                .save(this.output);
        
        ShapedRecipeBuilder.shaped(this.itemGetter, RecipeCategory.MISC, GATBlocks.BOOSTED_GROWTH_ACCELERATOR)
                .pattern("ECE")
                .pattern("BAB")
                .pattern("DFD")
                .define('A', AEBlocks.GROWTH_ACCELERATOR)
                .define('B', Tags.Items.GEMS_EMERALD)
                .define('C', AEItems.FLUIX_PEARL)
                .define('D', Tags.Items.INGOTS_NETHERITE)
                .define('E', AEItems.SPEED_CARD)
                .define('F', AEItems.ANNIHILATION_CORE)
                .unlockedBy("had_accelerator", this.has(AEBlocks.GROWTH_ACCELERATOR))
                .save(this.output);
        
        ShapedRecipeBuilder.shaped(this.itemGetter, RecipeCategory.MISC, GATItems.DIRECTIONAL_MODIFIER)
                .pattern(" B ")
                .pattern(" AB")
                .pattern("C  ")
                .define('A', ConventionTags.WRENCH)
                .define('B', AEItems.FLUIX_PEARL)
                .define('C', ConventionTags.CERTUS_QUARTZ)
                .unlockedBy("has_wrench", this.has(ConventionTags.WRENCH))
                .save(this.output);
    }
    
    public static class Runner extends RecipeProvider.Runner {
        
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }
        
        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new GATRecipeProvider(registries, output);
        }
        
        @Override
        public String getName() {
            return "Recipes: Growth Accelerator Tiers";
        }
    }
}
