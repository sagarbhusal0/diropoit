package com.example.diropoint;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class WaypointTeleportCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("diropoint")
            .then(CommandManager.literal("teleport")
            .then(CommandManager.argument("waypointName", StringArgumentType.string())
            .executes(context -> {
                ServerCommandSource source = context.getSource();
                ServerPlayerEntity player = source.getPlayer();
                if (player == null) return 0;
                
                String waypointName = StringArgumentType.getString(context, "waypointName");
                // In a real implementation, we would look up the waypoint and teleport the player
                player.sendMessage(Text.literal("Teleporting to waypoint: " + waypointName), false);
                return 1;
            }))));
    }
}
