package me.continent.season;

import org.bukkit.Chunk;

import java.util.Objects;
import java.util.UUID;

public class ChunkCoord {
    private final UUID world;
    private final int x;
    private final int z;

    public ChunkCoord(UUID world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public ChunkCoord(Chunk chunk) {
        this(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
    }

    public UUID getWorld() {
        return world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkCoord other)) return false;
        return x == other.x && z == other.z && world.equals(other.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, z);
    }
}
