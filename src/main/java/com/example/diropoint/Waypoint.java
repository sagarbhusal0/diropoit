package com.example.diropoint;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

public class Waypoint {
    private String name;
    private int color;
    private Vec3d position;
    private DimensionType dimension;
    private boolean isDeathPoint;

    public Waypoint(String name, int color, Vec3d position, DimensionType dimension, boolean isDeathPoint) {
        this.name = name;
        this.color = color;
        this.position = position;
        this.dimension = dimension;
        this.isDeathPoint = isDeathPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Vec3d getPosition() {
        return position;
    }

    public void setPosition(Vec3d position) {
        this.position = position;
    }

    public DimensionType getDimension() {
        return dimension;
    }

    public void setDimension(DimensionType dimension) {
        this.dimension = dimension;
    }

    public boolean isDeathPoint() {
        return isDeathPoint;
    }

    public void setDeathPoint(boolean deathPoint) {
        isDeathPoint = deathPoint;
    }
}
