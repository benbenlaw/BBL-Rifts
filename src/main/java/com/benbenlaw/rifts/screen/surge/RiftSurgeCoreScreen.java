package com.benbenlaw.rifts.screen.surge;

import com.benbenlaw.rifts.Rifts;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class RiftSurgeCoreScreen extends AbstractContainerScreen<RiftSurgeCoreMenu> {
    private static final Identifier TEXTURE = Rifts.identifier("textures/gui/rift_surge_core_gui.png");
    private static final Identifier ENERGY_BAR = Rifts.identifier("energy_bar");

    public RiftSurgeCoreScreen(RiftSurgeCoreMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackground(guiGraphics, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        for (int stage = 0; stage < RiftSurgeCoreMenu.STAGES; stage++) {
            int filled = menu.getStageFilled(stage);
            if (filled <= 0) continue;

            int topOffset = RiftSurgeCoreMenu.BAR_HEIGHT - filled;
            int barX = x + RiftSurgeCoreMenu.BAR_X + stage * RiftSurgeCoreMenu.BAR_SPACING;
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENERGY_BAR, 16, RiftSurgeCoreMenu.BAR_HEIGHT, 0, topOffset,
                    barX, y + RiftSurgeCoreMenu.BAR_Y + topOffset, 16, filled);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        int left = x + RiftSurgeCoreMenu.BAR_X;
        int right = left + (RiftSurgeCoreMenu.STAGES - 1) * RiftSurgeCoreMenu.BAR_SPACING + 16;
        int top = y + RiftSurgeCoreMenu.BAR_Y;

        if (mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= top + RiftSurgeCoreMenu.BAR_HEIGHT) {
            Component text = Component.translatable("gui.rifts.energy", menu.getEnergy(), menu.getCapacity());
            List<ClientTooltipComponent> components = List.of(ClientTooltipComponent.create(text.getVisualOrderText()));
            graphics.tooltip(this.font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
        }
    }
}
