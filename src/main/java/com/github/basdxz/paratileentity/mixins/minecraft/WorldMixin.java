package com.github.basdxz.paratileentity.mixins.minecraft;

import com.github.basdxz.paratileentity.defenition.managed.IParaBlock;
import com.github.basdxz.paratileentity.defenition.managed.IParaTileEntity;
import com.github.basdxz.paratileentity.util.Utils;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.github.basdxz.paratileentity.defenition.IParaTileManager.NULL_TILE_ID;

// Client-Side and Server-Side
@Mixin(World.class)
public class WorldMixin {

    /*
        Buffers a ParaTile a new IParaTileEntity is about to be created.

        A meta 0 will be called on a multi block update much more frequently than someone placing down a null tile.
     */
    @Inject(method = "setBlock(IIILnet/minecraft/block/Block;II)Z",
            at = @At("HEAD"),
            require = 1)
    public void setBlock(int posX, int posY, int posZ, Block block, int meta, int flag,
                         CallbackInfoReturnable<Boolean> cir) {
        if (meta == NULL_TILE_ID || !(block instanceof IParaBlock))
            return;

        val tileEntity = Utils.getTileEntityIfExists(thiz(), posX, posY, posZ);
        if (!tileEntity.isPresent() ||
                !(tileEntity.get() instanceof IParaTileEntity) || ((IParaTileEntity) tileEntity.get()).tileID() != meta)
            ((IParaBlock) block).manager().bufferedTile(thiz(), posX, posY, posZ, meta);
    }

    /*
        Same as using 'this' inside the World class itself.
     */
    private World thiz() {
        return (World) (Object) this;
    }
}