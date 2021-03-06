package com.github.basdxz.apparatus.defenition.tile.handler;

import com.github.basdxz.apparatus.defenition.tile.IParaTile;
import lombok.val;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.common.util.ForgeDirection.*;

// TODO Create our own extended-direction enum for 6 directions plus 4 state rotation
public interface IFacingHandler extends IParaTile {
    //ForgeDirection DEFAULT_INVENTORY_FACING = SOUTH; fixme It's normally south, but with Chisel rendering it's east
    ForgeDirection DEFAULT_INVENTORY_FACING = EAST;
    String FACING_NBT_TAG = "facing";

    ForgeDirection facing();

    IFacingHandler facing(ForgeDirection facing);

    /*
        Can easily be replaced by something like return Stream.of(NORTH, SOUTH, WEST, EAST).collect(Collectors.toSet());
     */
    default Set<ForgeDirection> validFacings() {
        return Stream.of(VALID_DIRECTIONS).collect(Collectors.toSet());
    }

    @Override
    default void onBlockPlacedBy(EntityLivingBase entityLivingBase, ItemStack itemStack) {
        val facing = placedFacing(entityLivingBase);
        if (validFacings().contains(facing))
            facing(placedFacing(entityLivingBase));
    }

    default ForgeDirection placedFacing(EntityLivingBase entityPlayer) {
        if (entityPlayer == null)
            return UNKNOWN;

        val pitch = Math.round(entityPlayer.rotationPitch);
        if (pitch >= 65)
            return UP;
        if (pitch <= -65)
            return DOWN;

        val yawQuadrant = MathHelper.floor_double(entityPlayer.rotationYaw * 4.0F / 360.0F + 0.5D) & 0B11;
        switch (yawQuadrant) {
            case 0:
                return NORTH;
            case 1:
                return EAST;
            case 2:
                return SOUTH;
            case 3:
                return WEST;
            default:
                return UNKNOWN;
        }
    }

    //TODO: Evaluate pattern
    @Override
    default void writeToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setInteger(FACING_NBT_TAG, facing().ordinal());
    }

    //TODO: Evaluate pattern
    @Override
    default void readFromNBT(NBTTagCompound nbtTagCompound) {
        facing(ForgeDirection.getOrientation(nbtTagCompound.getInteger(FACING_NBT_TAG)));
    }
}
