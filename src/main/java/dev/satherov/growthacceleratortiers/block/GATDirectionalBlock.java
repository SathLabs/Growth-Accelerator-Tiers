package dev.satherov.growthacceleratortiers.block;

import dev.satherov.growthacceleratortiers.blockentity.GATDirectionalBlockEntity;
import dev.satherov.growthacceleratortiers.core.definitions.GATAttachmentTypes;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class GATDirectionalBlock extends GATMonoBlock<GATDirectionalBlockEntity> {
    
    public static final EnumProperty<Directions> DIRECTION = EnumProperty.create("direction", Directions.class);
    
    public GATDirectionalBlock() {
        super(GATAttachmentTypes.DIRECTIONAL_POSITION);
        this.registerDefaultState(this.defaultBlockState().setValue(GATDirectionalBlock.DIRECTION, Directions.UP).setValue(GATMonoBlock.CONFLICTED, false));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(GATDirectionalBlock.DIRECTION);
    }
    
    public enum Directions implements StringRepresentable {
        UP,
        DOWN,
        NORTH,
        SOUTH,
        EAST,
        WEST,
        OPPOSITE;
        
        public Direction getDirection(Direction facing) {
            return switch (this) {
                case DOWN -> Direction.DOWN;
                case NORTH -> Direction.NORTH;
                case SOUTH -> Direction.SOUTH;
                case EAST -> Direction.EAST;
                case WEST -> Direction.WEST;
                case OPPOSITE -> facing;
                default -> Direction.UP;
            };
        }
        
        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
