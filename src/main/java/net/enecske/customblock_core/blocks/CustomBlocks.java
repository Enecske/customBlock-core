package net.enecske.customblock_core.blocks;

import net.enecske.customblock_core.core.CustomBlockRegistry;
import org.slf4j.Logger;

public class CustomBlocks {
    public CustomBlockRegistry.BlockRegistryItem EXAMPLE_BLOCK;

    public void register(Logger logger) {
        EXAMPLE_BLOCK = CustomBlockRegistry.registerBlock(new ExampleBlock(), logger);
    }
}