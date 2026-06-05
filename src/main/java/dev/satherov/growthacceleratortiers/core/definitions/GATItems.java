package dev.satherov.growthacceleratortiers.core.definitions;

import dev.satherov.growthacceleratortiers.api.GAT;
import dev.satherov.growthacceleratortiers.api.ids.GATCreativeTabIds;
import dev.satherov.growthacceleratortiers.api.ids.GATItemIds;
import dev.satherov.growthacceleratortiers.core.GATCreativeTab;
import dev.satherov.growthacceleratortiers.item.GATDirectionalModifier;

import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import com.google.common.base.Preconditions;

import org.jetbrains.annotations.Nullable;

import appeng.core.definitions.ItemDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class GATItems {
    
    public static final DeferredRegister.Items DR = DeferredRegister.createItems(GAT.MOD_ID);
    
    private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();
    
    public static final ItemDefinition<GATDirectionalModifier> DIRECTIONAL_MODIFIER = GATItems.item("Directional Modifier", GATItemIds.DIRECTIONAL_MODIFIER, GATDirectionalModifier::new);
    
    public static List<ItemDefinition<?>> getItems() {
        return GATItems.ITEMS;
    }
    
    static <T extends Item> ItemDefinition<T> item(String name, Identifier id, Function<Item.Properties, T> factory) {
        return GATItems.item(name, id, factory, GATCreativeTabIds.MAIN);
    }
    
    static <T extends Item> ItemDefinition<T> item(String name, Identifier id, Function<Item.Properties, T> factory, @Nullable ResourceKey<CreativeModeTab> group) {
        Preconditions.checkArgument(id.getNamespace().equals(GAT.MOD_ID), "Can only register for AE2");
        var definition = new ItemDefinition<>(name, GATItems.DR.registerItem(id.getPath(), factory));
        
        if (Objects.equals(group, GATCreativeTabIds.MAIN)) {
            GATCreativeTab.add(definition);
        } else if (group != null) {
            GATCreativeTab.add(definition);
            GATCreativeTab.addExternal(group, definition);
        }
        
        GATItems.ITEMS.add(definition);
        
        return definition;
    }
}
