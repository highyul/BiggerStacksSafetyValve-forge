package io.github.highyul.biggerstackssafetyvalve.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.highyul.biggerstackssafetyvalve.config.RuntimeConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;


public class CraftingLimitsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("biggerstacks_shiftclick_craftlimit")
                        .requires(source -> source.hasPermission(4))
                        .then(
                                Commands.argument(
                                                "number",
                                                IntegerArgumentType.integer(1, Integer.MAX_VALUE)
                                        )
                                        .executes(context -> {

                                            int value = IntegerArgumentType.getInteger(
                                                    context,
                                                    "number"
                                            );

                                            RuntimeConfig.updateCraftingLimits(value);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal(
                                                            "Shift-click craft limit updated to " + value
                                                    ),
                                                    true
                                            );

                                            return Command.SINGLE_SUCCESS;
                                        })
                        )
        );
    }
}


