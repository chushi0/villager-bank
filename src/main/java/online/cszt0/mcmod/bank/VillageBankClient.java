package online.cszt0.mcmod.bank;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import online.cszt0.mcmod.bank.entity.ClarkVillagerEntityRenderer;
import online.cszt0.mcmod.bank.net.NetClient;

@Environment(EnvType.CLIENT)
public class VillageBankClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClarkVillagerEntityRenderer.initialize();
        NetClient.getInstance().initialize();
    }

}
