package online.cszt0.mcmod.bank.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Getter;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate.Extended;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import online.cszt0.mcmod.bank.VillageBank;

public class ConvertClarkCriterion extends AbstractCriterion<ConvertClarkCriterion.Condition> {
    private static final Identifier identifier = VillageBank.identity("criterion_convert_clark");
    @Getter
    private static ConvertClarkCriterion criterion;

    public static void initialize() {
        criterion = Criteria.register(new ConvertClarkCriterion());
    }

    @Override
    public Identifier getId() {
        return identifier;
    }

    @Override
    protected Condition conditionsFromJson(JsonObject obj, Extended playerPredicate,
            AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Condition(obj.get("baby"));
    }

    public void trigger(ServerPlayerEntity player, VillagerEntity villager) {
        trigger(player, condition -> condition.test(player, villager));
    }

    public static class Condition extends AbstractCriterionConditions {
        private final boolean hasIsBaby;
        private final boolean isBaby;

        public Condition(JsonElement isBaby) {
            super(identifier, Extended.EMPTY);
            if (isBaby != null) {
                this.hasIsBaby = true;
                this.isBaby = isBaby.getAsBoolean();
            } else {
                this.hasIsBaby = false;
                this.isBaby = false;
            }
        }

        public boolean test(ServerPlayerEntity player, VillagerEntity villager) {
            if (hasIsBaby) {
                boolean villagerIsBaby = villager.isBaby();
                if (villagerIsBaby != this.isBaby) {
                    return false;
                }
            }
            return true;
        }
    }
}
