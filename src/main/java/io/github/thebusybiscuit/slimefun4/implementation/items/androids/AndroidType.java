package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

/**
 * This enum holds all the different types a {@link ProgrammableAndroid} can represent.
 *
 * @author TheBusyBiscuit
 * @see ProgrammableAndroid
 */
public enum AndroidType {
    /**
     * This is the default type. This {@link ProgrammableAndroid} has no special abilities.
     * But it can move!
     */
    NONE,

    /**
     * This type represents a {@link MinerAndroid}, it can break blocks.
     */
    MINER,

    /**
     * This type stands for the {@link FarmerAndroid}, it can harvest crops.
     */
    FARMER,

    ADVANCED_FARMER,

    /**
     * This type represents the {@link WoodcutterAndroid}, it can chop trees.
     */
    WOODCUTTER,

    /**
     * This type stands for the {@link ButcherAndroid}, it has the ability
     * to damage entities.
     */
    FIGHTER,

    /**
     * The {@link FishermanAndroid} can catch a fish and other materials.
     */
    FISHERMAN,

    /**
     * This type that can represent any other {@link AndroidType} that is not {@code FIGHTER}.
     * This is only used for internal purposes and has no actual implementation of {@link ProgrammableAndroid}
     * that is equivalent to this.
     */
    NON_FIGHTER;

    boolean isType(AndroidType type) {
        return type == NONE || type == this || (type == NON_FIGHTER && this != FIGHTER);
    }
}
