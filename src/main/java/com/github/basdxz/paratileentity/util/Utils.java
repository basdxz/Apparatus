package com.github.basdxz.paratileentity.util;

import com.github.basdxz.paratileentity.defenition.managed.IParaBlock;
import com.github.basdxz.paratileentity.defenition.managed.IParaTileEntity;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import ru.timeconqueror.spongemixins.MinecraftURLClassPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import static com.github.basdxz.paratileentity.ParaTileEntityMod.info;

@UtilityClass
public class Utils {
    public boolean isDevelopmentEnvironment() {
        return (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }

    public boolean loadJar(final String jarName) {
        try {
            File jar = MinecraftURLClassPath.getJarInModPath(jarName);
            if (jar == null) {
                info("Jar not found: " + jarName);
                return false;
            }

            info("Attempting to add " + jar + " to the URL Class Path");
            if (!jar.exists())
                throw new FileNotFoundException(jar.toString());
            MinecraftURLClassPath.addJar(jar);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
        Gets a TileEntity from world without creating it if it doesn't exist.
    */
    public Optional<TileEntity> getTileEntityIfExists(World world, int posX, int posY, int posZ) {
        Optional<TileEntity> tileEntity = Optional.empty();
        val chunk = world.getChunkFromBlockCoords(posX, posZ);
        if (chunk != null)
            tileEntity = Optional.ofNullable(chunk.getTileEntityUnsafe(
                    Utils.worldToChunkBlockPosXZ(posX),
                    posY,
                    Utils.worldToChunkBlockPosXZ(posZ)));
        return tileEntity;
    }

    /*
        Converts from world block position to chunk block position for X and Z
    */
    public int worldToChunkBlockPosXZ(final int worldBlockPos) {
        return worldBlockPos & 15;
    }

    /*
        A common redirect used in many of the IParaTile mixins by replacing the block meta with the tileID.
     */
    public int getBlockMetadataRedirect(World instance, int posX, int posY, int posZ) {
        if (instance.getBlock(posX, posY, posZ) instanceof IParaBlock) {
            val tileEntity = instance.getTileEntity(posX, posY, posZ);
            if (tileEntity instanceof IParaTileEntity)
                return ((IParaTileEntity) tileEntity).tileID();
        }
        return instance.getBlockMetadata(posX, posY, posZ);
    }
}
