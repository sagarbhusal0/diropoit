package com.example.diropoint.render;

import com.example.diropoint.DiroPointModClient;
import com.example.diropoint.waypoint.Waypoint;
import com.example.diropoint.WaypointManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class WaypointHudRenderer {
    private static final int MARKER_SIZE = 8;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 1.5f;
    private static final int EDGE_PADDING = 10;
    private static final float MIN_OPACITY = 0.3f;
    private static final float MAX_OPACITY = 1.0f;
    private static final float DEFAULT_FOV = 70.0f;

    public static void render(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        Camera camera = client.gameRenderer.getCamera();
        TextRenderer textRenderer = client.textRenderer;

        Vec3d cameraPos = camera.getPos();
        float aspectRatio = (float) screenWidth / screenHeight;
        
        // Create projection matrix with default FOV
        Matrix4f projectionMatrix = new Matrix4f().perspective(
            (float) Math.toRadians(DEFAULT_FOV),
            aspectRatio,
            0.05f,  // near plane
            client.options.getViewDistance().getValue() * 16.0f  // far plane = render distance in blocks
        );

        // Create view matrix
        Matrix4f viewMatrix = new Matrix4f();
        camera.getRotation().get(viewMatrix);
        viewMatrix.translate((float)-cameraPos.x, (float)-cameraPos.y, (float)-cameraPos.z);

        for (Waypoint waypoint : WaypointManager.getInstance().getWaypoints(client.world.getDimension())) {
            Vec3d waypointPos = waypoint.getPosition();
            double distance = cameraPos.distanceTo(waypointPos);

            // Skip if too far
            if (distance > DiroPointModClient.CONFIG.renderDistance) continue;

            // Project 3D position to screen space
            Vector4f pos = new Vector4f(
                (float)waypointPos.x,
                (float)waypointPos.y,
                (float)waypointPos.z,
                1.0f
            );
            pos.mul(viewMatrix);
            pos.mul(projectionMatrix);

            // Skip if behind camera
            if (pos.z < 0) continue;

            // Perspective divide
            float w = pos.w;
            pos.div(w);

            // Convert to screen coordinates
            int screenX = (int)((pos.x * 0.5f + 0.5f) * screenWidth);
            int screenY = (int)((1.0f - (pos.y * 0.5f + 0.5f)) * screenHeight);

            // Clamp to screen edges with padding
            screenX = MathHelper.clamp(screenX, EDGE_PADDING, screenWidth - EDGE_PADDING);
            screenY = MathHelper.clamp(screenY, EDGE_PADDING, screenHeight - EDGE_PADDING);

            // Calculate scale and opacity based on distance
            float distanceRatio = (float) (distance / DiroPointModClient.CONFIG.renderDistance);
            float scale = MathHelper.lerp(distanceRatio, MAX_SCALE, MIN_SCALE);
            float opacity = MathHelper.lerp(distanceRatio, MAX_OPACITY, MIN_OPACITY);

            // Draw waypoint marker
            int color = waypoint.getColor();
            float alpha = opacity * ((color >> 24 & 0xFF) / 255.0f);
            int finalColor = (((int)(alpha * 255.0f) & 0xFF) << 24) | (color & 0x00FFFFFF);

            // Draw marker background
            int markerHalfSize = (int)(MARKER_SIZE * scale / 2);
            context.fill(
                screenX - markerHalfSize,
                screenY - markerHalfSize,
                screenX + markerHalfSize,
                screenY + markerHalfSize,
                finalColor
            );

            // Draw waypoint name and distance
            String text = String.format("%s (%dm)", waypoint.getName(), (int)distance);
            int textWidth = textRenderer.getWidth(text);
            float textScale = scale * 0.75f;
            
            context.getMatrices().push();
            context.getMatrices().scale(textScale, textScale, 1.0f);
            
            float scaledX = (screenX - textWidth * textScale / 2) / textScale;
            float scaledY = (screenY + markerHalfSize + 2) / textScale;
            
            // Draw text shadow
            context.drawText(textRenderer, text, (int)scaledX + 1, (int)scaledY + 1, 0x44000000, false);
            // Draw main text
            context.drawText(textRenderer, text, (int)scaledX, (int)scaledY, 0xFFFFFFFF, false);
            
            context.getMatrices().pop();
        }
    }
} 