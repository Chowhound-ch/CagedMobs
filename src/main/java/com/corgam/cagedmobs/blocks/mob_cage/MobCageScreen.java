package com.corgam.cagedmobs.blocks.mob_cage;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.helpers.EntityRendererHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class MobCageScreen extends AbstractContainerScreen<MobCageContainer> {

    // The path to the GUI image
    private final ResourceLocation GUI = new ResourceLocation(CagedMobs.MOD_ID, "textures/gui/mob_cage.png");
    private final ResourceLocation UPGRADE_SLOT_OUTLINE = new ResourceLocation(CagedMobs.MOD_ID, "textures/gui/upgrade_slot.png");
    private final ResourceLocation ENVIRONMENT_SLOT_OUTLINE = new ResourceLocation(CagedMobs.MOD_ID, "textures/gui/environment_slot.png");

    private static float rotation = 0.0f;
    private static double yaw = 0;

    /**
     * Creates the cage screen rendered on the client side.
     * @param pMenu container
     * @param pPlayerInventory player inventory
     * @param pTitle screen name
     */
    public MobCageScreen(MobCageContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 183;
        this.imageWidth = 176;
        this.inventoryLabelY = this.imageHeight - 93;
    }

    /**
     * Makes the background darker and renders the item tooltips.
     * @param pGuiGraphics graphics stack
     * @param pMouseX x coord of the mouse
     * @param pMouseY y coord of the mouse
     * @param pPartialTick partial tick
     */
    public void render(PoseStack pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        // Render entity
        BlockEntity blockEntity = this.menu.player.level.getBlockEntity(this.menu.pos);
        if(blockEntity instanceof MobCageBlockEntity cageBE && cageBE.getEntity().isPresent()){
            Optional<Entity> entity = EntityRendererHelper.createEntity(this.menu.player.level, cageBE.getEntity().get().getEntityType());
            if(entity.isPresent()){
                rotation = (rotation+ 0.5f)% 360;
                enableScissor(this.leftPos+62, this.topPos+17, this.leftPos+114, this.topPos+87);
                EntityRendererHelper.renderEntity(pGuiGraphics, this.leftPos + 87, this.topPos + 125, yaw, 70, rotation, entity.get() );
                disableScissor();
                // Update yaw
                yaw = (yaw + 1.5) % 720.0F;
            }
        }
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    /**
     * Renders the main GUI of the cage.
     * @param pGuiGraphics graphics stack
     * @param pPartialTick partial tick
     * @param pMouseX x coord of the mouse
     * @param pMouseY y coord of the mouse
     */
    @Override
    protected void renderBg(PoseStack pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.GUI);
        blit(pGuiGraphics, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
        // Render item outline for environment slot
        Slot envSlot = this.menu.getEnvironmentSlot();
        if(!envSlot.hasItem()){
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, this.ENVIRONMENT_SLOT_OUTLINE);
            blit(pGuiGraphics, leftPos+envSlot.x, topPos+envSlot.y, 0, 0, 16, 16, 16, 16);
        }
        // Render item outlines for upgrade slots
        int i = 1;
        for(Slot slot : this.menu.getUpgradeSlots()){
            if (!slot.hasItem()) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, this.UPGRADE_SLOT_OUTLINE);
                blit(pGuiGraphics, leftPos+slot.x, topPos+slot.y, this.imageWidth + (16* (i-1)), 0, 16, 16, 16, 16);
                i++;
            }
        }
    }
}
