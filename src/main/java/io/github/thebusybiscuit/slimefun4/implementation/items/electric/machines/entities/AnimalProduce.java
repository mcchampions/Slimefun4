package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities;

import java.util.function.Predicate;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

/**
 * An {@link AnimalProduce} can be obtained via the {@link ProduceCollector}.
 *
 * @author TheBusyBiscuit
 *
 * @see ProduceCollector
 *
 */
public class AnimalProduce extends MachineRecipe implements Predicate<LivingEntity> {

    private final Predicate<LivingEntity> predicate;

    @ParametersAreNonnullByDefault
    public AnimalProduce(ItemStack input, ItemStack result, Predicate<LivingEntity> predicate) {
        super(5, new ItemStack[] {input}, new ItemStack[] {result});

        this.predicate = predicate;
    }

    @Override
    public boolean test(LivingEntity entity) {
        return predicate.test(entity);
    }
}
