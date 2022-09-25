package net.enecske.customblock_core.mixin;

import net.enecske.customblock_core.BlockIdentifier;
import net.enecske.customblock_core.CustomBlock;
import net.enecske.customblock_core.NoteblockBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    //@Inject(method = "getBlockBreakingSpeed(Lnet/minecraft/block/BlockState;)F", at = @At("TAIL"), cancellable = true)
    public void getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {

        cir.setReturnValue(calculateBlockBreakingSpeed(block, cir.getReturnValueF()));
    }

    private float calculateBlockBreakingSpeed(BlockState state, float returnValue) {
        CustomBlock block = NoteblockBlockEntity.getBlockType(new BlockIdentifier(state.get(NoteBlock.INSTRUMENT).ordinal(), state.get(NoteBlock.NOTE)));

        if(block == null) return returnValue;
        if(block.getHardness() == -1.0F) return 0F;

        return returnValue / block.getHardness();
    }
}
