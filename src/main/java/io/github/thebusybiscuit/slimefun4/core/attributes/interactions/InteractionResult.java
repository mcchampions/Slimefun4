package io.github.thebusybiscuit.slimefun4.core.attributes.interactions;

import io.github.thebusybiscuit.slimefun4.core.attributes.ExternallyInteractable;
import lombok.Getter;

import javax.annotation.Nullable;

/**
 * This class represents the result of an interaction on an {@link ExternallyInteractable} item.
 */
public class InteractionResult {
    @Getter
    private final boolean interactionSuccessful;
    private @Nullable String resultMessage;

    /**
     * Creates a new InteractionResult.
     *
     * @param successful Whether the interaction was successful or not.
     */
    public InteractionResult(boolean successful) {
        this.interactionSuccessful = successful;
    }

    /**
     * Sets a custom result message for this interaction.
     *
     * @param resultMessage The message to be sent with the Result
     */
    public void setResultMessage(@Nullable String resultMessage) {
        this.resultMessage = resultMessage;
    }

    /**
     * Returns whether this result has a result message or is null.
     *
     * @return true if a result message is present
     */
    public boolean hasResultMessage() {
        return this.resultMessage != null;
    }

    /**
     * Returns the custom result message for this result or null if not set.
     *
     * @return A String of the provided custom result message.
     */
    public @Nullable String getResultMessage() {
        return resultMessage;
    }
}
