package net.enecske.customblock_core.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.enecske.customblock_core.CustomBlockCore;
import net.enecske.customblock_core.core.BlockIdentifier;
import net.enecske.customblock_core.core.CustomBlock;
import net.enecske.customblock_core.core.CustomBlockRegistry;
import net.minecraft.block.*;
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
import static net.minecraft.block.NoteBlock.NOTE;

@Mixin(AbstractBlock.AbstractBlockState.class)
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

        CustomBlock block = CustomBlockRegistry.getBlockType(new BlockIdentifier(this.get(INSTRUMENT).ordinal(), this.get(NoteBlock.NOTE)));
        if (block != null)
            cir.setReturnValue(block.getMaterial());
    }
}
