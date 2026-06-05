package dev.satherov.growthacceleratortiers.core.definitions;

import dev.satherov.growthacceleratortiers.api.GAT;
import dev.satherov.growthacceleratortiers.data.PositionAttachment;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;


public class GATAttachmentTypes {
    
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, GAT.MOD_ID);
    
    public static final Supplier<AttachmentType<PositionAttachment>> BOOSTED_POSITION = GATAttachmentTypes.ATTACHMENT_TYPES.register("boosted_position", () ->
            AttachmentType.builder(PositionAttachment::new)
                    .serialize(PositionAttachment.CODEC)
                    .build()
    );
    
    public static final Supplier<AttachmentType<PositionAttachment>> DIRECTIONAL_POSITION = GATAttachmentTypes.ATTACHMENT_TYPES.register("directional_position", () ->
            AttachmentType.builder(PositionAttachment::new)
                    .serialize(PositionAttachment.CODEC)
                    .build()
    );
    
    public static void register(IEventBus modEventBus) {
        GATAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
    }
}
