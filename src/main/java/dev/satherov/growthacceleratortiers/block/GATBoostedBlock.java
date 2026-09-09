package dev.satherov.growthacceleratortiers.block;

import dev.satherov.growthacceleratortiers.blockentity.GATBoostedBlockEntity;
import dev.satherov.growthacceleratortiers.core.definitions.GATAttachmentTypes;

public class GATBoostedBlock extends GATMonoBlock<GATBoostedBlockEntity> {
    
    public GATBoostedBlock() {
        super(GATAttachmentTypes.BOOSTED_POSITION);
    }
}
