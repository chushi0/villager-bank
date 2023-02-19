package online.cszt0.mcmod.bank.entity;

import net.minecraft.client.render.entity.model.VillagerResemblingModel;

public class ClarkVillagerEntityModel extends VillagerResemblingModel<ClarkVillagerEntity> {

    public ClarkVillagerEntityModel() {
        super(getModelData().getRoot().createPart(64, 64));
    }

}