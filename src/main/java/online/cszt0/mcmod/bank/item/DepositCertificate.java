package online.cszt0.mcmod.bank.item;

import java.math.BigDecimal;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import online.cszt0.mcmod.bank.VillageBank;

@Slf4j(topic = VillageBank.MODID)
public class DepositCertificate extends Item {

    public static final DepositCertificate ITEM = new DepositCertificate();

    // 证明类型
    public static final String NBTKEY_CHARGE_TYPE = "ChargeType";
    // 周期
    public static final String NBTKEY_TIME = "Time";
    // 开始时间
    public static final String NBTKEY_START_TIME = "StartTime";
    // 本金
    public static final String NBTKEY_CAPITAL = "Capital";
    // 已产生利息
    public static final String NBTKEY_PRODUCE_INTEREST = "ProduceInterest";
    // 已结清利息
    public static final String NBTKEY_FINISH_INTEREST = "FinishInterest";
    // 上次结息时间
    public static final String NBTKEY_LAST_INTEREST_TIME = "LastInterestTime";

    // 定期存款证明
    public static final String CHARGETYPE_TIME_DEPOSIT = "TimeDeposit";

    private static final String TRANS_TIME_DEPOSIT = "item.village_bank.deposit_certificate.time_deposit";
    private static final String TRANS_TOOLTIP_CAPTIAL = "tooltip.village_bank.deposit_certificate.capital";
    private static final String TRANS_TOOLTIP_TIME = "tooltip.village_bank.deposit_certificate.time";
    private static final String TRANS_TOOLTIP_PRODUCE_INTEREST = "tooltip.village_bank.deposit_certificate.produce_interest";
    private static final String TRANS_TOOLTIP_FINISH_INTEREST = "tooltip.village_bank.deposit_certificate.finish_interest";
    private static final String TRANS_TOOLTIP_TIP = "tooltip.village_bank.deposit_certificate.tip";

    public DepositCertificate() {
        super(defaultSettings());
    }

    public static void initialize() {
        Registry.register(Registries.ITEM, VillageBank.identity("deposit_certificate"), ITEM);
    }

    private static Settings defaultSettings() {
        return new FabricItemSettings()
                .maxCount(1)
                .rarity(Rarity.UNCOMMON);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        try {
            NbtCompound nbt = stack.getNbt();
            if (nbt == null) {
                return;
            }
            String chargeType = nbt.getString(NBTKEY_CHARGE_TYPE);
            if (CHARGETYPE_TIME_DEPOSIT.equals(chargeType)) {
                int time = nbt.getInt(NBTKEY_TIME);
                BigDecimal captial = new BigDecimal(nbt.getString(NBTKEY_CAPITAL));
                BigDecimal produceInterest = new BigDecimal(nbt.getString(NBTKEY_PRODUCE_INTEREST));
                BigDecimal finishInterest = new BigDecimal(nbt.getString(NBTKEY_FINISH_INTEREST));

                tooltip.add(Text.translatable(TRANS_TOOLTIP_CAPTIAL, captial));
                tooltip.add(Text.translatable(TRANS_TOOLTIP_TIME, time));
                tooltip.add(Text.translatable(TRANS_TOOLTIP_PRODUCE_INTEREST, produceInterest.toEngineeringString()));
                tooltip.add(Text.translatable(TRANS_TOOLTIP_FINISH_INTEREST, finishInterest.toEngineeringString()));
                tooltip.add(Text.literal(""));
                tooltip.add(Text.translatable(TRANS_TOOLTIP_TIP));
            }
        } catch (NumberFormatException | NullPointerException e) {
            log.warn("build tooltip fail", e);
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if (stack != null) {
            NbtCompound nbt = stack.getNbt();
            if (nbt != null) {
                String chargeType = nbt.getString(NBTKEY_CHARGE_TYPE);
                if (CHARGETYPE_TIME_DEPOSIT.equals(chargeType)) {
                    return TRANS_TIME_DEPOSIT;
                }
            }
        }
        return super.getTranslationKey(stack);
    }
}
