package me.qscbm.slimefun4.services;

import io.github.thebusybiscuit.slimefun4.core.services.localization.Language;
import io.github.thebusybiscuit.slimefun4.core.services.localization.LanguageFile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageService {
    public static final HashMap<String, String> RESEARCH_NAME_MAP = new HashMap<>();

    public static final HashMap<String, String> MESSAGE_MAP = new HashMap<>();

    public static final HashMap<String, List<String>> MESSAGES_MAP = new HashMap<>();

    public static final HashMap<String, String> RECIPE_NAME_MAP = new HashMap<>();

    public static final HashMap<String, List<String>> RECIPE_LORE_MAP = new HashMap<>();

    public static final HashMap<String, String> CATEGORIE_NAME_MAP = new HashMap<>();

    public static final HashMap<String, String> RESOURCE_NAME_MAP = new HashMap<>();

    public static Language language;

    private static final LanguageService INSTANCE = new LanguageService();

    public static LanguageService get() {
        return INSTANCE;
    }

    public void load() {
        language = Slimefun.getLocalization().getLanguage("zh-CN");
        loadMessages();
        loadCategories();
        loadRecipes();
        loadResearches();
        loadResources();
    }

    public void loadResearches() {
        FileConfiguration file = language.getFile(LanguageFile.RESEARCHES);
        Map<String, Object> map = file.getValues(true);
        map.forEach((k, v) -> {
            if (v instanceof String s) {
                RESEARCH_NAME_MAP.put(k, s);
            }
        });
    }

    public void loadMessages() {
        FileConfiguration file = language.getFile(LanguageFile.MESSAGES);
        Map<String, Object> map = file.getValues(true);
        map.forEach((k, v) -> {
            if (v instanceof String s) {
                MESSAGE_MAP.put(k, s);
            } else if (v instanceof List<?> l) {
                MESSAGES_MAP.put(k, (List<String>) l);
            }
        });
    }

    public void loadResources() {
        FileConfiguration file = language.getFile(LanguageFile.RESOURCES);
        Map<String, Object> map = file.getValues(true);
        map.forEach((k, v) -> {
            if (v instanceof String s) {
                RESOURCE_NAME_MAP.put(k, s);
            }
        });
    }

    public void loadCategories() {
        FileConfiguration file = language.getFile(LanguageFile.CATEGORIES);
        Map<String, Object> map = file.getValues(true);
        map.forEach((k, v) -> {
            if (v instanceof String s) {
                CATEGORIE_NAME_MAP.put(k, s);
            }
        });
    }

    public void loadRecipes() {
        FileConfiguration file = language.getFile(LanguageFile.RECIPES);
        Map<String, Object> map = file.getValues(true);
        map.forEach((k, v) -> {
            if (v instanceof String s) {
                RECIPE_NAME_MAP.put(k, s);
            } else if (v instanceof List<?> l) {
                RECIPE_LORE_MAP.put(k, (List<String>) l);
            }
        });
    }
}
