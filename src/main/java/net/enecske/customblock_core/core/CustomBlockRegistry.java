package net.enecske.customblock_core.core;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;

import static net.minecraft.block.NoteBlock.INSTRUMENT;
import static net.minecraft.block.NoteBlock.NOTE;

public final class CustomBlockRegistry {
    private static final ArrayList<BlockRegistryItem> registeredBlocks = new ArrayList<>();

    private CustomBlockRegistry() {}

    public static BlockRegistryItem registerBlock(CustomBlock block, Logger logger) {
        if (getBlockType(block.getIdentifier()) != null || (block.getIdentifier().getNote() == 0 && block.getIdentifier().getInstrument() == 0)) {
            logger.error("Custom block " + getBlockType(block.getIdentifier()) + " with identifier + " + block.getIdentifier() + " has been already registered!");
            return null;
        }

        if (registeredBlocks.size() >= 399)
            logger.warn("Registry space for custom full blocks is already full!");


        logger.info("Registered custom block " + block);

        registeredBlocks.add(new BlockRegistryItem(block));
        return registeredBlocks.get(registeredBlocks.size() - 1);
    }

    public static CustomBlock[] getRegisteredBlocks() {
        CustomBlock[] blocks = new CustomBlock[registeredBlocks.size()];

        for (int i = 0; i < registeredBlocks.size(); i++) {
            blocks[i] = registeredBlocks.get(i) != null ? registeredBlocks.get(i).getBlock() : null;
        }

        return blocks;
    }

    public static CustomBlock getBlockType(BlockIdentifier identifier) {
        return getBlockType(identifier.getInstrument(), identifier.getNote());
    }

    public static CustomBlock getBlockType(int instrument, int note) {
        if(note == 0 && instrument == 0)
            return null;

        for (BlockRegistryItem registryItem : CustomBlockRegistry.registeredBlocks) {
            CustomBlock b = registryItem.getBlock();
            if (b != null && b.getIdentifier().getInstrument() == instrument && b.getIdentifier().getNote() == note)
                return b;
        }
        return null;
    }

    public static CustomBlock getBlockType(@NotNull BlockState state) {
        if (state.getBlock() != Blocks.NOTE_BLOCK)
            return null;

        return getBlockType(state.get(INSTRUMENT).ordinal(), state.get(NOTE));
    }

    public static class BlockRegistryItem {
        public final CustomBlock block;
        public final BlockIdentifier id;

        protected BlockRegistryItem(CustomBlock block) {
            this.block = block;
            this.id = block.getIdentifier();
        }

        public CustomBlock getBlock() {
            return block;
        }
    }
}
