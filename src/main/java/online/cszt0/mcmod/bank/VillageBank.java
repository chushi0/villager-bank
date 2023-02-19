package online.cszt0.mcmod.bank;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Identifier;
import online.cszt0.mcmod.bank.entity.ClarkVillagerEntity;

@Slf4j(topic = VillageBank.MODID)
public class VillageBank implements ModInitializer {
    public static final String MODID = "village_bank";

    @Override
    public void onInitialize() {
        ClarkVillagerEntity.initialize();
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
            content.add(ClarkVillagerEntity.getSpawnEgg());
        });
        log.info("initialized");
    }

    public static Identifier identity(String id) {
        return new Identifier(MODID, id);
    }
}
