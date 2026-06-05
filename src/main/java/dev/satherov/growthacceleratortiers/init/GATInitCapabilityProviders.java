package dev.satherov.growthacceleratortiers.init;

import dev.satherov.growthacceleratortiers.blockentity.GATGrowthAcceleratorBlockEntity;
import dev.satherov.growthacceleratortiers.core.definitions.GATBlockEntities;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import appeng.api.AECapabilities;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.blockentity.AEBaseInvBlockEntity;
import appeng.blockentity.powersink.AEBasePoweredBlockEntity;

public class GATInitCapabilityProviders {
    
    public static void register(RegisterCapabilitiesEvent event) {
        GATInitCapabilityProviders.initCrankable(event);
        
        for (var type : GATBlockEntities.getSubclassesOf(AEBaseInvBlockEntity.class)) {
            event.registerBlockEntity(Capabilities.Item.BLOCK, type, AEBaseInvBlockEntity::getExposedItemHandler);
        }
        for (var type : GATBlockEntities.getSubclassesOf(AEBasePoweredBlockEntity.class)) {
            event.registerBlockEntity(Capabilities.Energy.BLOCK, type, AEBasePoweredBlockEntity::getEnergyStorage);
        }
        for (var type : GATBlockEntities.getImplementorsOf(IInWorldGridNodeHost.class)) {
            event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, type, (object, context) -> (IInWorldGridNodeHost) object);
        }
    }
    
    private static void initCrankable(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(AECapabilities.CRANKABLE, GATBlockEntities.CRANKED_GROWTH_ACCELERATOR.get(), GATGrowthAcceleratorBlockEntity::getCrankable);
        event.registerBlockEntity(AECapabilities.CRANKABLE, GATBlockEntities.DIRECTIONAL_GROWTH_ACCELERATOR.get(), GATGrowthAcceleratorBlockEntity::getCrankable);
        event.registerBlockEntity(AECapabilities.CRANKABLE, GATBlockEntities.BOOSTED_GROWTH_ACCELERATOR.get(), GATGrowthAcceleratorBlockEntity::getCrankable);
    }
}
