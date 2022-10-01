package net.enecske.customblock_core;

import net.enecske.customblock_core.blocks.*;
import net.enecske.customblock_core.core.CustomBlockRegistry;
import org.slf4j.Logger;

public class CustomBlocks {
    public CustomBlockRegistry.BlockRegistryItem GABBRO_BLOCK;

    public void register(Logger logger) {
        GABBRO_BLOCK = CustomBlockRegistry.registerBlock(new GabbroBlock(), logger);
    }
}
