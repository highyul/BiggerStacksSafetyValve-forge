package io.github.highyul.biggerstackssafetyvalve.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.highyul.biggerstackssafetyvalve.config.RuntimeConfig;
import io.github.highyul.biggerstackssafetyvalve.config.RuntimeConfigData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;


public class CraftingLimitsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("biggerstacks_shiftclick_craftlimit")
                        .requires(source -> source.hasPermission(4))
                        // no argument: show the current value
                        .executes(context -> {
                            int current = RuntimeConfig.getCraftingLimits();
                            context.getSource().sendSuccess(
                                    () -> Component.literal(
                                            "Shift-click craft limit is " + current
                                                    + " (valid range " + RuntimeConfigData.MIN_CRAFTING_LIMIT
                                                    + " - " + RuntimeConfigData.MAX_CRAFTING_LIMIT + ")"
                                    ),
                                    false
                            );
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(
                                Commands.argument(
                                                "number",
                                                IntegerArgumentType.integer(
                                                        RuntimeConfigData.MIN_CRAFTING_LIMIT,
                                                        RuntimeConfigData.MAX_CRAFTING_LIMIT
                                                )
                                        )
                                        .executes(context -> {

                                            int value = IntegerArgumentType.getInteger(
                                                    context,
                                                    "number"
                                            );

                                            RuntimeConfig.UpdateResult result =
                                                    RuntimeConfig.updateCraftingLimits(value);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal(
                                                            "Shift-click craft limit updated to " + result.applied()
                                                                    + (result.persisted()
                                                                    ? ""
                                                                    : " (applied in memory only: could not write the config file)")
                                                    ),
                                                    true
                                            );

                                            return Command.SINGLE_SUCCESS;
                                        })
                        )
        );
    }
}
