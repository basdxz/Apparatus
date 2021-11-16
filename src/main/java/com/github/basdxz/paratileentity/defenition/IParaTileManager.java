package com.github.basdxz.paratileentity.defenition;

import com.github.basdxz.paratileentity.defenition.chisel.CarvableHelperExtended;
import com.github.basdxz.paratileentity.defenition.managed.IParaBlock;
import com.github.basdxz.paratileentity.defenition.tile.BufferedParaTile;
import com.github.basdxz.paratileentity.defenition.tile.IBufferedParaTile;
import com.github.basdxz.paratileentity.defenition.tile.IParaTile;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface IParaTileManager {
    String NULL_TILE_ID = "NULL";

    static boolean tileIDInvalid(String id) {
        return id.equals(NULL_TILE_ID);
    }

    String name();

    String modid();

    void postInit();

    CarvableHelperExtended carvingHelper();

    default Block block() {
        return paraBlock().block();
    }

    IParaBlock paraBlock();

    Class<? extends ItemBlock> itemClass();

    TileEntity createNewTileEntity();

    IParaTile registerTile(IParaTile tile);

    IParaTile paraTile(String id);

    Iterable<IParaTile> tileList();

    Iterable<String> allTileIDs();

    IBufferedParaTile nullTile();

    default void bufferedTile(World world, int posX, int posY, int posZ, String tileID) {
        bufferedTile(new BufferedParaTile(world, posX, posY, posZ, paraTile(tileID)));
    }

    default void bufferedTile(IParaTile paraTile) {
        bufferedTile(new BufferedParaTile(paraTile));
    }

    void bufferedTile(IBufferedParaTile bufferedTile);

    IBufferedParaTile bufferedTile();

    boolean bufferedTileNull();
}
