package io.github.thebusybiscuit.slimefun4.core.services.localization;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.config.Config;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.services.LocalizationService;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.qscbm.slimefun4.services.LanguageService;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * This is an abstract parent class of {@link LocalizationService}.
 * There is not really much more I can say besides that...
 *
 * @author TheBusyBiscuit
 * @see LocalizationService
 */
public abstract class SlimefunLocalization implements Keyed {
    private final Config defaultConfig;

    protected SlimefunLocalization(Slimefun plugin) {
        this.defaultConfig = new Config(plugin, "messages.yml");
    }

    protected Config getConfig() {
        return defaultConfig;
    }

    /**
     * Saves this Localization to its File
     */
    protected void save() {
        defaultConfig.save();
    }

    /**
     * This returns the chat prefix for our messages.
     * Every message (unless explicitly omitted) will have this
     * prefix prepended.
     *
     * @return The chat prefix
     */
    public String getChatPrefix() {
        return getMessage("prefix");
    }

    /**
     * This method attempts to return the {@link Language} with the given
     * language code.
     *
     * @param id The language code
     * @return A {@link Language} with the given id or null
     */
    public abstract @Nullable Language getLanguage(String id);

    /**
     * This method returns the currently selected {@link Language} of a {@link Player}.
     *
     * @param p The {@link Player} to query
     * @return The {@link Language} that was selected by the given {@link Player}
     */
    public abstract @Nullable Language getLanguage(Player p);

    /**
     * This method returns the default {@link Language} of this {@link Server}
     *
     * @return The default {@link Language}
     */
    public abstract @Nullable Language getDefaultLanguage();

    /**
     * This returns whether a {@link Language} with the given id exists within
     * the project resources.
     *
     * @param id The {@link Language} id
     * @return Whether the project contains a {@link Language} with that id
     */
    protected abstract boolean hasLanguage(String id);

    /**
     * This method returns a full {@link Collection} of every {@link Language} that was
     * found.
     *
     * @return A {@link Collection} that contains every installed {@link Language}
     */
    public abstract Collection<Language> getLanguages();

    /**
     * This method adds a new {@link Language} with the given id and texture.
     *
     * @param id      The {@link Language} id
     * @param texture The texture of how this {@link Language} should be displayed
     */
    protected abstract void addLanguage(String id, String texture);

    /**
     * This will load every {@link LanguagePreset} into memory.
     * To be precise: It performs {@link #addLanguage(String, String)} for every
     * value of {@link LanguagePreset}.
     */
    protected void loadEmbeddedLanguages() {
        addLanguage(LanguagePreset.CHINESE_CHINA.getLanguageCode(), LanguagePreset.CHINESE_CHINA.getTexture());
    }

    private FileConfiguration getDefaultFile(LanguageFile file) {
        Language language = getLanguage(LanguagePreset.CHINESE_CHINA.getLanguageCode());
        FileConfiguration fallback = language.getFile(file);

        if (fallback != null) {
            return fallback;
        } else {
            throw new IllegalStateException("Fallback file: \"" + file.getFilePath("en") + "\" is missing!");
        }
    }

    public String getMessage(String key) {
        String message = LanguageService.MESSAGE_MAP.get(key);
        if (message == null) {
            return getDefaultFile(LanguageFile.MESSAGES).getString(key);
        }

        return message;
    }

    public String getMessage(Player p, String key) {
        String message = LanguageService.MESSAGE_MAP.get(key);
        if (message == null) {
            return getDefaultFile(LanguageFile.MESSAGES).getString(key);
        }

        return message;
    }

    /**
     * Returns the Strings referring to the specified Key
     *
     * @param key The Key of those Messages
     * @return The List this key is referring to
     */
    public List<String> getDefaultMessages(String key) {
        return LanguageService.MESSAGES_MAP.get(key);
    }

    public List<String> getMessages(Player p, String key) {
        return LanguageService.MESSAGES_MAP.get(key);
    }

    @ParametersAreNonnullByDefault
    public List<String> getMessages(Player p, String key, UnaryOperator<String> function) {
        List<String> messages = getMessages(p, key);
        messages.replaceAll(function);

        return messages;
    }

