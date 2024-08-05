package io.github.thebusybiscuit.slimefun4.core.services.localization;

/**
 * This enum holds the different types of files each {@link Language} holds.
 *
 * @author TheBusyBiscuit
 *
 * @see Language
 * @see SlimefunLocalization
 *
 */
public enum LanguageFile {
    MESSAGES("messages.yml"),
    CATEGORIES("categories.yml"),
    RECIPES("recipes.yml"),
    RESOURCES("resources.yml"),
    RESEARCHES("researches.yml");

    static final LanguageFile[] valuesCached = values();

    private final String fileName;

    LanguageFile(String fileName) {
        this.fileName = fileName;
    }


    public String getFilePath(Language language) {
        return getFilePath(language.getId());
    }


    public String getFilePath(String languageId) {
        return "/languages/zh-CN/" + fileName;
    }
}
