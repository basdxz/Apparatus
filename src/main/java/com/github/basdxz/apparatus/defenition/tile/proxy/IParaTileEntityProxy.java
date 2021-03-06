package com.github.basdxz.apparatus.defenition.tile.proxy;

import net.minecraft.nbt.NBTTagCompound;

/*
   TODO: List all TileEntity functions here.
 */
public interface IParaTileEntityProxy {
    default IParaTileEntityProxy tileEntityProxy() {
        return this;
    }

    boolean canUpdate();

    void updateEntity();

    void writeToNBT(NBTTagCompound nbtTagCompound);

    void readFromNBT(NBTTagCompound nbtTagCompound);
}
