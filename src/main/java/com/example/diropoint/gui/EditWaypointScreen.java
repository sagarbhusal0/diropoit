package com.example.diropoint.gui;

import com.example.diropoint.waypoint.Waypoint;
import com.example.diropoint.WaypointManager;
import com.example.diropoint.waypoint.Waypoint.WaypointIcon;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import java.util.function.Consumer;
import java.util.function.Function;

public class EditWaypointScreen extends Screen {
    private static final int FIELD_HEIGHT = 20;
    private static final int PADDING = 8;
    private static final int LABEL_COLOR = 0xE0E0E0;
    private static final int ERROR_COLOR = 0xFF5555;
    private static final int ERROR_DISPLAY_TIME = 60; // 3 seconds at 20 ticks per second

    private final Screen parent;
    private final Waypoint waypoint;
    private final boolean isNewWaypoint;
    private String errorMessage = null;
    private int errorDisplayTicks = 0;

    private TextFieldWidget nameField;
    private TextFieldWidget xField;
    private TextFieldWidget yField;
    private TextFieldWidget zField;
    private ColorPickerWidget colorPicker;
    private CyclingButtonWidget<WaypointIcon> iconButton;
    private TextFieldWidget groupField;

    public EditWaypointScreen(Screen parent, Waypoint waypoint) {
        super(Text.literal(waypoint == null ? "Add Waypoint" : "Edit Waypoint"));
        this.parent = parent;
        this.isNewWaypoint = waypoint == null;
        if (isNewWaypoint) {
            Vec3d pos = client.player.getPos();
            this.waypoint = new Waypoint(
                "New Waypoint",
                pos,
                client.world.getDimension()
            );
        } else {
            this.waypoint = waypoint;
        }
    }

