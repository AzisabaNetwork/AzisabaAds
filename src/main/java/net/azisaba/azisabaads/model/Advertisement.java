package net.azisaba.azisabaads.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public final class Advertisement {
    private final UUID id;
    private final UUID owner;
    private final String message;
    private final long expiresAt;
    private int impressions;

    public Advertisement(@NotNull UUID id, @NotNull UUID owner, @NotNull String message, long expiresAt, int impressions) {
        this.id = Objects.requireNonNull(id, "id");
        this.owner = Objects.requireNonNull(owner, "owner");
        this.message = Objects.requireNonNull(message, "message");
        this.expiresAt = expiresAt;
        this.impressions = impressions;
    }

    public Advertisement(@NotNull UUID id, @NotNull UUID owner, @NotNull String message, long expiresAt) {
        this(id, owner, message, expiresAt, 0);
    }

    public @NotNull UUID getId() {
        return id;
    }

    public @NotNull UUID getOwner() {
        return owner;
    }

    public @NotNull String getMessage() {
        return message;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public int getImpressions() {
        return impressions;
    }

    public void setImpressions(int impressions) {
        this.impressions = impressions;
    }
}
