package online.cszt0.mcmod.bank.criterion;

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

public class ConvertClarkCriterion extends AbstractCriterion<ConvertClarkCriterion.Condition> implements Criterion {
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
        return new Condition();
    }

    @Override
    public void trigger(ServerPlayerEntity player) {
        trigger(player, v -> true);
    }

    public static class Condition extends AbstractCriterionConditions {

        public Condition() {
            super(identifier, Extended.EMPTY);
        }
    }
}
