package online.cszt0.mcmod.bank;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Identifier;
import online.cszt0.mcmod.bank.command.BankCommand;
import online.cszt0.mcmod.bank.criterion.ConvertClarkCriterion;
import online.cszt0.mcmod.bank.data.BankData;
import online.cszt0.mcmod.bank.entity.ClarkVillagerEntity;
import online.cszt0.mcmod.bank.item.DepositCertificate;
import online.cszt0.mcmod.bank.screen.BankScreen;

@Slf4j(topic = VillageBank.MODID)
public class VillageBank implements ModInitializer {
    public static final String MODID = "village_bank";

    @Override
    public void onInitialize() {
        DepositCertificate.initialize();
        ClarkVillagerEntity.initialize();
        ConvertClarkCriterion.initialize();
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
            content.add(ClarkVillagerEntity.getSpawnEgg());
        });
        BankScreen.initializeHandler();
        BankCommand.initialize();
        BankData.initialize();
        log.info("initialized");
    }

    public static Identifier identity(String id) {
        return new Identifier(MODID, id);
    }
}
