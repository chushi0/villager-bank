package online.cszt0.mcmod.bank.command;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.server.command.ServerCommandSource;

public class BankInfoSuggestion implements SuggestionProvider<ServerCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context,
            SuggestionsBuilder builder) throws CommandSyntaxException {
        String[] all = { "deposit" };
        String input;
        try {
            input = context.getArgument("type", String.class);
        } catch (IllegalArgumentException e) {
            input = "";
        }
        for (String s : all) {
            if (s.startsWith(input)) {
                builder.suggest(s);
            }
        }
        return builder.buildFuture();
    }
}
