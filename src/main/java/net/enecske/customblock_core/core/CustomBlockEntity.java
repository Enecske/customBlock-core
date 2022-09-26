package net.enecske.customblock_core.core;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class CustomBlockEntity extends BlockEntity {
    public BlockIdentifier identifier;
    public CustomBlock block;

    public static final BlockEntityType<CustomBlockEntity> TYPE = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            "customblock_core:noteblock_block_entity",
            BlockEntityType.Builder.create(CustomBlockEntity::new, Blocks.NOTE_BLOCK).build(null)
    );

    public CustomBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);

        calculateBlockType(pos, state);
    }

    public final void calculateBlockType(BlockPos pos, BlockState state) {
        identifier = new BlockIdentifier(state.get(NoteBlock.INSTRUMENT).ordinal(), state.get(NoteBlock.NOTE));

        block = getBlockType(identifier);
    }

    public static CustomBlock getBlockType(BlockIdentifier identifier) {
        if(identifier.getNote() == 0 && identifier.getInstrument() == 0)
            return null;
        for (CustomBlock b : CustomBlock.customBlocks) {
            if (b.getIdentifier().getInstrument() == identifier.getInstrument() && b.getIdentifier().getNote() == identifier.getNote())
                return b;
        }
        return null;
    }



    public static void init() {}

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        //block.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        //block.readNbt(nbt);
    }

    public CustomBlock getBlock() {
        return block;
    }

    public BlockIdentifier getIdentifier() {
        return identifier;
    }
}
