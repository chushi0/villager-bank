package online.cszt0.mcmod.bank.screen;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.Merchant;
import net.minecraft.village.SimpleMerchant;
import online.cszt0.mcmod.bank.VillageBank;
import online.cszt0.mcmod.bank.criterion.SaveMoneyCriterion;
import online.cszt0.mcmod.bank.data.BankData;
import online.cszt0.mcmod.bank.data.PlayerBankData;
import online.cszt0.mcmod.bank.entity.ClarkVillagerEntity;
import online.cszt0.mcmod.bank.util.Value;

@Slf4j(topic = VillageBank.MODID)
public class BankScreen extends HandledScreen<BankScreen.Handler> {
    public static final ScreenHandlerType<Handler> ScreenType = new ExtendedScreenHandlerType<>(Handler::new);
    public static final Identifier ScreenHandler = VillageBank.identity("bank_screen_handler");
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

    public static void initializeHandler() {
        Registry.register(Registries.SCREEN_HANDLER, ScreenHandler, ScreenType);
    }

    public static NamedScreenHandlerFactory createFactory(ClarkVillagerEntity entity) {
        return new Factory(entity);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        super.drawForeground(matrices, mouseX, mouseY);
        textRenderer.draw(matrices, DEPOSIT_TEXT, 157 - textRenderer.getWidth(DEPOSIT_TEXT) / 2, 21, 4210752);
        textRenderer.draw(matrices, WITHDRAW_TEXT, 228 - textRenderer.getWidth(WITHDRAW_TEXT) / 2, 21, 4210752);
        Text remainText = Text.translatable("ui.village_bank.bank_screen.remain",
                handler.bankInventory.getReadableCount());
        textRenderer.draw(matrices, remainText, 268 - textRenderer.getWidth(remainText), playerInventoryTitleY - 10,
                4210752);
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

        private final Merchant merchant;
        private final PlayerInventory playerInventory;
        private final BankInventory bankInventory;

        public Handler(int syncId, PlayerInventory inventory, Merchant merchant) {
            super(ScreenType, syncId);
            this.merchant = merchant;
            this.playerInventory = inventory;
            this.bankInventory = new BankInventory(this);
            setupInventory();
            setupDepositAndWithdraw();
        }

        public Handler(int syncId, PlayerInventory inv, PlayerEntity player, Merchant merchant) {
            this(syncId, inv, merchant);
            PlayerBankData data = BankData.getBankData(player.getServer()).getPlayerData(player);
            bankInventory.bankData = data;
        }

        public Handler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
            this(syncId, inv, new SimpleMerchant(inv.player));
            bankInventory.bankData = PlayerBankData.createFromNbt(null, buf.readNbt());
        }

        private void playYesSound() {
            if (!this.merchant.isClient()) {
                Entity entity = (Entity) this.merchant;
                entity.getWorld().playSound(entity.getX(), entity.getY(), entity.getZ(), this.merchant.getYesSound(),
                        SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
            }
        }

        @Override
        public void close(PlayerEntity player) {
            super.close(player);
            merchant.setCustomer(null);
        }

        private void setupInventory() {
            for (int i = 0; i < 9; ++i) {
                this.addSlot(new Slot(playerInventory, i, 108 + i * 18, 142));
            }
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 108 + j * 18, 84 + i * 18));
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
            if (slot.inventory == playerInventory) {
                return true;
            }
            return super.canInsertIntoSlot(stack, slot);
        }

        @Override
        public ItemStack quickMove(PlayerEntity player, int slotIndex) {
            Slot slot = getSlot(slotIndex);
            if (slot.inventory == playerInventory) {
                ItemStack stack = slot.getStack();
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                Item item = stack.getItem();
                if (item == Items.EMERALD || item == Items.EMERALD_BLOCK) {
                    bankInventory.setStack(BankInventory.INPUT_SLOT, stack);
                    slot.takeStack(stack.getCount());
                    bankInventory.markDirty();
                    return ItemStack.EMPTY;
                }
            }

            int count = bankInventory.getCount();
            ItemStack stack = new ItemStack(Items.EMERALD, count);
            playerInventory.insertStack(stack);
            bankInventory.increaseMoney(stack.getCount() - count);
            bankInventory.markDirty();
            return ItemStack.EMPTY;
        }
    }

    @RequiredArgsConstructor
    public static class BankInventory implements Inventory {
        static final int INPUT_SLOT = 0;
        static final int OUTPUT_SLOT = 1;

        private final Handler handler;

        private PlayerBankData bankData;

        @Override
        public void clear() {
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return true;
        }

        @Override
        public ItemStack getStack(int slot) {
            if (slot == OUTPUT_SLOT && getCount() > 0) {
                return new ItemStack(Items.EMERALD, Math.min(64, getCount()));
            }
            return ItemStack.EMPTY;
        }

        @Override
        public boolean isEmpty() {
            return getCount() == 0;
        }

        @Override
        public void markDirty() {
            log.info("money: {}", bankData.getDeposit());
        }

        public int getCount() {
            BigDecimal count = currentValue().get();
            int value = (int) count.doubleValue();
            if (value <= 0) {
                value = 0;
            }
            return value;
        }

        public String getReadableCount() {
            return currentValue().get().setScale(3, RoundingMode.DOWN).toEngineeringString();
        }

        public void increaseMoney(int count) {
            Value<BigDecimal> value = currentValue();
            value.set(value.get().add(BigDecimal.valueOf(count)));
            Value<BigDecimal> log = currentLogValue();
            if (log != null) {
                log.set(log.get().add(BigDecimal.valueOf(count)));
            }
            handler.playYesSound();
            if (handler.playerInventory.player instanceof ServerPlayerEntity serverPlayer) {
                SaveMoneyCriterion.getCriterion().trigger(serverPlayer);
            }
        }

        private Value<BigDecimal> currentValue() {
            return new Value<>(bankData::getDeposit, bankData::setDeposit);
        }

        private Value<BigDecimal> currentLogValue() {
            return new Value<>(bankData::getDepositOneDay, bankData::setDepositOneDay);
        }

        @Override
        public ItemStack removeStack(int slot) {
            if (slot == OUTPUT_SLOT && getCount() > 0) {
                int removeCount = Math.min(64, getCount());
                increaseMoney(-removeCount);
                return new ItemStack(Items.EMERALD, removeCount);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            if (slot == OUTPUT_SLOT && getCount() > 0) {
                int removeCount = Math.min(amount, getCount());
                increaseMoney(-removeCount);
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
                int amount = Math.min(64, getCount()) - stack.getCount();
                if (amount < 0) {
                    throw new IllegalArgumentException();
                }
                increaseMoney(-amount);
                return;
            }
            Item item = stack.getItem();
            if (item == Items.EMERALD) {
                increaseMoney(stack.getCount());
            } else if (item == Items.EMERALD_BLOCK) {
                increaseMoney(stack.getCount() * 9);
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
    public static class Factory implements ExtendedScreenHandlerFactory {
        private final ClarkVillagerEntity entity;

        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            return new Handler(syncId, inv, player, entity);
        }

        @Override
        public Text getDisplayName() {
            return entity.getName();
        }

        @Override
        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
            PlayerBankData data = BankData.getBankData(player.getServer()).getPlayerData(player);
            NbtCompound nbt = new NbtCompound();
            data.writeNbt(nbt);
            buf.writeNbt(nbt);
        }
    }
}
