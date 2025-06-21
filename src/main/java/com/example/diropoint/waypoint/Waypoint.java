package com.example.diropoint.waypoint;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

public class Waypoint {
    private String name;
    private Text displayName; // Supports formatting codes
    private Vec3d position;
    private DimensionType dimension;
    private int color;
    private WaypointIcon icon;
    private boolean visible;
    private boolean showBeacon;
    private boolean showOnHud;
    private boolean showOnMap;
    private boolean isTemporary;
    private boolean isDeathPoint;
    private long creationTime;
    private String createdBy; // For multiplayer support
    private String group; // For grouping/organization

    public Waypoint(String name, Vec3d position, DimensionType dimension) {
        this(name, position, dimension, 0xFF0000, WaypointIcon.getDefaultForDimension(dimension));
    }

    public Waypoint(String name, Vec3d position, DimensionType dimension, int color, WaypointIcon icon) {
        this.name = name;
        this.displayName = Text.literal(name);
        this.position = position;
        this.dimension = dimension;
        this.color = color;
        this.icon = icon;
        this.visible = true;
        this.showBeacon = true;
        this.showOnHud = true;
        this.showOnMap = true;
        this.isTemporary = false;
        this.isDeathPoint = false;
        this.creationTime = System.currentTimeMillis();
    }

    // Static factory methods
    public static Waypoint createDeathWaypoint(Vec3d position, DimensionType dimension) {
        Waypoint waypoint = new Waypoint("Death Point", position, dimension, 0xFF0000, WaypointIcon.SKULL);
        waypoint.isDeathPoint = true;
        return waypoint;
    }

    public static Waypoint createTemporaryWaypoint(String name, Vec3d position, DimensionType dimension) {
        Waypoint waypoint = new Waypoint(name, position, dimension);
        waypoint.isTemporary = true;
        return waypoint;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.displayName = Text.literal(name);
    }

    public Text getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Text displayName) {
        this.displayName = displayName;
    }

    public Vec3d getPosition() {
        return position;
    }

    public void setPosition(Vec3d position) {
        this.position = position;
    }

    public int getX() {
        return (int) position.x;
    }

    public int getY() {
        return (int) position.y;
    }

    public int getZ() {
        return (int) position.z;
    }

    public DimensionType getDimension() {
        return dimension;
    }

    public void setDimension(DimensionType dimension) {
        this.dimension = dimension;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public WaypointIcon getIcon() {
        return icon;
    }

    public void setIcon(WaypointIcon icon) {
        this.icon = icon;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isShowBeacon() {
        return showBeacon;
    }

    public void setShowBeacon(boolean showBeacon) {
        this.showBeacon = showBeacon;
    }

    public boolean isShowOnHud() {
        return showOnHud;
    }

    public void setShowOnHud(boolean showOnHud) {
        this.showOnHud = showOnHud;
    }

    public boolean isShowOnMap() {
        return showOnMap;
    }

    public void setShowOnMap(boolean showOnMap) {
        this.showOnMap = showOnMap;
    }

    public boolean isTemporary() {
        return isTemporary;
    }

    public void setTemporary(boolean temporary) {
        isTemporary = temporary;
    }

    public boolean isDeathPoint() {
        return isDeathPoint;
    }

    public void setDeathPoint(boolean deathPoint) {
        isDeathPoint = deathPoint;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public enum WaypointIcon {
        GRASS_BLOCK("textures/block/grass_block_top"),
        NETHERRACK("textures/block/netherrack"),
        END_STONE("textures/block/end_stone"),
        SKULL("textures/item/skeleton_skull"),
        CHEST("textures/block/chest_front"),
        SWORD("textures/item/diamond_sword"),
        STAR("textures/item/nether_star"),
        FLAG("textures/item/banner_pattern");

        private final Identifier texture;

        WaypointIcon(String texturePath) {
            this.texture = new Identifier("minecraft", texturePath);
        }

        public Identifier getTexture() {
            return texture;
        }

        public static WaypointIcon getDefaultForDimension(DimensionType dimension) {
           
            return GRASS_BLOCK;
        }
    }
} 