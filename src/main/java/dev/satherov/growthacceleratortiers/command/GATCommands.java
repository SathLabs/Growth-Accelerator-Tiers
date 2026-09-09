package dev.satherov.growthacceleratortiers.command;

import dev.satherov.growthacceleratortiers.api.GAT;
import dev.satherov.growthacceleratortiers.core.definitions.GATAttachmentTypes;
import dev.satherov.growthacceleratortiers.data.PositionAttachment;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class GATCommands {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(GAT.MOD_ID)
                        .then(GATCommands.infoCommand())
                        .then(GATCommands.modifyCommand())
        
        );
        dispatcher.register(
                Commands.literal("gat")
                        .then(GATCommands.infoCommand())
                        .then(GATCommands.modifyCommand())
        );
    }
    
    private static LiteralArgumentBuilder<CommandSourceStack> infoCommand() {
        return Commands.literal("info")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(GATCommands::info)
                );
    }
    
    private static LiteralArgumentBuilder<CommandSourceStack> modifyCommand() {
        return Commands.literal("modify")
                .requires(cs -> cs.hasPermission(4))
                .then(Commands.literal("boosted")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(GATCommands::modifyBoosted)
                                )
                        )
                )
                .then(Commands.literal("directional")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(GATCommands::modifyDirectional)
                                )
                        )
                );
    }
    
    private static int info(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
        ServerLevel level = ctx.getSource().getLevel();
        ChunkAccess chunk = level.getChunkAt(pos);
        
        boolean boosted = chunk.hasData(GATAttachmentTypes.BOOSTED_POSITION.get());
        boolean directional = chunk.hasData(GATAttachmentTypes.DIRECTIONAL_POSITION.get());
        
        if (!boosted && !directional) {
            ctx.getSource().sendFailure(Component.translatable("command.growthacceleratortiers.no_data"));
            return -1;
        }
        
        MutableComponent message = Component.translatable("command.growthacceleratortiers.info");
        if (boosted) {
            message.append("\n - ").append(Component.translatable("command.growthacceleratortiers.boosted_position", chunk.getData(GATAttachmentTypes.BOOSTED_POSITION.get()).get(pos)));
        }
        if (directional) {
            message.append("\n - ").append(Component.translatable("command.growthacceleratortiers.directional_position", chunk.getData(GATAttachmentTypes.DIRECTIONAL_POSITION.get()).get(pos)));
        }
        ctx.getSource().sendSuccess(() -> message, false);
        
        return 0;
    }
    
    private static int modifyBoosted(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
        ServerLevel level = ctx.getSource().getLevel();
        ChunkAccess chunk = level.getChunkAt(pos);
        
        if (!chunk.hasData(GATAttachmentTypes.BOOSTED_POSITION.get())) {
            ctx.getSource().sendFailure(Component.translatable("command.growthacceleratortiers.no_data"));
            return -1;
        }
        
        PositionAttachment data = chunk.getData(GATAttachmentTypes.BOOSTED_POSITION.get());
        MutableComponent message = Component.translatable("command.growthacceleratortiers.modify");
        
        int before = data.get(pos);
        int after = IntegerArgumentType.getInteger(ctx, "value");
        
        data.put(pos, after);
        chunk.setUnsaved(true);
        
        message.append("\n - ").append(Component.translatable("command.growthacceleratortiers.boosted_position", before + " -> " + after));
        ctx.getSource().sendSuccess(() -> message, false);
        return 0;
    }
    
    private static int modifyDirectional(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
        ServerLevel level = ctx.getSource().getLevel();
        ChunkAccess chunk = level.getChunkAt(pos);
        
        if (!chunk.hasData(GATAttachmentTypes.DIRECTIONAL_POSITION.get())) {
            ctx.getSource().sendFailure(Component.translatable("command.growthacceleratortiers.no_data"));
            return -1;
        }
        
        PositionAttachment data = chunk.getData(GATAttachmentTypes.DIRECTIONAL_POSITION.get());
        MutableComponent message = Component.translatable("command.growthacceleratortiers.modify");
        
        int before = data.get(pos);
        int after = IntegerArgumentType.getInteger(ctx, "value");
        
        data.put(pos, after);
        chunk.setUnsaved(true);
        
        message.append("\n - ").append(Component.translatable("command.growthacceleratortiers.directional_position", before + " -> " + after));
        ctx.getSource().sendSuccess(() -> message, false);
        return 0;
    }
}
