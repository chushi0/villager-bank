package online.cszt0.mcmod.bank.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import online.cszt0.mcmod.bank.VillageBank;

public class ClarkVillagerEntityRenderer extends MobEntityRenderer<ClarkVillagerEntity, ClarkVillagerEntityModel> {

    public ClarkVillagerEntityRenderer(Context context) {
        super(context, new ClarkVillagerEntityModel(), 0.5f);
        this.addFeature(new ItemFeatureRenderer(this, context.getHeldItemRenderer()));
    }

    public static void initialize() {
        EntityRendererRegistry.register(ClarkVillagerEntity.getEntityType(), ClarkVillagerEntityRenderer::new);
    }

    @Override
    public Identifier getTexture(ClarkVillagerEntity entity) {
        return VillageBank.identity("textures/entity/clark_village/clark_villager.png");
    }

    @Override
    protected void scale(ClarkVillagerEntity villagerEntity, MatrixStack matrixStack, float f) {
        float g = 0.9375F;

        matrixStack.scale(g, g, g);
    }

    static class ItemFeatureRenderer extends FeatureRenderer<ClarkVillagerEntity, ClarkVillagerEntityModel> {

        private final HeldItemRenderer heldItemRenderer;

        public ItemFeatureRenderer(FeatureRendererContext<ClarkVillagerEntity, ClarkVillagerEntityModel> context,
                HeldItemRenderer heldItemRenderer) {
            super(context);
            this.heldItemRenderer = heldItemRenderer;
        }

        public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i,
                ClarkVillagerEntity livingEntity, float f, float g, float h, float j, float k, float l) {
            matrixStack.push();
            matrixStack.translate(0.0F, 0.4F, -0.4F);
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
            ItemStack itemStack = new ItemStack(Items.EMERALD_BLOCK);
            this.heldItemRenderer.renderItem(livingEntity, itemStack, Mode.GROUND, false, matrixStack,
                    vertexConsumerProvider, i);
            matrixStack.pop();
        }
    }
}