    public @Nullable String getResearchName(Player p, NamespacedKey key) {
        return LanguageService.RESEARCH_NAME_MAP.get(key.getNamespace() + '.' + key.getKey());
    }

    public @Nullable String getItemGroupName(Player p, NamespacedKey key) {
        return LanguageService.CATEGORIE_NAME_MAP.get(key.getNamespace() + '.' + key.getKey());
    }

    public @Nullable String getResourceString(Player p, String key) {
        return LanguageService.RESOURCE_NAME_MAP.get(key);
    }

    public ItemStack getRecipeTypeItem(Player p, RecipeType recipeType) {
        ItemStack item = recipeType.toItem();

        if (item == null) {
            // Fixes #3088
            return new ItemStack(Material.AIR);
        }

        NamespacedKey key = recipeType.getKey();

        return new CustomItemStack(item, meta -> {
            String displayName = LanguageService.RECIPE_NAME_MAP.get(key.getNamespace() + "." + key.getKey() + ".name");

            // Set the display name if possible, else keep the default item name.
            if (displayName != null) {
                meta.setDisplayName(ChatColor.AQUA + displayName);
            }

            List<String> lore = LanguageService.RECIPE_LORE_MAP.get(key.getNamespace() + "." + key.getKey() + ".lore");

            // Set the lore if possible, else keep the default lore.
            if (lore != null) {
                lore.replaceAll(line -> ChatColor.GRAY + line);
                meta.setLore(lore);
            }

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        });
    }

    public void sendMessage(CommandSender recipient, String key, boolean addPrefix) {
        String prefix = addPrefix ? getChatPrefix() : "";

        if (recipient instanceof Player player) {
            recipient.sendMessage(ChatColors.color(prefix + getMessage(player, key)));
        } else {
            recipient.sendMessage(ChatColor.stripColor(ChatColors.color(prefix + getMessage(key))));
        }
    }

    public void sendActionbarMessage(Player player, String key, boolean addPrefix) {
        String prefix = addPrefix ? getChatPrefix() : "";
        String message = ChatColors.color(prefix + getMessage(player, key));

        BaseComponent[] components = TextComponent.fromLegacyText(message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
    }

    public void sendMessage(CommandSender recipient, String key) {
        sendMessage(recipient, key, true);
    }

    @ParametersAreNonnullByDefault
    public void sendMessage(CommandSender recipient, String key, UnaryOperator<String> function) {
        sendMessage(recipient, key, true, function);
    }

    @ParametersAreNonnullByDefault
    public void sendMessage(CommandSender recipient, String key, boolean addPrefix, UnaryOperator<String> function) {
        String prefix = addPrefix ? getChatPrefix() : "";

        if (recipient instanceof Player player) {
            recipient.sendMessage(ChatColors.color(prefix + function.apply(getMessage(player, key))));
        } else {
            recipient.sendMessage(ChatColor.stripColor(ChatColors.color(prefix + function.apply(getMessage(key)))));
        }
    }

    public void sendMessages(CommandSender recipient, String key) {
        String prefix = getChatPrefix();

        if (recipient instanceof Player player) {
            for (String translation : getMessages(player, key)) {
                String message = ChatColors.color(prefix + translation);
                recipient.sendMessage(message);
            }
        } else {
            for (String translation : getDefaultMessages(key)) {
                String message = ChatColors.color(prefix + translation);
                recipient.sendMessage(ChatColor.stripColor(message));
            }
        }
    }

    @ParametersAreNonnullByDefault
    public void sendMessages(CommandSender recipient, String key, boolean addPrefix, UnaryOperator<String> function) {
        String prefix = addPrefix ? getChatPrefix() : "";

        if (recipient instanceof Player player) {
            for (String translation : getMessages(player, key)) {
                String message = ChatColors.color(prefix + function.apply(translation));
                recipient.sendMessage(message);
            }
        } else {
            for (String translation : getDefaultMessages(key)) {
                String message = ChatColors.color(prefix + function.apply(translation));
                recipient.sendMessage(ChatColor.stripColor(message));
            }
        }
    }

    @ParametersAreNonnullByDefault
    public void sendMessages(CommandSender recipient, String key, UnaryOperator<String> function) {
        sendMessages(recipient, key, true, function);
    }

}
