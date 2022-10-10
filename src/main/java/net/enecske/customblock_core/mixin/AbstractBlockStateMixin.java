package net.enecske.customblock_core.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.enecske.customblock_core.core.BlockIdentifier;
import net.enecske.customblock_core.core.CustomBlock;
import net.enecske.customblock_core.core.CustomBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
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

    protected AbstractBlockStateMixin(Block owner, ImmutableMap<Property<?>, Comparable<?>> entries, MapCodec<BlockState> codec) {
        super(owner, entries, codec);
    }

    @Inject(method = "getMaterial", at = @At("HEAD"), cancellable = true)
    public void getCustomMaterial(CallbackInfoReturnable<Material> cir) {
        if (this.getBlock() != Blocks.NOTE_BLOCK) return;

        CustomBlock block = CustomBlockRegistry.getBlockType(new BlockIdentifier(this.get(INSTRUMENT).ordinal(), this.get(NOTE)));
        if (block != null)
            cir.setReturnValue(block.getMaterial());
    }

    @Inject(method = "isIn(Lnet/minecraft/tag/TagKey;)Z", at = @At("TAIL"), cancellable = true)
    public void isIn(TagKey<Block> tag, CallbackInfoReturnable<Boolean> cir) {
        /*
        This feature is still 0.01% done

        When it's done you'll be able to add custom blocks to tags in the datapack files
        Maybe I'll create a separate mod only for this

        So be aware when using this, I cannot guarantee that it'll work as intended every scenario
         */

        if (this.getBlock() == Blocks.NOTE_BLOCK) {
            int note = this.get(NOTE);
            int instrument = this.get(INSTRUMENT).ordinal();

            CustomBlock block = CustomBlockRegistry.getBlockType(new BlockIdentifier(instrument, note));

            if (block != null)
                cir.setReturnValue(block.isPartOfTag(tag));
        }
    }
}
