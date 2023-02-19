package online.cszt0.mcmod.bank.entity;

import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.StopAndLookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import online.cszt0.mcmod.bank.VillageBank;
import online.cszt0.mcmod.bank.net.OpenClarkScenePackage;
import online.cszt0.mcmod.bank.net.PackageIdentifier;
import online.cszt0.mcmod.bank.screen.BankScreen;

public class ClarkVillagerEntity extends MerchantEntity {

    @Getter
    private static EntityType<ClarkVillagerEntity> entityType;
    @Getter
    private static Item spawnEgg;

    public ClarkVillagerEntity(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    public static void initialize() {
        entityType = Registry.register(Registries.ENTITY_TYPE, VillageBank.identity("clark_village"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ClarkVillagerEntity::new)
                        .dimensions(EntityDimensions.fixed(0.75f, 0.75f))
                        .build());
        FabricDefaultAttributeRegistry.register(entityType, createMobAttributes());
        spawnEgg = new SpawnEggItem(entityType, 0xc4c4c4, 0xadadad, new Item.Settings());
        Registry.register(Registries.ITEM, VillageBank.identity("clark_village_spawn_egg"), spawnEgg);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, ZombieEntity.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, EvokerEntity.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, VindicatorEntity.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, VexEntity.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, PillagerEntity.class, 15.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, IllusionerEntity.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, ZoglinEntity.class, 10.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 0.5D));
        this.goalSelector.add(4, new GoToWalkTargetGoal(this, 0.35D));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 0.35D));
        this.goalSelector.add(9, new StopAndLookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!isAlive() || hasCustomer()) {
            return super.interactMob(player, hand);
        }
        setCustomer(player);
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new BankScreen()));
        ServerPlayNetworking.send((ServerPlayerEntity) player, PackageIdentifier.OPEN_CLARK_SCENE,
                new OpenClarkScenePackage(getUuid()).getMarshalResult());
        return ActionResult.success(world.isClient);
    }

    @Override
    protected void afterUsing(TradeOffer offer) {
    }

    @Override
    protected void fillRecipes() {
    }

    @Override
    @Nullable
    public PassiveEntity createChild(ServerWorld arg0, PassiveEntity arg1) {
        return null;
    }

}
