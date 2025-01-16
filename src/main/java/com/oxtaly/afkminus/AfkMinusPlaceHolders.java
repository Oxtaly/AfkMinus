package com.oxtaly.afkminus;

import com.oxtaly.afkminus.api.AfkMinusAPI;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.oxtaly.afkminus.AfkMinus.MOD_ID;

public class AfkMinusPlaceHolders {
    public static final Identifier AFK_PLACEHOLDER = Identifier.of(MOD_ID, "afk");

    public static void register() {
        Placeholders.register(AFK_PLACEHOLDER, (context, argument) -> {
            if (context.player() != null) {
                if(AfkMinusAPI.isAFK(context.player()))
                    return PlaceholderResult.value(TextParserUtils.formatText(AfkMinus.CONFIG_MANAGER.getData().afkPlaceholder));
                return PlaceholderResult.value(Text.empty());
            }
            return PlaceholderResult.invalid("Missing player!");
        });
    }
}
