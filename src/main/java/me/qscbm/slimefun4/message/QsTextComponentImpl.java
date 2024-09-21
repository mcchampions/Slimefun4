package me.qscbm.slimefun4.message;

import net.kyori.adventure.internal.Internals;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.MonkeyBars;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@SuppressWarnings({"UnstableApiUsage", "NullableProblems"})
public class QsTextComponentImpl implements TextComponent {
    protected List<Component> children;
    protected Style style;

    @Override
    public final List<Component> children() {
        return this.children;
    }

    @Override
    public final Style style() {
        return this.style;
    }

    private String content;

    public QsTextComponentImpl(String content) {
        this.children = new ArrayList<>();
        this.style = Style.empty();
        this.content = content;
    }

    @Override
    public String content() {
        return this.content;
    }

    @Override
    public QsTextComponentImpl append(final @NotNull ComponentLike like) {
        final Component component = like.asComponent();
        if (component == Component.empty()) return this;
        children.add(component);
        return this;
    }

    public QsTextComponentImpl append(final @NotNull QsTextComponentImpl like) {
        children.add(like);
        return this;
    }

    @Override
    public QsTextComponentImpl color(TextColor color) {
        return style(style.color(color));
    }


    @Override
    public QsTextComponentImpl content(final String content) {
        this.content = content;
        return this;
    }

    @Override
    public QsTextComponentImpl children(final List<? extends ComponentLike> children) {
        List<Component> components = new ArrayList<>();
        for (ComponentLike like : children) {
            Component component = like.asComponent();
            components.add(component);
        }
        this.children = components;
        return this;
    }

    @Override
    public QsTextComponentImpl style(final Style style) {
        this.style = style;
        return this;
    }

    @Override
    public boolean equals(final @Nullable Object other) {
        if (this == other) return true;
        if (!(other instanceof QsTextComponentImpl that)) return false;
        if (!super.equals(other)) return false;
        return Objects.equals(this.content, that.content);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = (31 * result) + this.content.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return Internals.toString(this);
    }

    @Override
    public Builder toBuilder() {
        return null;
    }
}
