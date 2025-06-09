package com.example.diropoint;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.dimension.DimensionType;

import java.util.List;

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
                
                // Get the current dimension's waypoints
                DimensionType dimension = player.getWorld().getDimension();
                List<Waypoint> waypoints = WaypointManager.getInstance().getWaypoints(dimension);
                
                // Find the waypoint by name
                Waypoint targetWaypoint = null;
                for (Waypoint waypoint : waypoints) {
                    if (waypoint.getName().equalsIgnoreCase(waypointName)) {
                        targetWaypoint = waypoint;
                        break;
                    }
                }
                
                if (targetWaypoint != null) {
                    // Teleport the player
                    player.teleport(targetWaypoint.getX(), targetWaypoint.getY(), targetWaypoint.getZ());
                    player.sendMessage(Text.literal("Teleported to waypoint: " + waypointName), false);
                    return 1;
                } else {
                    player.sendMessage(Text.literal("Waypoint not found: " + waypointName), false);
                    return 0;
                }
            }))));
    }

    public static void teleportToWaypoint(ClientPlayerEntity player, Waypoint waypoint) {
        if (player == null || waypoint == null || !player.hasPermissionLevel(2)) {
            return;
        }

        String command = String.format("tp %d %d %d",
            waypoint.getX(),
            waypoint.getY(),
            waypoint.getZ()
        );

        player.networkHandler.sendCommand(command);
    }
}
