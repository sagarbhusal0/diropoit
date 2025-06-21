package com.example.diropoint.render;

import com.example.diropoint.DiroPointModClient;
import com.example.diropoint.waypoint.Waypoint;
import com.example.diropoint.WaypointManager;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class WaypointRenderer {
    private static final float BEAM_WIDTH = 0.5f;
    private static final float DEFAULT_BEAM_HEIGHT = 256.0f;
    private static final float MIN_ALPHA = 0.1f;
    private static final float MAX_ALPHA = 0.8f;

    public static void renderWaypoints(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !DiroPointModClient.CONFIG.showInWorld) return;

        Vec3d cameraPos = context.camera().getPos();
        float renderDistanceSquared = (float) Math.pow(DiroPointModClient.CONFIG.renderDistance, 2);

        Matrix4f positionMatrix = context.matrixStack().peek().getPositionMatrix();
        RenderLayer renderLayer = DiroPointModClient.CONFIG.renderThroughBlocks ? 
            RenderLayer.getBeaconBeam(null, true) : 
            RenderLayer.getBeaconBeam(null, false);

        // Get vertex consumers and ensure it's the immediate type
        VertexConsumerProvider consumers = context.consumers();
        if (!(consumers instanceof VertexConsumerProvider.Immediate)) return;
        VertexConsumerProvider.Immediate immediate = (VertexConsumerProvider.Immediate) consumers;
        
        VertexConsumer vertexConsumer = immediate.getBuffer(renderLayer);

        for (Waypoint waypoint : WaypointManager.getInstance().getWaypoints(client.player.getWorld().getDimension())) {
            Vec3d waypointPos = waypoint.getPosition();
            double distanceSquared = cameraPos.squaredDistanceTo(waypointPos);

            if (distanceSquared > renderDistanceSquared) continue;

            float alpha = calculateAlpha(distanceSquared, renderDistanceSquared);
            renderBeam(positionMatrix, vertexConsumer, cameraPos, waypointPos, waypoint.getColor(), alpha);
        }

        immediate.draw();
    }

    private static void renderBeam(Matrix4f matrix, VertexConsumer vertices, Vec3d camera, Vec3d waypoint, int color, float alpha) {
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        float x = (float) (waypoint.x - camera.x);
        float y = (float) (waypoint.y - camera.y);
        float z = (float) (waypoint.z - camera.z);

        renderBeamSegment(matrix, vertices, x, y, z, r, g, b, alpha);
    }

    private static void renderBeamSegment(Matrix4f matrix, VertexConsumer vertices, float x, float y, float z, float r, float g, float b, float alpha) {
        float halfWidth = BEAM_WIDTH / 2.0f;

        // Bottom face
        vertices.vertex(matrix, x - halfWidth, y, z - halfWidth).color(r, g, b, alpha).next();
        vertices.vertex(matrix, x - halfWidth, y, z + halfWidth).color(r, g, b, alpha).next();
        vertices.vertex(matrix, x + halfWidth, y, z + halfWidth).color(r, g, b, alpha).next();
        vertices.vertex(matrix, x + halfWidth, y, z - halfWidth).color(r, g, b, alpha).next();

        // Top face
        float topY = y + DEFAULT_BEAM_HEIGHT;
        vertices.vertex(matrix, x - halfWidth, topY, z - halfWidth).color(r, g, b, 0).next();
        vertices.vertex(matrix, x - halfWidth, topY, z + halfWidth).color(r, g, b, 0).next();
        vertices.vertex(matrix, x + halfWidth, topY, z + halfWidth).color(r, g, b, 0).next();
        vertices.vertex(matrix, x + halfWidth, topY, z - halfWidth).color(r, g, b, 0).next();

        // Side faces
        vertices.vertex(matrix, x - halfWidth, y, z - halfWidth).color(r, g, b, alpha).next();
        vertices.vertex(matrix, x - halfWidth, topY, z - halfWidth).color(r, g, b, 0).next();
        vertices.vertex(matrix, x - halfWidth, topY, z + halfWidth).color(r, g, b, 0).next();
        vertices.vertex(matrix, x - halfWidth, y, z + halfWidth).color(r, g, b, alpha).next();

        vertices.vertex(matrix, x + halfWidth, y, z + halfWidth).color(r, g, b, alpha).next();
        vertices.vertex(matrix, x + halfWidth, topY, z + halfWidth).color(r, g, b, 0).next();
        vertices.vertex(matrix, x + halfWidth, topY, z - halfWidth).color(r, g, b, 0).next();
        vertices.vertex(matrix, x + halfWidth, y, z - halfWidth).color(r, g, b, alpha).next();

        vertices.vertex(matrix, x - halfWidth, y, z + halfWidth).color(r, g, b, alpha).next();
        vertices.vertex(matrix, x - halfWidth, topY, z + halfWidth).color(r, g, b, 0).next();
        vertices.vertex(matrix, x + halfWidth, topY, z + halfWidth).color(r, g, b, 0).next();
        vertices.vertex(matrix, x + halfWidth, y, z + halfWidth).color(r, g, b, alpha).next();

        vertices.vertex(matrix, x + halfWidth, y, z - halfWidth).color(r, g, b, alpha).next();
        vertices.vertex(matrix, x + halfWidth, topY, z - halfWidth).color(r, g, b, 0).next();
        vertices.vertex(matrix, x - halfWidth, topY, z - halfWidth).color(r, g, b, 0).next();
        vertices.vertex(matrix, x - halfWidth, y, z - halfWidth).color(r, g, b, alpha).next();
    }

    private static float calculateAlpha(double distanceSquared, float maxDistanceSquared) {
        float distanceRatio = (float) (distanceSquared / maxDistanceSquared);
        return MIN_ALPHA + (MAX_ALPHA - MIN_ALPHA) * (1.0f - distanceRatio);
    }
} 