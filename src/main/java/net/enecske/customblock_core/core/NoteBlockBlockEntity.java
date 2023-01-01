package net.enecske.customblock_core.core;

import net.enecske.customblock_core.CustomBlockCore;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class NoteBlockBlockEntity extends BlockEntity {
    private Instrument instrument = Instrument.HARP;
    private int note = 0;

    public NoteBlockBlockEntity(BlockPos pos, BlockState state) {
        super(CustomBlockCore.NOTE_BLOCK_BLOCK_ENTITY_TYPE, pos, state);
    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Note", note);
        nbt.putString("Instrument", instrument.asString());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        note = Math.max(0, Math.min(24, nbt.getInt("Note")));

        try {
            instrument = Instrument.valueOf(nbt.getString("Instrument"));
        } catch (IllegalArgumentException e) {
            LoggerFactory.getLogger(CustomBlockCore.MODID).warn(Arrays.toString(e.getStackTrace()));
        }
    }

    public void cycleNote() {
        note++;
        if(note > 24)
            note = 0;
    }
}
