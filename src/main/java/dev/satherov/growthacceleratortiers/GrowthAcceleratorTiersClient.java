package dev.satherov.growthacceleratortiers;

import dev.satherov.growthacceleratortiers.api.GAT;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = GAT.MOD_ID, dist = Dist.CLIENT)
public class GrowthAcceleratorTiersClient {
    
    public GrowthAcceleratorTiersClient(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
