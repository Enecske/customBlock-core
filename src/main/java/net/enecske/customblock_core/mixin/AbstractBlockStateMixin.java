package net.enecske.customblock_core.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.enecske.customblock_core.core.BlockIdentifier;
import net.enecske.customblock_core.core.CustomBlock;
import net.enecske.customblock_core.core.CustomBlockEntity;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.NoteBlock.INSTRUMENT;

@Mixin(targets = "net.minecraft.block.AbstractBlock.AbstractBlockState")
public abstract class AbstractBlockStateMixin extends State<Block, BlockState> {
    @Shadow public abstract Block getBlock();
    @Shadow @Deprecated public abstract void neighborUpdate(World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify);

    /*
    * This class is only experimental
    * I'm not sure if I'll keep it
    */

    protected AbstractBlockStateMixin(Block owner, ImmutableMap<Property<?>, Comparable<?>> entries, MapCodec<BlockState> codec) {
        super(owner, entries, codec);
    }

    @Inject(method = "getMaterial", at = @At("HEAD"), cancellable = true)
    public void returnCustomMaterial(CallbackInfoReturnable<Material> cir) {
        if (this.getBlock() != Blocks.NOTE_BLOCK) return;

        CustomBlock block = CustomBlockEntity.getBlockType(new BlockIdentifier(this.get(INSTRUMENT).ordinal(), this.get(NoteBlock.NOTE)));
        if (block != null)
            cir.setReturnValue(block.getMaterial());
    }

    @Inject(method = "getHardness", at = @At("TAIL"), cancellable = true)
    public void getCustomHardness(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if(this.getBlock() != Blocks.NOTE_BLOCK) return;



        CustomBlock block = CustomBlockEntity.getBlockType(new BlockIdentifier(this.get(INSTRUMENT).ordinal(), this.get(NoteBlock.NOTE)));
        if (block != null)
            cir.setReturnValue(block.getHardness());
    }
}
