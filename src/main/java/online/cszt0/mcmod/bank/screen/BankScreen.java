package online.cszt0.mcmod.bank.screen;

import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import online.cszt0.mcmod.bank.VillageBank;
import online.cszt0.mcmod.bank.entity.ClarkVillagerEntity;

@Slf4j(topic = VillageBank.MODID)
public class BankScreen extends HandledScreen<BankScreen.Handler> {
    public static final ScreenHandlerType<Handler> ScreenType = new ScreenHandlerType<>(Handler::new);
    private static final Identifier TEXTURE = VillageBank.identity("textures/gui/clark.png");

    private static final Text DEPOSIT_TEXT = Text.translatable("ui.village_bank.bank_screen.deposit");
    private static final Text WITHDRAW_TEXT = Text.translatable("ui.village_bank.bank_screen.withdraw");

    public BankScreen(BankScreen.Handler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        log.info("initialize bankscreen");
        this.backgroundWidth = 276;
        this.playerInventoryTitleX = 107;
    }

    public static void initialize() {
        HandledScreens.register(ScreenType, BankScreen::new);
    }

    public static NamedScreenHandlerFactory createFactory(ClarkVillagerEntity entity) {
        return new Factory(entity);
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
        private BankInventory bankInventory;

        public Handler(int syncId, PlayerInventory inventory) {
            super(ScreenType, syncId);
            this.bankInventory = new BankInventory();
            setupInventory(inventory);
            setupDepositAndWithdraw();
        }

        private void setupInventory(PlayerInventory inventory) {
            this.inventory = inventory;

            for (int i = 0; i < 9; ++i) {
                this.addSlot(new Slot(inventory, i, 108 + i * 18, 142));
            }
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(inventory, j + i * 9 + 9, 108 + j * 18, 84 + i * 18));
                }
            }
        }

        private void setupDepositAndWithdraw() {
            this.addSlot(new DepositSlot(this.bankInventory, BankInventory.INPUT_SLOT, 136, 37));
            this.addSlot(new DepositSlot(this.bankInventory, BankInventory.INPUT_SLOT, 162, 37));
            this.addSlot(new WithdrawSlot(this.bankInventory, BankInventory.OUTPUT_SLOT, 220, 37));
        }

        @Override
        public boolean canUse(PlayerEntity player) {
            return true;
        }

        @Override
        public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
            if (slot.inventory == inventory) {
                return true;
            }
            return super.canInsertIntoSlot(stack, slot);
        }

        @Override
        public ItemStack quickMove(PlayerEntity player, int slot) {
            log.info("quickMove");
            return ItemStack.EMPTY;
        }
    }

    public static class BankInventory implements Inventory {
        static final int INPUT_SLOT = 0;
        static final int OUTPUT_SLOT = 1;

        private int moneyCount = 32;

        @Override
        public void clear() {
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return true;
        }

        @Override
        public ItemStack getStack(int slot) {
            if (slot == OUTPUT_SLOT && moneyCount > 0) {
                return new ItemStack(Items.EMERALD, Math.min(64, moneyCount));
            }
            return ItemStack.EMPTY;
        }

        @Override
        public boolean isEmpty() {
            return moneyCount == 0;
        }

        @Override
        public void markDirty() {
        }

        @Override
        public ItemStack removeStack(int slot) {
            if (slot == OUTPUT_SLOT && moneyCount > 0) {
                int removeCount = Math.min(64, moneyCount);
                moneyCount -= removeCount;
                return new ItemStack(Items.EMERALD, removeCount);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            if (slot == OUTPUT_SLOT && moneyCount > 0) {
                int removeCount = Math.min(amount, moneyCount);
                moneyCount -= removeCount;
                return new ItemStack(Items.EMERALD, removeCount);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            if (slot == OUTPUT_SLOT) {
                if (stack.getItem() != Items.EMERALD && !stack.isEmpty()) {
                    throw new IllegalArgumentException();
                }
                int amount = Math.min(64, moneyCount) - stack.getCount();
                if (amount < 0) {
                    throw new IllegalArgumentException();
                }
                this.moneyCount -= amount;
                return;
            }
            Item item = stack.getItem();
            if (item == Items.EMERALD) {
                moneyCount += stack.getCount();
            } else if (item == Items.EMERALD_BLOCK) {
                moneyCount += stack.getCount() * 9;
            } else if (!stack.isEmpty()) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public int size() {
            return 2;
        }

    }

    public static class DepositSlot extends Slot {
        public DepositSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        public boolean canInsert(ItemStack stack) {
            return stack.getItem() == Items.EMERALD || stack.getItem() == Items.EMERALD_BLOCK;
        }
    }

    public static class WithdrawSlot extends Slot {
        public WithdrawSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        public boolean canInsert(ItemStack stack) {
            return false;
        }
    }

    @RequiredArgsConstructor
    public static class Factory implements NamedScreenHandlerFactory {
        private final ClarkVillagerEntity entity;

        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            return new Handler(syncId, inv);
        }

        @Override
        public Text getDisplayName() {
            return entity.getName();
        }
    }
}
