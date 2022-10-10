package net.enecske.customblock_core.core;

import net.minecraft.block.BlockState;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

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
        return (CustomBlock[]) registeredBlocks.toArray();
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

    public static CustomBlock getBlockType(@NotNull BlockState state) {
        return getBlockType(new BlockIdentifier(state.get(INSTRUMENT).ordinal(), state.get(NOTE)));
    }

    @SuppressWarnings("ClassCanBeRecord")
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
