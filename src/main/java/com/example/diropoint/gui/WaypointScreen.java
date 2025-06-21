package com.example.diropoint.gui;

import com.example.diropoint.waypoint.Waypoint;
import com.example.diropoint.WaypointManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.world.dimension.DimensionType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WaypointScreen extends Screen {
    private static final int BUTTON_HEIGHT = 20;
    private static final int PADDING = 8;
    private static final int HEADER_HEIGHT = 32;
    private static final int FOOTER_HEIGHT = 48;
    private static final int ENTRY_HEIGHT = 24;

    private TextFieldWidget searchField;
    private List<WaypointEntry> waypointEntries = new ArrayList<>();
    private DimensionType selectedDimension;
    private String searchQuery = "";
    private SortMode sortMode = SortMode.NAME;
    private int scrollOffset = 0;
    private boolean isDragging = false;
    private double lastMouseY;

    public WaypointScreen() {
        super(Text.literal("Waypoints"));
        this.selectedDimension = client.world.getDimension();
    }

    @Override
    protected void init() {
        super.init();

        // Add search field
        searchField = new TextFieldWidget(
            textRenderer,
            PADDING,
            HEADER_HEIGHT - BUTTON_HEIGHT - 4,
            150,
            BUTTON_HEIGHT,
            Text.literal("Search waypoints...")
        );
        searchField.setMaxLength(32);
        searchField.setChangedListener(this::onSearchChanged);
        addDrawableChild(searchField);

        // Add dimension filter buttons
        int buttonWidth = 80;
        int x = width - buttonWidth - PADDING;
        
        // Sort button
        ButtonWidget sortButton = ButtonWidget.builder(
            Text.literal("Sort: " + sortMode.name()),
            button -> cycleSortMode()
        )
        .dimensions(x, HEADER_HEIGHT - BUTTON_HEIGHT - 4, buttonWidth, BUTTON_HEIGHT)
        .build();
        addDrawableChild(sortButton);

        // Add waypoint button
        ButtonWidget addButton = ButtonWidget.builder(
            Text.literal("+ Add Waypoint"),
            button -> openAddWaypointScreen()
        )
        .dimensions(width - 120 - PADDING, height - BUTTON_HEIGHT - PADDING, 120, BUTTON_HEIGHT)
        .build();
        addDrawableChild(addButton);

        // Import/Export buttons
        ButtonWidget importButton = ButtonWidget.builder(
            Text.literal("Import"),
            button -> importWaypoints()
        )
        .dimensions(PADDING, height - BUTTON_HEIGHT - PADDING, 80, BUTTON_HEIGHT)
        .build();
        addDrawableChild(importButton);

        ButtonWidget exportButton = ButtonWidget.builder(
            Text.literal("Export"),
            button -> exportWaypoints()
        )
        .dimensions(PADDING + 90, height - BUTTON_HEIGHT - PADDING, 80, BUTTON_HEIGHT)
        .build();
        addDrawableChild(exportButton);

        refreshWaypointList();
    }

    private void refreshWaypointList() {
        waypointEntries.clear();
        List<Waypoint> waypoints = WaypointManager.getInstance().getWaypoints(selectedDimension);
        
        // Filter by search query
        if (!searchQuery.isEmpty()) {
            waypoints = waypoints.stream()
                .filter(w -> w.getName().toLowerCase().contains(searchQuery.toLowerCase()))
                .toList();
        }

        // Sort waypoints
        waypoints.sort(getSorter());

        // Create entries
        for (Waypoint waypoint : waypoints) {
            waypointEntries.add(new WaypointEntry(waypoint));
        }
    }

    private Comparator<Waypoint> getSorter() {
        return switch (sortMode) {
            case NAME -> Comparator.comparing(Waypoint::getName);
            case DISTANCE -> Comparator.comparingDouble(w -> 
                client.player.getPos().distanceTo(w.getPosition()));
            case CREATION -> Comparator.comparingLong(Waypoint::getCreationTime);
        };
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        // Draw title
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, PADDING, 0xFFFFFF);

        // Draw waypoint list
        int y = HEADER_HEIGHT;
        int visibleHeight = height - HEADER_HEIGHT - FOOTER_HEIGHT;
        int maxScroll = Math.max(0, waypointEntries.size() * ENTRY_HEIGHT - visibleHeight);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        // Draw scrollable list
        context.enableScissor(0, HEADER_HEIGHT, width, height - FOOTER_HEIGHT);
        int entryY = y - scrollOffset;
        for (WaypointEntry entry : waypointEntries) {
            if (entryY + ENTRY_HEIGHT >= HEADER_HEIGHT && entryY <= height - FOOTER_HEIGHT) {
                entry.render(context, PADDING, entryY, width - PADDING * 2, ENTRY_HEIGHT, mouseX, mouseY);
            }
            entryY += ENTRY_HEIGHT;
        }
        context.disableScissor();

        // Draw header and footer backgrounds
        context.fill(0, 0, width, HEADER_HEIGHT, 0x88000000);
        context.fill(0, height - FOOTER_HEIGHT, width, height, 0x88000000);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scrollOffset -= (int)(amount * 16);
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging) {
            scrollOffset += (int)(lastMouseY - mouseY);
            lastMouseY = mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseY >= HEADER_HEIGHT && mouseY <= height - FOOTER_HEIGHT) {
            isDragging = true;
            lastMouseY = mouseY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void onSearchChanged(String query) {
        this.searchQuery = query;
        refreshWaypointList();
    }

    private void cycleSortMode() {
        sortMode = SortMode.values()[(sortMode.ordinal() + 1) % SortMode.values().length];
        refreshWaypointList();
    }

    private void openAddWaypointScreen() {
        client.setScreen(new EditWaypointScreen(this, null));
    }

    private void importWaypoints() {
        // TODO: Implement waypoint import
    }

    private void exportWaypoints() {
        // TODO: Implement waypoint export
    }

    private enum SortMode {
        NAME,
        DISTANCE,
        CREATION
    }

    private class WaypointEntry {
        private final Waypoint waypoint;
        private ButtonWidget editButton;
        private ButtonWidget deleteButton;
        private ButtonWidget teleportButton;

        public WaypointEntry(Waypoint waypoint) {
            this.waypoint = waypoint;
        }

        public void render(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY) {
            // Background
            context.fill(x, y, x + width, y + height - 1, 0x44000000);

            // Icon
            context.drawTexture(waypoint.getIcon().getTexture(), x + 4, y + 4, 0, 0, 16, 16, 16, 16);

            // Name and coordinates
            String coords = String.format("(%d, %d, %d)", waypoint.getX(), waypoint.getY(), waypoint.getZ());
            context.drawTextWithShadow(textRenderer, waypoint.getDisplayName(), x + 24, y + 4, waypoint.getColor());
            context.drawTextWithShadow(textRenderer, coords, x + 24, y + 14, 0x808080);

            // Buttons
            if (editButton == null) {
                int buttonWidth = 40;
                int buttonX = x + width - buttonWidth * 3 - 8;
                editButton = ButtonWidget.builder(Text.literal("Edit"), b -> editWaypoint())
                    .dimensions(buttonX, y + 2, buttonWidth, 16).build();
                deleteButton = ButtonWidget.builder(Text.literal("Delete"), b -> deleteWaypoint())
                    .dimensions(buttonX + buttonWidth + 4, y + 2, buttonWidth, 16).build();
                teleportButton = ButtonWidget.builder(Text.literal("TP"), b -> teleportToWaypoint())
                    .dimensions(buttonX + buttonWidth * 2 + 8, y + 2, buttonWidth, 16).build();
            }

            editButton.setY(y + 2);
            deleteButton.setY(y + 2);
            teleportButton.setY(y + 2);

            editButton.render(context, mouseX, mouseY, 0);
            deleteButton.render(context, mouseX, mouseY, 0);
            if (client.player.hasPermissionLevel(2)) {
                teleportButton.render(context, mouseX, mouseY, 0);
            }
        }

        private void editWaypoint() {
            client.setScreen(new EditWaypointScreen(WaypointScreen.this, waypoint));
        }

        private void deleteWaypoint() {
            // TODO: Show confirmation dialog
            WaypointManager.getInstance().removeWaypoint(waypoint);
            refreshWaypointList();
        }

        private void teleportToWaypoint() {
            if (client.player.hasPermissionLevel(2)) {
                WaypointManager.teleportToWaypoint(client.player, waypoint);
            }
        }
    }
} 