package online.cszt0.mcmod.bank.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import online.cszt0.mcmod.bank.VillageBank;

public class ClarkVillagerEntityRenderer extends MobEntityRenderer<ClarkVillagerEntity, ClarkVillagerEntityModel> {

    public ClarkVillagerEntityRenderer(Context context) {
        super(context, new ClarkVillagerEntityModel(), 0.5f);
    }

    public static void initialize() {
        EntityRendererRegistry.register(ClarkVillagerEntity.getEntityType(), ClarkVillagerEntityRenderer::new);
    }

    @Override
    public Identifier getTexture(ClarkVillagerEntity entity) {
        return VillageBank.identity("textures/entity/clark_village/clark_village.png");
    }
}