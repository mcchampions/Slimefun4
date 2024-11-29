package me.qscbm.slimefun4.commands;

import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.qscbm.slimefun4.helper.ItemHelper;
import net.guizhanss.slimefun4.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Pattern;

public class CalcCommand extends SubCommand {
    private static final Pattern PARAM_FIRST_PATTERN = Pattern.compile("%1");
    private static final Pattern PARAM_SECOND_PATTERN = Pattern.compile("%2");
    private static final Pattern PARAM_THIRD_PATTERN = Pattern.compile("%3");
    private static final Pattern PARAM_FOUTH_PATTERN = Pattern.compile("%4");

    public CalcCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "calc", false);
    }

    @Override
    protected String getDescription() {
        return "commands.calc.description";
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        long amount;
        String reqItem;
        SlimefunItem item;

        if (args.length > 3 || args.length < 2) {
            ChatUtils.sendMessage(sender, "错误的参数数量");
            return;
        }

        if (!sender.hasPermission("slimefun.command.calc")) {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            return;
        }

        reqItem = args[1];

        if (args.length == 2) {
            amount = 1;
        } else if (!CommonPatterns.NUMERIC.matcher(args[2]).matches()) {
            Slimefun.getLocalization()
                    .sendMessage(
                            sender,
                            "commands.calc.not-a-number-string",
                            true);
            return;
        } else {
            try {
                amount = Long.parseLong(args[2]);
                if (amount == 0 || amount > Integer.MAX_VALUE) {
                    Slimefun.getLocalization()
                            .sendMessage(
                                    sender,
                                    "commands.calc.not-a-number-string",
                                    true);
                    return;
                }
            } catch (NumberFormatException e) {
                Slimefun.getLocalization()
                        .sendMessage(
                                sender,
                                "commands.calc.not-a-number-string",
                                true);
                return;
            }
        }

        item = SlimefunItem.getById(reqItem.toUpperCase(Locale.ROOT));

        if (item == null) {
            item = SlimefunItem.getByName(reqItem);
            if (item == null) {
                Slimefun.getLocalization()
                        .sendMessage(
                                sender,
                                "commands.calc.no-item-string",
                                true);
                return;
            }
        }

        printResults(sender, item, amount);
    }

    public static void printResults(CommandSender sender, SlimefunItem item, long amount) {
        Slimefun.runAsync(() -> {
            Map<ItemStack, Long> results = calculate(item, amount);

            String header;
            String name = item.getItemNormalName();
            if (amount == 1) {
                header = PARAM_FIRST_PATTERN.matcher(Slimefun.getLocalization().getMessage("commands.calc.header-string")).replaceFirst(name);
            } else {
                header = PARAM_SECOND_PATTERN.matcher(PARAM_FIRST_PATTERN.matcher(Slimefun.getLocalization().getMessage("commands.calc.header-amount-string")).replaceFirst(name)).replaceFirst(String.valueOf(amount));
            }

            ChatUtils.sendMessage(sender, header);

            List<Map.Entry<ItemStack, Long>> entries = new ArrayList<>(results.entrySet());
            entries.sort(Comparator.comparingLong(Map.Entry::getValue));

            for (Map.Entry<ItemStack, Long> entry : entries) {
                long originalValues = entry.getValue();
                if (originalValues <= 0) continue;
                String parsedAmount;
                int maxStackSize = entry.getKey().getMaxStackSize();
                if (originalValues <= maxStackSize) {
                    parsedAmount = Long.toString(originalValues);
                } else {
                    parsedAmount = PARAM_FOUTH_PATTERN.matcher(PARAM_THIRD_PATTERN.matcher(PARAM_SECOND_PATTERN.matcher(PARAM_FIRST_PATTERN.matcher(Slimefun.getLocalization().getMessage("commands.calc.stack-string")).replaceFirst(String.valueOf(originalValues))).replaceFirst(String.valueOf(Math.floor(originalValues / (float) maxStackSize)))).replaceFirst(String.valueOf(maxStackSize))).replaceFirst(String.valueOf(originalValues % maxStackSize));
                }
                Slimefun.getLocalization().sendMessage(
                        sender, "commands.calc.amount-string", (m) ->
                                PARAM_SECOND_PATTERN.matcher(PARAM_FIRST_PATTERN.matcher(m).replaceFirst(ItemHelper.getItemName(entry.getKey()))).replaceFirst(parsedAmount)
                );
            }
        });
    }

    public static Map<ItemStack, Long> calculate(SlimefunItem parent, long amount) {
        Map<ItemStack, Long> result = new HashMap<>();

        int multiplier = parent.getRecipeOutput().getAmount();
        long operations = (amount + multiplier - 1) / multiplier;
        for (ItemStack item : parent.getRecipe()) {
            if (item == null) {
                continue;
            }
            add(result, item, item.getAmount() * operations);
        }

        SlimefunItemStack next = getNextItem(result);
        while (next != null) {
            multiplier = next.getItem().getRecipeOutput().getAmount();
            operations = (result.get(next) + multiplier - 1) / multiplier;
            add(result, next, -(multiplier * operations));
            for (ItemStack item : next.getItem().getRecipe()) {
                if (item == null) {
                    continue;
                }
                add(result, item, item.getAmount() * operations);
            }
            next = getNextItem(result);
        }

        return result;
    }

    private static SlimefunItemStack getNextItem(Map<ItemStack, Long> map) {
        for (Map.Entry<ItemStack, Long> entry : map.entrySet()) {
            if (entry.getKey() instanceof SlimefunItemStack ingredient) {
                if (ingredient.getItem() != null) {
                    if (entry.getValue() > 0) {
                        return ingredient;
                    }
                }
            }
        }
        return null;
    }

    private static void add(Map<ItemStack, Long> map, ItemStack key, long amount) {
        ItemStack clone = key.clone();
        clone.setAmount(1);
        map.merge(clone, amount, Long::sum);
    }
}
