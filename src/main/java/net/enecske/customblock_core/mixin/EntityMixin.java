package net.enecske.customblock_core.mixin;

import net.enecske.customblock_core.core.CustomBlock;
import net.enecske.customblock_core.core.CustomBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, EntityLike, CommandOutput {
    @Shadow public World world;
    @Shadow protected abstract BlockPos getVelocityAffectingPos();

    @Inject(method = "getVelocityMultiplier", at = @At("TAIL"), cancellable = true)
    protected void getVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
        BlockState blockState = this.world.getBlockState(this.getBlockPos());
        float f = blockState.getBlock().getVelocityMultiplier();


        if (!blockState.isOf(Blocks.WATER) && !blockState.isOf(Blocks.BUBBLE_COLUMN)) {
            float velocityMultiplier = this.world.getBlockState(this.getVelocityAffectingPos()).getBlock().getVelocityMultiplier();
            CustomBlock block = CustomBlockRegistry.getBlockType(blockState);
            if (block != null)
                velocityMultiplier = block.getVelocityMultiplier();

            cir.setReturnValue((double) f == 1.0 ? velocityMultiplier : f);
        } else {
            cir.setReturnValue(f);
        }
    }

    @Inject(method = "getJumpVelocityMultiplier", at = @At("TAIL"), cancellable = true)
    protected void getJumpVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
        BlockState state1 = this.world.getBlockState(this.getBlockPos());
        BlockState state2 = this.world.getBlockState(this.getVelocityAffectingPos());

        float f = state1.getBlock().getJumpVelocityMultiplier();
        float g = state2.getBlock().getJumpVelocityMultiplier();

        CustomBlock block1 = CustomBlockRegistry.getBlockType(state1);
        if (block1 != null)
            f = block1.getVelocityMultiplier();

        CustomBlock block2 = CustomBlockRegistry.getBlockType(state2);
        if (block2 != null)
            g = block2.getVelocityMultiplier();

        cir.setReturnValue((double) f == 1.0 ? g : f);
    }
}
