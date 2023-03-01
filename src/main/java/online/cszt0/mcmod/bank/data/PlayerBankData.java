package online.cszt0.mcmod.bank.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.NbtCompound;

@RequiredArgsConstructor
public class PlayerBankData {
    private static final String KEY_DEPOSIT = "Deposit";

    private final BankData bankData;

    // 活期储蓄
    @Getter
    private int deposit;

    public void markDirty() {
        bankData.markDirty();
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt(KEY_DEPOSIT, deposit);
        return;
    }

    public static PlayerBankData createFromNbt(BankData bankData, NbtCompound nbt) {
        PlayerBankData data = new PlayerBankData(bankData);
        data.deposit = nbt.getInt(KEY_DEPOSIT);
        return data;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
        markDirty();
    }
}
