package online.cszt0.mcmod.bank.data;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import online.cszt0.mcmod.bank.VillageBank;

public class BankData extends PersistentState {
    private static final String KEY_PLAYERS_BANK_DATA = "PlayersBankData";

    private final HashMap<UUID, PlayerBankData> playersBankData = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound playersNbtCompound = new NbtCompound();
        playersBankData.forEach((uuid, data) -> {
            NbtCompound playerBankData = new NbtCompound();
            data.writeNbt(playerBankData);
            playersNbtCompound.put(String.valueOf(uuid), playerBankData);
        });
        nbt.put(KEY_PLAYERS_BANK_DATA, playersNbtCompound);
        return nbt;
    }

    public static BankData createFromNbt(NbtCompound nbt) {
        BankData data = new BankData();
        NbtCompound playersNbtCompound = nbt.getCompound(KEY_PLAYERS_BANK_DATA);
        playersNbtCompound.getKeys().forEach(uuid -> data.playersBankData.put(UUID.fromString(uuid),
                PlayerBankData.createFromNbt(data, playersNbtCompound.getCompound(uuid))));
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
}
