package me.qscbm.slimefun4.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.bakedlibs.dough.reflection.ReflectionGetterMethodFunction;
import io.github.bakedlibs.dough.reflection.ReflectionUtils;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NBTUtils {
    public static final String CRAFTBUKKIT_PACKAGE_NAME = Bukkit.getServer().getClass().getPackage().getName();

    public static final String VERSION;

    private static final Class<?> CRAFT_SKULL_META_CLAZZ;

    private static final Field SKULL_PROFILE;

    private static final Class<?> RESOLVABLE_PROFILE;

    private static Method RESOLVABLE_PROFILE_GAME_PROFILE_GETTER;

    private static final Method PROPERTY_NAME_GETTER;

    private static final Method PROPERTY_VALUE_GETTER;

    private static ReflectionGetterMethodFunction RESOLVABLE_PROFILE_GAME_PROFILE_GETTER_FUNCTION;

    private static ReflectionGetterMethodFunction PROPERTY_NAME_GETTER_FUNCTION;

    private static ReflectionGetterMethodFunction PROPERTY_VALUE_GETTER_FUNCTION;

    static {
        String detectedVersion = CRAFTBUKKIT_PACKAGE_NAME.substring(CRAFTBUKKIT_PACKAGE_NAME.lastIndexOf('.') + 1);
        if (detectedVersion.charAt(0) != 'v') {
            // Paper or something...
            detectedVersion = VersionUtils.getBukkitVersion();
        }
        VERSION = detectedVersion;
        try {
            CRAFT_SKULL_META_CLAZZ = Class.forName(CRAFTBUKKIT_PACKAGE_NAME + ".inventory." + "CraftMetaSkull");
            SKULL_PROFILE = CRAFT_SKULL_META_CLAZZ.getDeclaredField("profile");
            SKULL_PROFILE.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        Class<?> resolvableProfile;
        try {
            resolvableProfile = Class.forName("net.minecraft.world.item.component.ResolvableProfile");
            RESOLVABLE_PROFILE_GAME_PROFILE_GETTER = ReflectionUtils.getMethod(resolvableProfile, "f");
            if (RESOLVABLE_PROFILE_GAME_PROFILE_GETTER != null) {
                RESOLVABLE_PROFILE_GAME_PROFILE_GETTER.setAccessible(true);
                RESOLVABLE_PROFILE_GAME_PROFILE_GETTER_FUNCTION = ReflectionUtils.createGetterFunction(RESOLVABLE_PROFILE_GAME_PROFILE_GETTER);
            } else {
                RESOLVABLE_PROFILE_GAME_PROFILE_GETTER = ReflectionUtils.getMethod(resolvableProfile, "g");
                if (RESOLVABLE_PROFILE_GAME_PROFILE_GETTER != null) {
                    RESOLVABLE_PROFILE_GAME_PROFILE_GETTER.setAccessible(true);
                    RESOLVABLE_PROFILE_GAME_PROFILE_GETTER_FUNCTION = ReflectionUtils.createGetterFunction(RESOLVABLE_PROFILE_GAME_PROFILE_GETTER);
                }
            }
        } catch (ClassNotFoundException e) {
            resolvableProfile = null;
        }
        RESOLVABLE_PROFILE = resolvableProfile;
        PROPERTY_NAME_GETTER = ReflectionUtils.getMethod(Property.class, "getName");
        if (PROPERTY_NAME_GETTER != null) {
            PROPERTY_NAME_GETTER.setAccessible(true);
            PROPERTY_NAME_GETTER_FUNCTION = ReflectionUtils.createGetterFunction(PROPERTY_NAME_GETTER);
        }
        PROPERTY_VALUE_GETTER = ReflectionUtils.getMethod(Property.class, "getValue");
        if (PROPERTY_VALUE_GETTER != null) {
            PROPERTY_VALUE_GETTER.setAccessible(true);
            PROPERTY_VALUE_GETTER_FUNCTION = ReflectionUtils.createGetterFunction(PROPERTY_VALUE_GETTER);
        }
    }

    public static String getTexture(SkullMeta skullMeta) {
        try {
            Object pro = SKULL_PROFILE.get(skullMeta);
            if (RESOLVABLE_PROFILE != null && RESOLVABLE_PROFILE.isInstance(pro)) {
                if (RESOLVABLE_PROFILE_GAME_PROFILE_GETTER != null) {
                    pro = RESOLVABLE_PROFILE_GAME_PROFILE_GETTER_FUNCTION.invoke(pro);
                }
            }
            if (pro == null) {
                return null;
            }
            GameProfile profile = (GameProfile) pro;
            Collection<Property> properties = profile.getProperties().values();
            for (Property prop : properties) {
                String texture = null;
                if (PROPERTY_NAME_GETTER != null) {
                    if ("textures".equals(PROPERTY_NAME_GETTER_FUNCTION.invoke(prop))) {
                        texture = new String(Base64.getDecoder().decode((String) PROPERTY_VALUE_GETTER_FUNCTION.invoke(prop)));
                    }
                } else {
                    if ("textures".equals(prop.name())) {
                        texture = new String(Base64.getDecoder().decode(prop.value()));
                    }
                }
                return getMatch(texture, "\\{\"url\":\"(.*?)\"\\}");
            }
            return null;
        } catch (IllegalAccessException e) {
            Slimefun.logger().warning(e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static String getMatch(String string, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
}
