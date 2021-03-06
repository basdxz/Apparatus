package com.github.basdxz.apparatus.defenition.managed;

import com.github.basdxz.apparatus.defenition.tile.IParaTile;
import com.github.basdxz.apparatus.util.ExceptionUtil;
import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;

/*
    TODO: Setup and configure a TileEntitySpecialRenderer dedicated for rendering custom stuff on frame.
 */
@Accessors(fluent = true)
public abstract class ParaTileEntityBase extends TileEntity implements IParaTileEntity {
    @Getter
    protected IParaTile paraTile;
    @Setter
    protected String expectedTileID;

    public ParaTileEntityBase() {
        blockMetadata = getBlockMetadata();
        blockType = getBlockType();
        paraTile(manager().nullTile().paraTile());
    }

    @Override
    public IParaTileEntity registerTileEntity(String modid, String name) {
        GameRegistry.registerTileEntity(getClass(), modid + ":" + name + "_" + TILE_ENTITY_ID_POSTFIX);
        return this;
    }

    @Override
    public TileEntity tileEntity() {
        return this;
    }

    @Override
    public TileEntity newTileEntity() {
        try {
            return getClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalStateException("Class extending ParaTileEntityBase requires no args constructor.");
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
            throw new IllegalStateException("Something went wrong with creating a new instance, refer to stacktrace.");
        }
    }

    @Override
    public IParaTileEntity world(World worldObj) {
        this.worldObj = worldObj;
        return this;
    }

    @Override
    public World world() {
        return worldObj;
    }

    @Override
    public IParaTileEntity posX(int posX) {
        xCoord = posX;
        return this;
    }

    @Override
    public int posX() {
        return xCoord;
    }

    @Override
    public IParaTileEntity posY(int posY) {
        yCoord = posY;
        return this;
    }

    @Override
    public int posY() {
        return yCoord;
    }

    @Override
    public IParaTileEntity posZ(int posZ) {
        zCoord = posZ;
        return this;
    }

    @Override
    public int posZ() {
        return zCoord;
    }

    @Override
    public boolean canUpdate() {
        return proxiedTileEntity().canUpdate();
    }

    @Override
    public void updateEntity() {
        try {
            validateParaTile();
            if (!paraTile().canUpdate()) {
                reloadTileEntity();
            } else {
                proxiedTileEntity().updateEntity();
            }
        } catch (Exception exception) {
            ExceptionUtil.reportTileEntityUpdateException(paraTile(), posX(), posY(), posZ(), exception);
        }
    }

    public void reloadTileEntity() {
        val newParaTileEntity = (IParaTileEntity) newTileEntity();

        newParaTileEntity.posX(posX());
        newParaTileEntity.posY(posY());
        newParaTileEntity.posZ(posZ());
        newParaTileEntity.world(world());

        newParaTileEntity.paraTile(paraTile()).reloadParaTile();

        val nbtTag = new NBTTagCompound();
        writeToNBT(nbtTag);
        newParaTileEntity.tileEntity().readFromNBT(nbtTag);

        world().removeTileEntity(posX(), posY(), posZ());
        world().setTileEntity(posX(), posY(), posZ(), newParaTileEntity.tileEntity());
        newParaTileEntity.tileEntity().markDirty();
        world().markBlockForUpdate(posX(), posY(), posZ());
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        try {
            super.writeToNBT(nbtTagCompound);
            writeTileIDToNBT(nbtTagCompound);
            proxiedTileEntity().writeToNBT(nbtTagCompound);
        } catch (Exception exception) {
            ExceptionUtil.reportNBTWriteException(paraTile(), posX(), posY(), posZ(), nbtTagCompound, exception);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        try {
            super.readFromNBT(nbtTagCompound);
            readTileIDFromNBT(nbtTagCompound);
            validateParaTile();
            proxiedTileEntity().readFromNBT(nbtTagCompound);
        } catch (Exception exception) {
            ExceptionUtil.reportNBTReadException(paraTile(), posX(), posY(), posZ(), nbtTagCompound, exception);
        }
    }

    public void loadParaTile(IParaTile paraTile) {
        paraTile(paraTile).reloadParaTile();
    }

    protected void validateParaTile() {
        if (isParaTileInvalid())
            reloadParaTile();
    }

    protected boolean isParaTileInvalid() {
        return !paraTile().tileID().equals(expectedTileID);
    }

    @Override
    public void reloadParaTile() {
        paraTile(safeClone(manager().paraTile(expectedTileID)));
    }

    @Override
    public IParaTileEntity paraTile(IParaTile paraTile) {
        this.paraTile = paraTile;
        expectedTileID = paraTile.tileID();
        return this;
    }

    /*
        Only clones non-singleton ParaTiles.

        Also allows the clone() method to have access to this TileEntity as well as the cloned class
        But clears reference to **this** TileEntity, preventing unexpected use.
    */
    protected IParaTile safeClone(IParaTile paraTile) {
        if (!paraTile.cloneable())
            return paraTile;

        paraTile.tileEntity(this);
        val clonedParaTile = paraTile.clone();
        paraTile.tileEntity(null);
        return clonedParaTile;
    }

    @Override
    public Packet getDescriptionPacket() {
        val nbtTag = new NBTTagCompound();
        writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, DEFAULT_TILE_ENTITY_PACKET_FLAG, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager networkManager, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
    }

    @Override
    public void updateContainingBlockInfo() {
    }

    @Override
    public int getBlockMetadata() {
        return 0;
    }

    @Override
    public Block getBlockType() {
        return manager().block();
    }
}
