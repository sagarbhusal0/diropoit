package com.example.diropoint;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class WaypointScreen extends Screen {
    public WaypointScreen() {
        super(Text.literal("Diro Point Waypoints"));
    }

    @Override
    protected void init() {
        super.init();
        // Add GUI elements here
        ButtonWidget addButton = ButtonWidget.builder(Text.literal("Add Waypoint"), button -> {
            // Handle add waypoint action
        })
        .dimensions(10, 10, 100, 20)
        .build();
        
        addDrawableChild(addButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        
        // Draw the title centered at the top
        context.drawCenteredTextWithShadow(
            textRenderer,
            title,
            width / 2,
            20,
            0xFFFFFF
        );
    }

    @Override
    public boolean shouldPause() {
        return true;
    }
}
