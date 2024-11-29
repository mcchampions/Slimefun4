package io.github.thebusybiscuit.slimefun4.implementation.items.magical;

import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;

/**
 * This {@link SlimefunItem} allows you to convert any {@link ZombieVillager} to
 * their {@link Villager} variant. It is also one of the very few utilisations of {@link EntityInteractHandler}.
 *
 * @author Linox
 *
 * @see EntityInteractHandler
 *
 */
public class MagicalZombiePills extends SimpleSlimefunItem<EntityInteractHandler> implements NotPlaceable {
    public MagicalZombiePills(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);

        addItemHandler(onRightClick());
    }

    @Override
    public EntityInteractHandler getItemHandler() {
        return (e, item, offhand) -> {
            Entity entity = e.getRightClicked();

            if (e.isCancelled()
                    || !Slimefun.getProtectionManager()
                            .hasPermission(e.getPlayer(), entity.getLocation(), Interaction.INTERACT_ENTITY)) {
                // They don't have permission to use it in this area
                return;
            }

            Player p = e.getPlayer();

            if (entity instanceof ZombieVillager zombieVillager) {
                useItem(p, item);
                healZombieVillager(zombieVillager, p);
            } else if (entity instanceof PigZombie pigZombie) {
                useItem(p, item);
                healZombifiedPiglin(pigZombie);
            }
        };
    }

    /**
     * This method cancels {@link PlayerRightClickEvent} to prevent placing {@link MagicalZombiePills}.
     *
     * @return the {@link ItemUseHandler} of this {@link SlimefunItem}
     */
    public static ItemUseHandler onRightClick() {
        return PlayerRightClickEvent::cancel;
    }

    private static void useItem(Player p, ItemStack item) {
        if (p.getGameMode() != GameMode.CREATIVE) {
            ItemUtils.consumeItem(item, false);
        }

        // This is supposed to be a vanilla sound. No need for a SoundEffect
        p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1, 1);
    }

    private static void healZombieVillager(ZombieVillager zombieVillager, Player p) {
        zombieVillager.setConversionTime(1);
        zombieVillager.setConversionPlayer(p);
    }

    private static void healZombifiedPiglin(PigZombie zombiePiglin) {
        Location loc = zombiePiglin.getLocation();

        zombiePiglin.remove();
        loc.getWorld().spawnEntity(loc, EntityType.PIGLIN);
    }
}
