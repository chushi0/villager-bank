package online.cszt0.mcmod.bank.net;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
// @Slf4j(topic = VillageBank.MODID)
public class NetClient {
    @Getter
    private static final NetClient instance = new NetClient();

    public void initialize() {
        // ClientPlayNetworking.registerGlobalReceiver(PackageIdentifier.OPEN_CLARK_SCENE,
        // this::onOpenClarkScene);
    }

    // @Deprecated(forRemoval = true, since = "0.0.0")
    // private void onOpenClarkScene(MinecraftClient minecraftclient,
    // ClientPlayNetworkHandler clientplaynetworkhandler,
    // PacketByteBuf packetbytebuf, PacketSender packetsender) {
    // OpenClarkScenePackage pkg = new OpenClarkScenePackage();
    // pkg.unmarshal(packetbytebuf);
    // log.info("onOpenClarkScreen: entity {}", pkg.getEntity());
    // BankScreen.Handler handler = new
    // BankScreen.Handler(minecraftclient.player.getInventory(), 0);
    // handler.setEntityUuid(pkg.getEntity());
    // minecraftclient.execute(() -> {
    // minecraftclient.player.currentScreenHandler = handler;
    // minecraftclient.setScreen(new BankScreen(handler,
    // minecraftclient.player.getInventory(), pkg.getName()));
    // });
    // }
}
