package online.cszt0.mcmod.bank.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Getter;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate.Extended;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import online.cszt0.mcmod.bank.VillageBank;
import online.cszt0.mcmod.bank.data.BankData;
import online.cszt0.mcmod.bank.data.PlayerBankData;

public class SaveMoneyCriterion extends AbstractCriterion<SaveMoneyCriterion.Condition> {
    private static final Identifier identifier = VillageBank.identity("criterion_save_money");

    @Getter
    private static SaveMoneyCriterion criterion;

    public static void initialize() {
        criterion = Criteria.register(new SaveMoneyCriterion());
    }

    public void trigger(ServerPlayerEntity player) {
        trigger(player, condition -> condition.test(player));
    }

    @Override
    public Identifier getId() {
        return identifier;
    }

    @Override
    protected Condition conditionsFromJson(JsonObject obj, Extended playerPredicate,
            AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Condition(obj.get("min"), obj.get("max"));
    }

    public static class Condition extends AbstractCriterionConditions {

        private final boolean hasMin;
        private final boolean hasMax;
        private final int min;
        private final int max;

        public Condition(JsonElement min, JsonElement max) {
            super(identifier, Extended.EMPTY);
            if (min == null) {
                this.hasMin = false;
                this.min = 0;
            } else {
                this.hasMin = true;
                this.min = min.getAsInt();
            }
            if (max == null) {
                this.hasMax = false;
                this.max = 0;
            } else {
                this.hasMax = true;
                this.max = max.getAsInt();
            }
        }

        public boolean test(ServerPlayerEntity player) {
            PlayerBankData data = BankData.getBankData(player.getServer()).getPlayerData(player);
            double v = data.getDepositOneDay().doubleValue();
            if (hasMin && v < min) {
                return false;
            }
            if (hasMax && v > max) {
                return false;
            }
            return true;
        }
    }
}
