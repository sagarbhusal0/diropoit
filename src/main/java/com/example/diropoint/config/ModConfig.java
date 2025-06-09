package com.example.diropoint.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "diropoint")
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean showWaypointDistance = true;

    @ConfigEntry.Gui.Tooltip
    public boolean showWaypointCoordinates = true;

    @ConfigEntry.Gui.Tooltip
    public int maxWaypoints = 50;

    @ConfigEntry.Gui.Tooltip
    public boolean enableSounds = true;

    @ConfigEntry.Gui.Tooltip
    public float waypointScale = 1.0f;

    @ConfigEntry.ColorPicker
    public int waypointColor = 0xFF0000; // Default red color

    @ConfigEntry.Gui.Tooltip
    public boolean showInWorld = true;

    @ConfigEntry.Gui.Tooltip
    public boolean showOnMap = true;

    @ConfigEntry.Gui.Tooltip
    public int renderDistance = 64;
} 