package com.benbenlaw.rifts.screen.crusher;

import com.benbenlaw.core.Core;
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

public class RiftCrusherScreen extends AbstractContainerScreen<RiftCrusherMenu> {
    private static final Identifier TEXTURE = Rifts.identifier("textures/gui/rift_crusher_gui.png");
    private static final Identifier ENERGY_BAR = Rifts.identifier("energy_bar");
    private static final Identifier PROGRESS_ARROW = Core.identifier("progress_arrow");

    public RiftCrusherScreen(RiftCrusherMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackground(guiGraphics, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        if (menu.isCrushing()) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_ARROW, 24, 16, 0, 0,
                    x + RiftCrusherMenu.ARROW_X, y + RiftCrusherMenu.INPUT_Y, menu.getScaledProgress() + 1, 16);
        }

        if (menu.hasEnergy()) {
            int currentEnergyHeight = menu.getEnergyFilled();
            int topOffset = 52 - currentEnergyHeight;

            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENERGY_BAR, 16, 52, 0, topOffset, x + 8, y + topOffset + 16, 16, currentEnergyHeight);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        int barX = x + 8;
        int barY = y + 16;

        if (mouseX >= barX && mouseX <= barX + 16 && mouseY >= barY && mouseY <= barY + 52) {
            Component text = Component.literal("Rift Energy: " + menu.getEnergy() + " / " + menu.getCapacity());
            List<ClientTooltipComponent> components = List.of(ClientTooltipComponent.create(text.getVisualOrderText()));
            graphics.tooltip(this.font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
        }
    }
}
