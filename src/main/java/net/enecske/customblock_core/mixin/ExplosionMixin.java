package net.enecske.customblock_core.mixin;

import net.enecske.customblock_core.core.CustomBlock;
import net.enecske.customblock_core.core.CustomBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Shadow @Final private World world;

    private BlockState storedState;

    /*
    This class is needed because Minecraft removes the block before calling Block.onDestroyedByExplosion
    These two methods flip the order of World.setBlockState and Block.onDestroyedByExplosion
     */

    @Redirect(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public boolean removeBlockStateSetting(World instance, BlockPos pos, BlockState state, int i) {
        return true;
    }

    @Redirect(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onDestroyedByExplosion(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/explosion/Explosion;)V"))
    public void setBlock(Block instance, World world, BlockPos pos, Explosion explosion) {
        instance.onDestroyedByExplosion(world, pos, explosion);
        this.world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
    }

    @Redirect(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"))
    public Block getBlock(BlockState instance) {
        storedState = instance;
        return instance.getBlock();
    }

    @Redirect(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;shouldDropItemsOnExplosion(Lnet/minecraft/world/explosion/Explosion;)Z"))
    public boolean shouldDropItems(Block instance, Explosion explosion) {
        if (instance == Blocks.NOTE_BLOCK) {
            CustomBlock type = CustomBlockRegistry.getBlockType(storedState);
            return type != null && type.shouldDropItemsOnExplosion(explosion);
        }

        return instance.shouldDropItemsOnExplosion(explosion);
    }
}
