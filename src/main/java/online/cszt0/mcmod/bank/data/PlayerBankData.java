package online.cszt0.mcmod.bank.data;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.NbtCompound;
import online.cszt0.mcmod.bank.util.Serialize;

@RequiredArgsConstructor
public class PlayerBankData {
    private static final String KEY_DEPOSIT = "Deposit";
    // 活期存款利息 0.041%
    private static final BigDecimal INTEREST_DEPOSIT = new BigDecimal("0.00041");

    private final BankData bankData;

    // 活期储蓄
    @Getter
    private BigDecimal deposit = BigDecimal.ZERO;

    // 一天内的交易额
    @Getter
    private BigDecimal depositOneDay = BigDecimal.ZERO;

    public void markDirty() {
        if (bankData != null) {
            bankData.markDirty();
        }
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putByteArray(KEY_DEPOSIT, Serialize.bigDecimal().serialize(deposit));
    }

    public static PlayerBankData createFromNbt(BankData bankData, NbtCompound nbt) {
        PlayerBankData data = new PlayerBankData(bankData);
        data.deposit = (BigDecimal) Serialize.bigDecimal().deserialize(nbt.getByteArray(KEY_DEPOSIT));
        return data;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
        markDirty();
    }

    public void setDepositOneDay(BigDecimal depositOneDay) {
        this.depositOneDay = depositOneDay;
        markDirty();
    }

    public void updateNewDay() {
        this.depositOneDay = BigDecimal.ZERO;
        // 月初发利息
        if (bankData.getWorldData().getDayOfMonth() == 0) {
            BigDecimal interest = deposit.multiply(INTEREST_DEPOSIT);
            deposit = deposit.add(interest);
            markDirty();
        }
    }
}
