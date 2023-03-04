package online.cszt0.mcmod.bank.command;

import java.math.BigDecimal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import online.cszt0.mcmod.bank.data.BankData;
import online.cszt0.mcmod.bank.data.PlayerBankData;
import online.cszt0.mcmod.bank.util.Value;

public class BankCommand {
    public static void initialize() {
        CommandRegistrationCallback.EVENT.register(new BankCommand()::initCommands);
    }

    private void initCommands(CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("bank")
                        .then(CommandManager.literal("lookup")
                                .requires(source -> source.hasPermissionLevel(1))
                                .then(CommandManager.argument("type", StringArgumentType.word())
                                        .suggests(new BankInfoSuggestion())
                                        .executes(this::lookupData)))
                        .then(CommandManager.literal("set")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.argument("type", StringArgumentType.word())
                                        .suggests(new BankInfoSuggestion())
                                        .then(CommandManager.argument("num", StringArgumentType.word())
                                                .executes(this::setData)))));
    }

    private int lookupData(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return 0;
        }
        PlayerBankData data = BankData.getBankData(player.getServer()).getPlayerData(player);
        String type = context.getArgument("type", String.class);
        Value<BigDecimal> value = getValue(type, data);
        if (value == null) {
            throw new SimpleCommandExceptionType(
                    Text.translatable("command.village_bank.error.invalid_argument", type)).create();
        }
        player.sendMessage(Text.translatable("command.village_bank.info.deposit", value.get().toEngineeringString()));
        return 1;
    }

    private int setData(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return 0;
        }
        PlayerBankData data = BankData.getBankData(player.getServer()).getPlayerData(player);
        String type = context.getArgument("type", String.class);
        Value<BigDecimal> value = getValue(type, data);
        if (value == null) {
            throw new SimpleCommandExceptionType(Text.translatable("command.village_bank.error.invalid_argument", type))
                    .create();
        }
        String num = context.getArgument("num", String.class);
        BigDecimal decimal;
        try {
            decimal = new BigDecimal(num);
        } catch (NumberFormatException e) {
            throw new SimpleCommandExceptionType(Text.translatable("command.village_bank.error.invalid_argument", num))
                    .create();
        }
        value.set(decimal);
        player.sendMessage(Text.translatable("command.village_bank.info.deposit", value.get().toEngineeringString()));
        return 1;
    }

    private Value<BigDecimal> getValue(String type, PlayerBankData data) {
        return switch (type) {
            case "deposit" -> new Value<BigDecimal>(data::getDeposit, data::setDeposit);
            default -> null;
        };
    }
}
