package com.github.basdxz.apparatus.defenition.tile;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.minecraft.world.World;

import static com.github.basdxz.apparatus.defenition.IParaTileManager.NULL_TILE_ID;

@Getter
@Accessors(fluent = true)
public class BufferedParaTile implements IBufferedParaTile {
    private final World world;
    private final int posX;
    private final int posY;
    private final int posZ;
    private final IParaTile paraTile;

    public BufferedParaTile(@NonNull IParaTile paraTile) {
        World world = null;
        int posX = 0;
        int posY = 0;
        int posZ = 0;

        if (paraTile.cloneable()) {
            try {
                world = paraTile.worldObj();
                posX = paraTile.posX();
                posY = paraTile.posY();
                posZ = paraTile.posZ();
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("Provided ParaTile must exist in-world!");
            }
        }

        this.world = world;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.paraTile = paraTile;
        init();
    }

    public BufferedParaTile(World world, int posX, int posY, int posZ, @NonNull IParaTile paraTile) {
        this.world = world;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.paraTile = paraTile;
        init();
    }

    protected void init() {
        if (paraTile.cloneable() && NULL_TILE_ID.equals(paraTile.tileID()) && world == null)
            throw new IllegalArgumentException("World can only be null on tileID 0 or singletons.");
    }
}
