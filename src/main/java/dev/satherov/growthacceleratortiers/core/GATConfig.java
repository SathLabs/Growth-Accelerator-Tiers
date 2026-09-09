package dev.satherov.growthacceleratortiers.core;

import dev.satherov.growthacceleratortiers.api.GAT;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class GATConfig {
    
    private static GATConfig instance;
    private final ClientConfig client = new ClientConfig();
    private final CommonConfig common = new CommonConfig();
    
    private GATConfig(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, this.client.spec);
        container.registerConfig(ModConfig.Type.COMMON, this.common.spec);
    }
    
    public static void register(ModContainer container) {
        if (!container.getModId().equals(GAT.MOD_ID)) {
            throw new IllegalArgumentException();
        }
        GATConfig.instance = new GATConfig(container);
    }
    
    public static GATConfig instance() {
        return GATConfig.instance;
    }
    
    private static ModConfigSpec.BooleanValue define(ModConfigSpec.Builder builder, String name, boolean defaultValue, String comment) {
        builder.comment(comment);
        return GATConfig.define(builder, name, defaultValue);
    }
    
    private static ModConfigSpec.BooleanValue define(ModConfigSpec.Builder builder, String name, boolean defaultValue) {
        return builder.define(name, defaultValue);
    }
    
    private static ModConfigSpec.IntValue define(ModConfigSpec.Builder builder, String name, int defaultValue, String comment) {
        builder.comment(comment);
        return GATConfig.define(builder, name, defaultValue);
    }
    
    private static ModConfigSpec.DoubleValue define(ModConfigSpec.Builder builder, String name, double defaultValue) {
        return GATConfig.define(builder, name, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
    }
    
    private static ModConfigSpec.DoubleValue define(ModConfigSpec.Builder builder, String name, double defaultValue, String comment) {
        builder.comment(comment);
        return GATConfig.define(builder, name, defaultValue);
    }
    
    private static ModConfigSpec.DoubleValue define(ModConfigSpec.Builder builder, String name, double defaultValue, double min, double max, String comment) {
        builder.comment(comment);
        return GATConfig.define(builder, name, defaultValue, min, max);
    }
    
    private static ModConfigSpec.DoubleValue define(ModConfigSpec.Builder builder, String name, double defaultValue, double min, double max) {
        return builder.defineInRange(name, defaultValue, min, max);
    }
    
    private static ModConfigSpec.IntValue define(ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment) {
        builder.comment(comment);
        return GATConfig.define(builder, name, defaultValue, min, max);
    }
    
    private static ModConfigSpec.IntValue define(ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max) {
        return builder.defineInRange(name, defaultValue, min, max);
    }
    
    private static ModConfigSpec.IntValue define(ModConfigSpec.Builder builder, String name, int defaultValue) {
        return builder.defineInRange(name, defaultValue, 0, Integer.MAX_VALUE);
    }
    
    private static <T extends Enum<T>> ModConfigSpec.EnumValue<T> defineEnum(ModConfigSpec.Builder builder, String name, T defaultValue) {
        return builder.defineEnum(name, defaultValue);
    }
    
    private static <T extends Enum<T>> ModConfigSpec.EnumValue<T> defineEnum(ModConfigSpec.Builder builder, String name, T defaultValue, String comment) {
        builder.comment(comment);
        return GATConfig.defineEnum(builder, name, defaultValue);
    }
    
    public boolean enableConflicts() {
        return this.common.enableConflicts.get();
    }
    
    public int getBoostedInternalEnergyMultiplier() {
        return this.common.boostedInternalEnergyMultiplier.get();
    }
    
    public int getBoostedIdlePowerConsumption() {
        return this.common.boostedIdlePowerConsumption.get();
    }
    
    public double getBoostedSpeedMultiplier() {
        return this.common.boostedSpeedMultiplier.get();
    }
    
    public int getCrankedInternalEnergyMultiplier() {
        return this.common.crankedInternalEnergyMultiplier.get();
    }
    
    public int getCrankedIdlePowerConsumption() {
        return this.common.crankedIdlePowerConsumption.get();
    }
    
    public double getCrankedSpeedMultiplier() {
        return this.common.crankedSpeedMultiplier.get();
    }
    
    public int getDirectionalInternalEnergyMultiplier() {
        return this.common.directionalInternalEnergyMultiplier.get();
    }
    
    public int getDirectionalIdlePowerConsumption() {
        return this.common.directionalIdlePowerConsumption.get();
    }
    
    public double getDirectionalSpeedMultiplier() {
        return this.common.directionalSpeedMultiplier.get();
    }
    
    static class ClientConfig {
        private final ModConfigSpec spec;
        
        public ClientConfig() {
            var builder = new ModConfigSpec.Builder();
            this.spec = builder.build();
        }
    }
    
    static class CommonConfig {
        private final ModConfigSpec spec;
        
        public ModConfigSpec.BooleanValue enableConflicts;
        
        public ModConfigSpec.IntValue boostedInternalEnergyMultiplier;
        public ModConfigSpec.IntValue boostedIdlePowerConsumption;
        public ModConfigSpec.DoubleValue boostedSpeedMultiplier;
        
        public ModConfigSpec.IntValue crankedInternalEnergyMultiplier;
        public ModConfigSpec.IntValue crankedIdlePowerConsumption;
        public ModConfigSpec.DoubleValue crankedSpeedMultiplier;
        
        public ModConfigSpec.IntValue directionalInternalEnergyMultiplier;
        public ModConfigSpec.IntValue directionalIdlePowerConsumption;
        public ModConfigSpec.DoubleValue directionalSpeedMultiplier;
        
        public CommonConfig() {
            var builder = new ModConfigSpec.Builder();
            
            builder.push("general");
            
            this.enableConflicts = GATConfig.define(builder,
                    "enable_conflicts",
                    true,
                    "If set to false, the Boosted / Directional Accelerator will not conflict with other Accelerators of the same type"
            );
            
            builder.pop();
            
            builder.push("boosted_growth_accelerator");
            
            this.boostedInternalEnergyMultiplier = GATConfig.define(builder,
                    "boosted_internal_energy_multiplier",
                    60, 1, Integer.MAX_VALUE,
                    "Defines the Multiplier for the internal power inventory of the Boosted Growth Accelerator.\n 10 would imitate the normal AE2 Growth Accelerator"
            );
            
            this.boostedIdlePowerConsumption = GATConfig.define(builder,
                    "boosted_idle_power_consumption",
                    24, 1, Integer.MAX_VALUE,
                    "Defines the power consumption of the Boosted Growth Accelerator consumed while running.\n 8 would imitate the normal AE2 Growth Accelerator"
            );
            
            this.boostedSpeedMultiplier = GATConfig.define(builder,
                    "boosted_speed_multiplier",
                    8.0f, 1.0f, Double.MAX_VALUE,
                    "Defines the multiplier with which the Boosted Growth Accelerator will tick the adjacent blocks.\n 1.0f would imitate the normal AE2 Growth Accelerator"
            );
            
            builder.pop();
            
            builder.push("cranked_growth_accelerator");
            
            this.crankedInternalEnergyMultiplier = GATConfig.define(builder,
                    "cranked_internal_energy_multiplier",
                    20, 1, Integer.MAX_VALUE,
                    "Defines the Multiplier for the internal power inventory of the Cranked Growth Accelerator.\n 10 would imitate the normal AE2 Growth Accelerator"
            );
            
            this.crankedIdlePowerConsumption = GATConfig.define(builder,
                    "cranked_idle_power_consumption",
                    8, 1, Integer.MAX_VALUE,
                    "Defines the power consumption of the Cranked Growth Accelerator consumed while running.\n 8 would imitate the normal AE2 Growth Accelerator"
            );
            
            this.crankedSpeedMultiplier = GATConfig.define(builder,
                    "cranked_speed_multiplier",
                    8.0f, 1.0f, Double.MAX_VALUE,
                    "Defines the multiplier with which the Cranked Growth Accelerator will tick the adjacent blocks.\n 1.0f would imitate the normal AE2 Growth Accelerator"
            );
            
            builder.pop();
            
            builder.push("directional_growth_accelerator");
            
            this.directionalInternalEnergyMultiplier = GATConfig.define(builder,
                    "directional_internal_energy_multiplier",
                    20, 1, Integer.MAX_VALUE,
                    "Defines the Multiplier for the internal power inventory of the Directional Growth Accelerator.\n 10 would imitate the normal AE2 Growth Accelerator"
            );
            
            this.directionalIdlePowerConsumption = GATConfig.define(builder,
                    "directional_idle_power_consumption",
                    12, 1, Integer.MAX_VALUE,
                    "Defines the power consumption of the Directional Growth Accelerator consumed while running.\n 8 would imitate the normal AE2 Growth Accelerator"
            );
            
            this.directionalSpeedMultiplier = GATConfig.define(builder,
                    "directional_speed_multiplier",
                    2.0f, 1.0f, Double.MAX_VALUE,
                    "Defines the multiplier with which the Directional Growth Accelerator will tick the adjacent blocks.\n 1.0f would imitate the normal AE2 Growth Accelerator"
            );
            
            builder.pop();
            
            this.spec = builder.build();
        }
    }
}
