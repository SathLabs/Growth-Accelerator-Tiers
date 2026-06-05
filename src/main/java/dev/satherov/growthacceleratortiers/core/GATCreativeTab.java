package dev.satherov.growthacceleratortiers.core;

import dev.satherov.growthacceleratortiers.api.ids.GATCreativeTabIds;
import dev.satherov.growthacceleratortiers.core.definitions.GATBlocks;

import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.ItemDefinition;
import appeng.core.localization.GuiText;
import appeng.items.AEBaseItem;

import java.util.ArrayList;
import java.util.List;

public class GATCreativeTab {
    
    private static final Multimap<ResourceKey<CreativeModeTab>, ItemDefinition<?>> externalItemDefs = HashMultimap
            .create();
    private static final List<ItemDefinition<?>> itemDefs = new ArrayList<>();
    
    public static void init(Registry<CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .title(GuiText.CreativeTab.text())
                .icon(() -> GATBlocks.CRANKED_GROWTH_ACCELERATOR.stack(1))
                .displayItems(GATCreativeTab::buildDisplayItems)
                .build();
        Registry.register(registry, GATCreativeTabIds.MAIN, tab);
    }
    
    public static void initExternal(BuildCreativeModeTabContentsEvent contents) {
        for (var itemDefinition : GATCreativeTab.externalItemDefs.get(contents.getTabKey())) {
            contents.accept(itemDefinition);
        }
    }
    
    public static void add(ItemDefinition<?> itemDef) {
        GATCreativeTab.itemDefs.add(itemDef);
    }
    
    public static void addExternal(ResourceKey<CreativeModeTab> tab, ItemDefinition<?> itemDef) {
        GATCreativeTab.externalItemDefs.put(tab, itemDef);
    }
    
    private static void buildDisplayItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        for (var itemDef : GATCreativeTab.itemDefs) {
            var item = itemDef.asItem();
            
            // For block items, the block controls the creative tab
            if (item instanceof AEBaseBlockItem baseItem
                    && baseItem.getBlock() instanceof AEBaseBlock baseBlock) {
                baseBlock.addToMainCreativeTab(itemDisplayParameters, output);
            } else if (item instanceof AEBaseItem baseItem) {
                baseItem.addToMainCreativeTab(itemDisplayParameters, output);
            } else {
                output.accept(itemDef);
            }
        }
    }
}