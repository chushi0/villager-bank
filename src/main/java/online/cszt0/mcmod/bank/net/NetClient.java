package online.cszt0.mcmod.bank.net;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import online.cszt0.mcmod.bank.VillageBank;
import online.cszt0.mcmod.bank.screen.BankScreen;

@Environment(EnvType.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j(topic = VillageBank.MODID)
public class NetClient {
    @Getter
    private static final NetClient instance = new NetClient();

    public void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(PackageIdentifier.OPEN_CLARK_SCENE, this::onOpenClarkScene);
    }

    private void onOpenClarkScene(MinecraftClient minecraftclient, ClientPlayNetworkHandler clientplaynetworkhandler,
            PacketByteBuf packetbytebuf, PacketSender packetsender) {
        OpenClarkScenePackage pkg = new OpenClarkScenePackage();
        pkg.unmarshal(packetbytebuf);
        log.info("onOpenClarkScreen: entity {}", pkg.getEntity());
        BankScreen.Handler handler = new BankScreen.Handler(minecraftclient.player.getInventory());
        handler.setEntityUuid(pkg.getEntity());
        minecraftclient.execute(() -> {
            minecraftclient.player.currentScreenHandler = handler;
            minecraftclient.setScreen(new BankScreen(handler, minecraftclient.player.getInventory(), pkg.getName()));
        });
    }
}
