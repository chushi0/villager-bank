package online.cszt0.mcmod.bank.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.NbtCompound;

@RequiredArgsConstructor
public class WorldData {
    // 当前是第几天
    private static final String KEY_DAY = "Day";

    private final BankData bankData;

    @Getter
    private long day;

    private void markDirty() {
        if (bankData != null) {
            bankData.markDirty();
        }
    }

    public void readNbt(NbtCompound nbt) {
        day = nbt.getLong(KEY_DAY);
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putLong(KEY_DAY, day);
    }

    public void setDay(long day) {
        this.day = day;
        markDirty();
    }

    public int getDayOfMonth() {
        return (int) (getDay() % 30);
    }

    public void updateNewDay() {
        markDirty();
    }
}
