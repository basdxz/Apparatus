package com.github.basdxz.paratileentity.mixins.minecraft;

import com.github.basdxz.paratileentity.defenition.managed.IParaBlock;
import com.github.basdxz.paratileentity.defenition.managed.IParaTileEntity;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static com.github.basdxz.paratileentity.defenition.IParaTileManager.NULL_TILE_ID;

// Client-Side and Server-Side
@Mixin(Chunk.class)
public class ChunkMixin {
    private Block cachedBlock;

    /*
        Caches the block before it is set in chunk, needed since @Redirect can't capture locals.
    */
    @Inject(method = "func_150807_a(IIILnet/minecraft/block/Block;I)Z",
            at = @At("HEAD"),
            require = 1)
    private void setBlock(int posX, int posY, int posZ, Block block, int blockMeta,
                          CallbackInfoReturnable<Boolean> cir) {
        cachedBlock = block;
    }

    /*
        Cancels the setBlock method if it would otherwise set an existing tile to a null tile.

        Only exists to fix multiblock update packets replacing good existing tile with null tiles.
     */
    @Inject(method = "func_150807_a(IIILnet/minecraft/block/Block;I)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getLightOpacity (Lnet/minecraft/world/IBlockAccess;III)I"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            require = 1)
    private void setBlockEarlyCancel(int posX, int posY, int posZ, Block block, int blockMeta,
                                     CallbackInfoReturnable<Boolean> cir, int i1, int j1, Block oldBlock, int oldMeta,
                                     ExtendedBlockStorage extendedblockstorage, boolean flag, int l1, int i2) {
        if (!(block instanceof IParaBlock && blockMeta == NULL_TILE_ID) ||
                !(oldBlock instanceof IParaBlock && oldMeta != NULL_TILE_ID))
            return;

        if (((IParaBlock) block).manager() == ((IParaBlock) oldBlock).manager()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    /*
        Redirects the meta getting of IParaBlock to point at the tileID if the TileEntity is already loaded.

        Since the meta of IParaBlock will always be zero, this allows the setBlock to skip setting block if it is
        the same kind of ParaTile by allowing setBlock to compare the tileID instead.
    */
    @Redirect(method = "func_150807_a(IIILnet/minecraft/block/Block;I)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/Chunk;getBlockMetadata (III)I"),
            require = 1)
    private int getBlockMetadataRedirect(Chunk instance, int posX, int posY, int posZ) {
        if (cachedBlock instanceof IParaBlock) {
            val tileEntity = instance.getTileEntityUnsafe(posX, posY, posZ);
            if (tileEntity instanceof IParaTileEntity)
                return ((IParaTileEntity) tileEntity).tileID();
        }
        return instance.getBlockMetadata(posX, posY, posZ);
    }

    /*
        Redirects the meta setting of blocks if they are IParaBlocks to be 0.

        This is because I want to keep all IParaBlocks at meta 0 when in world to ease debugging.
     */
    @Redirect(method = "func_150807_a(IIILnet/minecraft/block/Block;I)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;setExtBlockMetadata (IIII)V"),
            require = 1)
    private void setExtBlockMetadataRedirect(ExtendedBlockStorage instance, int posX, int posY, int posZ, int blockMeta) {
        instance.setExtBlockMetadata(posX, posY, posZ, (cachedBlock instanceof IParaBlock) ? 0 : blockMeta);
    }
}