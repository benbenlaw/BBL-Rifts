package com.benbenlaw.rifts.screen.infuser;

import com.benbenlaw.core.Core;
import com.benbenlaw.core.screen.util.DurationTooltip;
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


public class RIftInfuserScreen extends AbstractContainerScreen<RiftInfuserMenu> {
    private static final Identifier TEXTURE = Rifts.identifier("textures/gui/rift_infuser_gui.png");
    private static final Identifier ENERGY_BAR = Rifts.identifier("energy_bar");
    private static final Identifier PROGRESS_ARROW = Core.identifier("progress_arrow");

    public RIftInfuserScreen(RiftInfuserMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackground(guiGraphics, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        if (menu.isCrafting()) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_ARROW, 24, 16, 0, 0, x + 104, y + 36, menu.getScaledProgress() + 1, 16);
        }

        if (menu.hasEnergy()) {
            int currentEnergyHeight = menu.getEnergyFilled();
            int topOffset = 52 - currentEnergyHeight;

            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENERGY_BAR, 16, 52,0, topOffset, x + 8, y + topOffset + 16, 16, currentEnergyHeight);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        DurationTooltip.renderDurationTooltip(guiGraphics, mouseX, mouseY, x, y, 161, 5, menu.data.get(0), menu.data.get(1));
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        int barX = x + 8;
        int barY = y + 16;
        int barWidth = 16;
        int barHeight = 52;

        if (mouseX >= barX && mouseX <= barX + barWidth && mouseY >= barY && mouseY <= barY + barHeight) {
            int currentEnergy = menu.data.get(2);
            int maxEnergy = menu.data.get(3);

            Component text = Component.literal("Rift Energy: " + currentEnergy + " / " + maxEnergy);
            List<ClientTooltipComponent> components = List.of(ClientTooltipComponent.create(text.getVisualOrderText()));
            graphics.tooltip(this.font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);

        }

    }
}
