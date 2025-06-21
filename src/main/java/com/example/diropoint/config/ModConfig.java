package com.example.diropoint.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "diropoint")
public class ModConfig implements ConfigData {
    // HUD Settings
    @ConfigEntry.Gui.Tooltip
    public boolean showWaypointDistance = true;

    @ConfigEntry.Gui.Tooltip
    public boolean showWaypointCoordinates = true;

    @ConfigEntry.Gui.Tooltip
    public float hudScale = 1.0f;

    @ConfigEntry.Gui.Tooltip
    public float hudMinOpacity = 0.3f;

    @ConfigEntry.Gui.Tooltip
    public float hudMaxOpacity = 1.0f;

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public HudPosition hudPosition = HudPosition.TOP_CENTER;

    // Beacon Settings
    @ConfigEntry.Gui.Tooltip
    public boolean showInWorld = true;

    @ConfigEntry.Gui.Tooltip
    public boolean renderThroughBlocks = false;

    @ConfigEntry.Gui.Tooltip
    public int renderDistance = 512;

    @ConfigEntry.Gui.Tooltip
    public float beamHeight = 256.0f;

    @ConfigEntry.Gui.Tooltip
    public float beamWidth = 0.5f;

    @ConfigEntry.Gui.Tooltip
    public float minBeamAlpha = 0.1f;

    @ConfigEntry.Gui.Tooltip
    public float maxBeamAlpha = 0.8f;

    // Waypoint Settings
    @ConfigEntry.Gui.Tooltip
    public int maxWaypoints = 100;

    @ConfigEntry.Gui.Tooltip
    public boolean enableSounds = true;

    @ConfigEntry.ColorPicker
    public int defaultWaypointColor = 0xFF0000; // Default red color

    @ConfigEntry.Gui.Tooltip
    public boolean showOnMap = true;

    @ConfigEntry.Gui.Tooltip
    public boolean createDeathWaypoints = true;

    @ConfigEntry.Gui.Tooltip
    public boolean clearTemporaryOnDimensionChange = true;

    @ConfigEntry.Gui.Tooltip
    public boolean hideInSpectator = true;

    @ConfigEntry.Gui.Tooltip
    public boolean autoDeleteDeathWaypoints = false;

    @ConfigEntry.Gui.Tooltip
    public int deathWaypointDuration = 300; // 5 minutes in seconds

    // Multiplayer Settings
    @ConfigEntry.Gui.Tooltip
    public boolean enableMultiplayerSync = false;

    @ConfigEntry.Gui.Tooltip
    public boolean shareWithTeamOnly = true;

    public enum HudPosition {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT
    }

    @Override
    public void validatePostLoad() {
        // Ensure values are within valid ranges
        hudScale = clamp(hudScale, 0.5f, 2.0f);
        hudMinOpacity = clamp(hudMinOpacity, 0.0f, 1.0f);
        hudMaxOpacity = clamp(hudMaxOpacity, hudMinOpacity, 1.0f);
        renderDistance = clamp(renderDistance, 16, 1024);
        beamHeight = clamp(beamHeight, 16.0f, 512.0f);
        beamWidth = clamp(beamWidth, 0.1f, 2.0f);
        minBeamAlpha = clamp(minBeamAlpha, 0.0f, 1.0f);
        maxBeamAlpha = clamp(maxBeamAlpha, minBeamAlpha, 1.0f);
        maxWaypoints = clamp(maxWaypoints, 1, 1000);
        deathWaypointDuration = clamp(deathWaypointDuration, 0, 3600);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
} 