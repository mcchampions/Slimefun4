package io.github.thebusybiscuit.slimefun4.api.items;

import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.List;
import java.util.Objects;

import lombok.Getter;

/**
 * This class represents a Setting for a {@link SlimefunItem} that can be modified via
 * the {@code Items.yml} {@link Config} file.
 *
 * @param <T> The type of data stored under this {@link ItemSetting}
 * @author TheBusyBiscuit
 */
public class ItemSetting<T> {
    private final SlimefunItem item;

    @Getter
    private final String key;
    @Getter
    private final T defaultValue;

    private T value;

    /**
     * This creates a new {@link ItemSetting} with the given key and default value
     *
     * @param item         The {@link SlimefunItem} this {@link ItemSetting} belongs to
     * @param key          The key under which this setting will be stored (relative to the {@link SlimefunItem})
     * @param defaultValue The default value for this {@link ItemSetting}
     */
    public ItemSetting(SlimefunItem item, String key, T defaultValue) {
        this.item = item;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * This method checks if a given input would be valid as a value for this
     * {@link ItemSetting}. You can override this method to implement your own checks.
     *
     * @param input The input value to validate
     * @return Whether the given input was valid
     */
    public boolean validateInput(T input) {
        return input != null;
    }

    /**
     * This method updates this {@link ItemSetting} with the given value.
     * Override this method to catch changes of a value.
     * A value may never be null.
     *
     * @param newValue The new value for this {@link ItemSetting}
     */
    public void update(T newValue) {
        this.value = newValue;
    }

    /**
     * This returns the associated {@link SlimefunItem} for this {@link ItemSetting}.
     *
     * @return The associated {@link SlimefunItem}
     */
    protected SlimefunItem getItem() {
        return item;
    }

    /**
     * This returns the <strong>current</strong> value of this {@link ItemSetting}.
     *
     * @return The current value
     */
    public T getValue() {
        if (value != null) {
            /*
             * If the value has been initialized, return it immediately.
             */
            return value;
        } else {
            /*
             * In a normal environment, we can mitigate the issue
             * easily and just print a warning instead.
             */
            item.warn("ItemSetting '" + key + "' was invoked but was not initialized yet.");
            return defaultValue;
        }
    }

    /**
     * This method checks if this {@link ItemSetting} stores the given data type.
     *
     * @param c The class of data type you want to compare
     * @return Whether this {@link ItemSetting} stores the given type
     */
    public boolean isType(Class<?> c) {
        return c.isInstance(defaultValue);
    }

    /**
     * This is an error message which should provide further context on what values
     * are allowed.
     *
     * @return An error message which is displayed when this {@link ItemSetting} is misconfigured.
     */
    protected String getErrorMessage() {
        return "请使用在 '" + defaultValue.getClass().getSimpleName() + "' 范围内的值!";
    }

    /**
     * This method is called by a {@link SlimefunItem} which wants to load its {@link ItemSetting}
     * from the {@link Config} file.
     */
    @SuppressWarnings("unchecked")
    public void reload() {
        Slimefun.getItemCfg().setDefaultValue(item.getId() + '.' + key, defaultValue);
        Object configuredValue = Slimefun.getItemCfg().getValue(item.getId() + '.' + key);

        if (defaultValue.getClass().isInstance(configuredValue)
            || (configuredValue instanceof List && defaultValue instanceof List)) {
            // We can do an unsafe cast here, we did an isInstance(...) check before!
            T newValue = (T) configuredValue;

            if (validateInput(newValue)) {
                this.value = newValue;
            } else {
                item.warn("发现在 Items.yml 中有无效的物品设置!"
                          + "\n  在 \""
                          + item.getId()
                          + "."
                          + key
                          + "\""
                          + "\n  "
                          + configuredValue
                          + " 不是一个有效值!"
                          + "\n"
                          + getErrorMessage());

            }
        } else {
            this.value = defaultValue;
            String found = configuredValue == null
                    ? "null"
                    : configuredValue.getClass().getSimpleName();

            item.warn("发现在 Items.yml 中有无效的物品设置!"
                      + "\n请只设置有效的值."
                      + "\n  在 \""
                      + item.getId()
                      + "."
                      + key
                      + "\""
                      + "\n  期望值为 \""
                      + defaultValue.getClass().getSimpleName()
                      + "\" 但填写了: \""
                      + found
                      + "\"");

        }
    }

    @Override
    public String toString() {
        T currentValue = this.value != null ? this.value : defaultValue;
        return getClass().getSimpleName()
               + " {"
               + key
               + " = "
               + currentValue
               + " (default: "
               + defaultValue
               + ")";
    }

    @Override
    public final int hashCode() {
        return Objects.hash(item, key);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof ItemSetting<?> setting) {
            return Objects.equals(key, setting.key) && Objects.equals(item, setting.item);
        } else {
            return false;
        }
    }
}
