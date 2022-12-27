package goliath.raidableclaims.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import goliath.raidableclaims.RaidableClaims;
import goliath.raidableclaims.client.gui.menu.ClaimTowerMenu;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ClaimTowerScreen extends AbstractContainerScreen<ClaimTowerMenu> {
    public static final ResourceLocation GUI_TEXTURE =  new ResourceLocation(RaidableClaims.MODID, "textures/gui/claim-tower-screen.png");
    protected final ClaimTowerMenu menu;

    public ClaimTowerScreen(ClaimTowerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.menu = menu;

    }

    @Override
    protected void renderBg(PoseStack poseStack, float p_97788_, int p_97789_, int p_97790_) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void init() {
        super.init();
        Button newButton = new Button((width-imageWidth)/2 + 40, (height-imageHeight)/2 + 40, 100, 20, new TextComponent("Show Claim Area"), onPress -> {
            RaidableClaims.LOGGER.info("Button Pressed");
        });
        this.addRenderableWidget(newButton);
    }
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, delta);
        renderTooltip(poseStack, mouseX, mouseY);

    }

    @Override
    public boolean isPauseScreen() { return false; }
}
