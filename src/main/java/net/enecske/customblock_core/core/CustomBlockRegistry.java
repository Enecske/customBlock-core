package net.enecske.customblock_core.core;

import net.enecske.customblock_core.CustomBlockCore;
import net.minecraft.block.BlockState;

import java.util.ArrayList;

import org.slf4j.Logger;

import static net.minecraft.block.NoteBlock.INSTRUMENT;
import static net.minecraft.block.NoteBlock.NOTE;

public final class CustomBlockRegistry {
    public static ArrayList<BlockRegistryItem> registeredBlocks = new ArrayList<>();

    public static BlockRegistryItem registerBlock(CustomBlock block, Logger logger) {
        CustomBlockCore.debugLog(block.toString());

        logger.info("Registered custom block " + block);

        registeredBlocks.add(new BlockRegistryItem(block));
        return registeredBlocks.get(registeredBlocks.size() - 1);
    }

    public static CustomBlock getBlockType(BlockIdentifier identifier) {
        if(identifier.getNote() == 0 && identifier.getInstrument() == 0)
            return null;
        for (BlockRegistryItem registryItem : CustomBlockRegistry.registeredBlocks) {
            CustomBlock b = registryItem.getBlock();
            if (b != null && b.getIdentifier().getInstrument() == identifier.getInstrument() && b.getIdentifier().getNote() == identifier.getNote())
                return b;
        }
        return null;
    }

    public static CustomBlock getBlockType(BlockState state) {
        return getBlockType(new BlockIdentifier(state.get(INSTRUMENT).ordinal(), state.get(NOTE)));
    }

    public static class BlockRegistryItem {
        public final CustomBlock block;

        protected BlockRegistryItem(CustomBlock block) {
            this.block = block;
        }

        public CustomBlock getBlock() {
            return block;
        }
    }
}