    @Override
    protected void init() {
        super.init();
        int centerX = width / 2;
        int y = 40;

        // Name field
        nameField = addTextField(centerX - 100, y + 12, 200, waypoint.getName());
        y += 40;

        // Coordinates
        xField = addTextField(centerX - 100, y + 12, 60, String.valueOf(waypoint.getX()));
        yField = addTextField(centerX - 30, y + 12, 60, String.valueOf(waypoint.getY()));
        zField = addTextField(centerX + 40, y + 12, 60, String.valueOf(waypoint.getZ()));
        y += 40;

        // Color picker
        colorPicker = new ColorPickerWidget(centerX - 100, y + 12, 200, FIELD_HEIGHT, waypoint.getColor());
        addDrawableChild(colorPicker);
        y += 40;

        // Icon selector
        Function<WaypointIcon, Text> nameProvider = icon -> Text.literal(icon.name());
        iconButton = CyclingButtonWidget.<WaypointIcon>builder(nameProvider)
            .values(WaypointIcon.values())
            .initially(waypoint.getIcon())
            .build(centerX - 100, y + 12, 200, FIELD_HEIGHT, Text.literal("Icon"));
        addDrawableChild(iconButton);
        y += 40;

        // Visibility toggles
        int toggleWidth = 95;
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Visible: " + (waypoint.isVisible() ? "ON" : "OFF")),
            button -> {
                waypoint.setVisible(!waypoint.isVisible());
                button.setMessage(Text.literal("Visible: " + (waypoint.isVisible() ? "ON" : "OFF")));
            })
            .dimensions(centerX - 100, y, toggleWidth, FIELD_HEIGHT)
            .build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Beacon: " + (waypoint.isShowBeacon() ? "ON" : "OFF")),
            button -> {
                waypoint.setShowBeacon(!waypoint.isShowBeacon());
                button.setMessage(Text.literal("Beacon: " + (waypoint.isShowBeacon() ? "ON" : "OFF")));
            })
            .dimensions(centerX + 5, y, toggleWidth, FIELD_HEIGHT)
            .build());
        y += 24;

        addDrawableChild(ButtonWidget.builder(
            Text.literal("HUD: " + (waypoint.isShowOnHud() ? "ON" : "OFF")),
            button -> {
                waypoint.setShowOnHud(!waypoint.isShowOnHud());
                button.setMessage(Text.literal("HUD: " + (waypoint.isShowOnHud() ? "ON" : "OFF")));
            })
            .dimensions(centerX - 100, y, toggleWidth, FIELD_HEIGHT)
            .build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Map: " + (waypoint.isShowOnMap() ? "ON" : "OFF")),
            button -> {
                waypoint.setShowOnMap(!waypoint.isShowOnMap());
                button.setMessage(Text.literal("Map: " + (waypoint.isShowOnMap() ? "ON" : "OFF")));
            })
            .dimensions(centerX + 5, y, toggleWidth, FIELD_HEIGHT)
            .build());
        y += 24;

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Temporary: " + (waypoint.isTemporary() ? "ON" : "OFF")),
            button -> {
                waypoint.setTemporary(!waypoint.isTemporary());
                button.setMessage(Text.literal("Temporary: " + (waypoint.isTemporary() ? "ON" : "OFF")));
            })
            .dimensions(centerX - 100, y, toggleWidth, FIELD_HEIGHT)
            .build());
        y += 40;

        // Group field
        groupField = addTextField(centerX - 100, y + 12, 200, waypoint.getGroup());
        y += 40;

        // Save and Cancel buttons
        ButtonWidget saveButton = ButtonWidget.builder(
            Text.literal("Save"),
            button -> saveWaypoint()
        )
        .dimensions(centerX - 100, height - 40, 95, FIELD_HEIGHT)
        .build();
        addDrawableChild(saveButton);

        ButtonWidget cancelButton = ButtonWidget.builder(
            Text.literal("Cancel"),
            button -> client.setScreen(parent)
        )
        .dimensions(centerX + 5, height - 40, 95, FIELD_HEIGHT)
        .build();
        addDrawableChild(cancelButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        
        // Draw title
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xFFFFFF);
        
        int centerX = width / 2;
        int y = 40;

        // Draw labels
        context.drawTextWithShadow(textRenderer, Text.literal("Name:"), centerX - 100, y, LABEL_COLOR);
        y += 40;
        
        context.drawTextWithShadow(textRenderer, Text.literal("Coordinates:"), centerX - 100, y, LABEL_COLOR);
        y += 40;
        
        context.drawTextWithShadow(textRenderer, Text.literal("Color:"), centerX - 100, y, LABEL_COLOR);
        y += 40;
        
        context.drawTextWithShadow(textRenderer, Text.literal("Icon:"), centerX - 100, y, LABEL_COLOR);
        y += 88;
        
        context.drawTextWithShadow(textRenderer, Text.literal("Group:"), centerX - 100, y, LABEL_COLOR);

        // Draw error message if present
        if (errorMessage != null && errorDisplayTicks > 0) {
            context.drawCenteredTextWithShadow(textRenderer, Text.literal(errorMessage), width / 2, height - 60, ERROR_COLOR);
            errorDisplayTicks--;
            if (errorDisplayTicks <= 0) {
                errorMessage = null;
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        super.tick();
        nameField.tick();
        xField.tick();
        yField.tick();
        zField.tick();
        groupField.tick();
    }

    private TextFieldWidget addTextField(int x, int y, int width, String text) {
        TextFieldWidget field = new TextFieldWidget(textRenderer, x, y, width, FIELD_HEIGHT, Text.literal(""));
        field.setText(text);
        addDrawableChild(field);
        return field;
    }

    private void showError(String message) {
        errorMessage = message;
        errorDisplayTicks = ERROR_DISPLAY_TIME;
        if (client != null && client.player != null) {
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), SoundCategory.MASTER, 1.0f, 0.5f);
        }
    }

    private void saveWaypoint() {
        // Validate name
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Name cannot be empty");
            return;
        }

        // Update waypoint data
        waypoint.setName(name);
        try {
            double x = Double.parseDouble(xField.getText());
            double y = Double.parseDouble(yField.getText());
            double z = Double.parseDouble(zField.getText());
            waypoint.setPosition(new Vec3d(x, y, z));
        } catch (NumberFormatException e) {
            showError("Invalid coordinates");
            return;
        }

        waypoint.setColor(colorPicker.getSelectedColor());
        waypoint.setIcon(iconButton.getValue());
        waypoint.setGroup(groupField.getText().trim());

        // Save to manager
        if (isNewWaypoint) {
            WaypointManager.getInstance().addWaypoint(waypoint);
        }

        // Return to parent screen
        client.setScreen(parent);
    }

    private class ColorPickerWidget extends ButtonWidget {
        private static final int[] PRESET_COLORS = {
            0xFF0000, // Red
            0x00FF00, // Green
            0x0000FF, // Blue
            0xFFFF00, // Yellow
            0xFF00FF, // Magenta
            0x00FFFF, // Cyan
            0xFFFFFF, // White
            0x808080  // Gray
        };

        private int selectedColor;
        private boolean showingPicker = false;

        public ColorPickerWidget(int x, int y, int width, int height, int initialColor) {
            super(x, y, width, height, Text.literal(""), button -> {}, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
            this.selectedColor = initialColor;
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            context.fill(getX(), getY(), getX() + width, getY() + height, 0xFF000000 | selectedColor);
            if (isHovered()) {
                context.fill(getX(), getY(), getX() + width, getY() + height, 0x80FFFFFF);
            }

            if (showingPicker) {
                renderColorPicker(context, mouseX, mouseY);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isHovered()) {
                showingPicker = !showingPicker;
                playDownSound(client.getSoundManager());
                return true;
            }

            if (showingPicker) {
                int pickerX = getX();
                int pickerY = getY() + height + 4;

                for (int i = 0; i < PRESET_COLORS.length; i++) {
                    int colorX = pickerX + (i % 4) * 30;
                    int colorY = pickerY + (i / 4) * 30;

                    if (mouseX >= colorX && mouseX < colorX + 25 &&
                        mouseY >= colorY && mouseY < colorY + 25) {
                        selectedColor = PRESET_COLORS[i];
                        showingPicker = false;
                        playDownSound(client.getSoundManager());
                        return true;
                    }
                }
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        private void renderColorPicker(DrawContext context, int mouseX, int mouseY) {
            int pickerX = getX();
            int pickerY = getY() + height + 4;

            // Draw color grid
            for (int i = 0; i < PRESET_COLORS.length; i++) {
                int colorX = pickerX + (i % 4) * 30;
                int colorY = pickerY + (i / 4) * 30;

                // Draw color square
                context.fill(colorX, colorY, colorX + 25, colorY + 25, 0xFF000000 | PRESET_COLORS[i]);

                // Draw hover effect
                if (mouseX >= colorX && mouseX < colorX + 25 &&
                    mouseY >= colorY && mouseY < colorY + 25) {
                    context.fill(colorX, colorY, colorX + 25, colorY + 25, 0x80FFFFFF);
                }
            }
        }

        public int getSelectedColor() {
            return selectedColor;
        }
    }
} 