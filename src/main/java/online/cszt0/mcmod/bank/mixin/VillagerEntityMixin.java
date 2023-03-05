package online.cszt0.mcmod.bank.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import online.cszt0.mcmod.bank.VillageBank;
import online.cszt0.mcmod.bank.criterion.ConvertClarkCriterion;
import online.cszt0.mcmod.bank.entity.ClarkVillagerEntity;

@Mixin(VillagerEntity.class)
@Slf4j(topic = VillageBank.MODID)
public abstract class VillagerEntityMixin {

    @Shadow
    private void releaseAllTickets() {
        throw new IllegalStateException("Stub!");
    }

    @Inject(at = @At("INVOKE"), method = "interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", cancellable = true)
    private void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        VillagerEntity entity = (VillagerEntity) (Object) this;
        log.info("interactMob: {}", entity.getUuid());
        ItemStack itemStack = player.getStackInHand(hand);
        if (!itemStack.isOf(Items.EMERALD_BLOCK)) {
            return;
        }
        info.setReturnValue(ActionResult.success(entity.world.isClient));
        if (!(player instanceof ServerPlayerEntity)) {
            return;
        }
        itemStack.decrement(1);
        entity.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, entity.getSoundPitch());
        ConvertClarkCriterion.getCriterion().trigger((ServerPlayerEntity) player, entity);

        releaseAllTickets();
        entity.convertTo(ClarkVillagerEntity.getEntityType(), true);
    }
}
