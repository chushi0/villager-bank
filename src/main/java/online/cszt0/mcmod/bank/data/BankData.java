package online.cszt0.mcmod.bank.data;

import java.util.HashMap;
import java.util.UUID;

import lombok.Getter;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import online.cszt0.mcmod.bank.VillageBank;

public class BankData extends PersistentState {
    private static final String KEY_PLAYERS_BANK_DATA = "PlayersBankData";
    private static final String KEY_WORLD_DATA = "WorldData";

    // 玩家个人数据
    private final HashMap<UUID, PlayerBankData> playersBankData = new HashMap<>();

    // 世界全局数据
    @Getter
    private final WorldData worldData = new WorldData(this);

    public static void initialize() {
        ServerTickEvents.START_WORLD_TICK.register(BankData::onWorldTick);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound playersNbtCompound = new NbtCompound();
        playersBankData.forEach((uuid, data) -> {
            NbtCompound playerBankData = new NbtCompound();
            data.writeNbt(playerBankData);
            playersNbtCompound.put(String.valueOf(uuid), playerBankData);
        });
        nbt.put(KEY_PLAYERS_BANK_DATA, playersNbtCompound);
        NbtCompound worlNbtCompound = new NbtCompound();
        worldData.writeNbt(worlNbtCompound);
        nbt.put(KEY_WORLD_DATA, worlNbtCompound);
        return nbt;
    }

    public static BankData createFromNbt(NbtCompound nbt) {
        BankData data = new BankData();
        NbtCompound playersNbtCompound = nbt.getCompound(KEY_PLAYERS_BANK_DATA);
        playersNbtCompound.getKeys().forEach(uuid -> data.playersBankData.put(UUID.fromString(uuid),
                PlayerBankData.createFromNbt(data, playersNbtCompound.getCompound(uuid))));
        NbtCompound worldNbtCompound = nbt.getCompound(KEY_WORLD_DATA);
        if (worldNbtCompound != null) {
            data.worldData.readNbt(worldNbtCompound);
        }
        return data;
    }

    public static BankData getBankData(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        BankData data = persistentStateManager.getOrCreate(BankData::createFromNbt, BankData::new, VillageBank.MODID);
        return data;
    }

    public synchronized PlayerBankData getPlayerData(PlayerEntity player) {
        PlayerBankData data = playersBankData.get(player.getUuid());
        if (data == null) {
            data = new PlayerBankData(this);
            playersBankData.put(player.getUuid(), data);
            markDirty();
        }
        return data;
    }

    private static void onWorldTick(ServerWorld world) {
        long tick = world.getTimeOfDay();
        long time = tick % 24000;
        long day = tick / 24000;
        // avoid frequently update
        if (time % 100 != 0) {
            return;
        }
        BankData bankData = getBankData(world.getServer());
        WorldData worldData = bankData.getWorldData();
        if (day == worldData.getDay()) {
            return;
        }
        if (day < worldData.getDay()) {
            worldData.setDay(day);
            return;
        }
        worldData.setDay(day);
        bankData.playersBankData.forEach((uuid, data) -> data.updateNewDay());
        worldData.updateNewDay();
    }
}
