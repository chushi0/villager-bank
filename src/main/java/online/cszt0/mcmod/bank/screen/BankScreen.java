package online.cszt0.mcmod.bank.screen;

import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import online.cszt0.mcmod.bank.VillageBank;

@Slf4j(topic = VillageBank.MODID)
public class BankScreen extends HandledScreen<BankScreen.Handler> {
    private static final Identifier TEXTURE = VillageBank.identity("textures/gui/clark.png");

    public BankScreen(BankScreen.Handler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 276;
        this.playerInventoryTitleX = 107;

        handler.setupInventory(inventory);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        drawTexture(matrices, i, j, this.getZOffset(), 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 512,
                256);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    public static class Handler extends ScreenHandler {
        @Setter
        @Getter
        private UUID entityUuid;

        private PlayerInventory inventory;

        public Handler() {
            super(ScreenHandlerType.MERCHANT, 0);
        }

        public void setupInventory(PlayerInventory inventory) {
            this.inventory = inventory;
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(inventory, j + i * 9 + 9, 108 + j * 18, 84 + i * 18));
                }
            }

            for (int i = 0; i < 9; ++i) {
                this.addSlot(new Slot(inventory, i, 108 + i * 18, 142));
            }
        }

        @Override
        public boolean canUse(PlayerEntity player) {
            return inventory.canPlayerUse(player);
        }

        @Override
        public ItemStack quickMove(PlayerEntity player, int slot) {
            log.info("quickMove");
            return null;
        }
    }
}
