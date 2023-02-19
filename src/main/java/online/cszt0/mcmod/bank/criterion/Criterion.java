package online.cszt0.mcmod.bank.criterion;

import net.minecraft.server.network.ServerPlayerEntity;

public interface Criterion {
    void trigger(ServerPlayerEntity player);
}
