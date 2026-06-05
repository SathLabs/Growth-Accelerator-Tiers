package dev.satherov.growthacceleratortiers.data;

import net.minecraft.core.BlockPos;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

import java.util.HashMap;
import java.util.Map;

public class PositionAttachment {
    
    public static final MapCodec<PositionAttachment> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.unboundedMap(Codec.STRING, Codec.INT)
                            .fieldOf("values")
                            .forGetter(attachment -> {
                                Map<String, Integer> map = new HashMap<>();
                                attachment.values.long2IntEntrySet().forEach(entry ->
                                        map.put(Long.toString(entry.getLongKey()), entry.getIntValue())
                                );
                                return map;
                            })
            ).apply(instance, PositionAttachment::new)
    );
    private final Long2IntOpenHashMap values = new Long2IntOpenHashMap();
    
    private PositionAttachment(Map<String, Integer> initial) {
        initial.forEach((key, value) -> this.values.put(Long.parseLong(key), (int) value));
    }
    
    public PositionAttachment() { }
    
    public void put(BlockPos pos, int value) {
        this.values.put(pos.immutable().asLong(), value);
    }
    
    public int get(BlockPos pos) {
        return this.values.getOrDefault(pos.immutable().asLong(), 0);
    }
}
