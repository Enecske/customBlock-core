package net.enecske.customblock_core;

import net.enecske.customblock_core.blocks.CustomBlocks;
import net.enecske.customblock_core.core.CustomBlockRegistry;
import net.enecske.customblock_core.core.NoteBlockBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

public class CustomBlockCore implements ModInitializer {
	public static final String MODID = "customblock_core";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final CustomBlocks BLOCKS = new CustomBlocks();

	public static final BlockEntityType<NoteBlockBlockEntity> NOTE_BLOCK_BLOCK_ENTITY_TYPE = Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			"customblock_core:noteblock_block_entity",
			BlockEntityType.Builder.create(NoteBlockBlockEntity::new, Blocks.NOTE_BLOCK).build(null)
	);

	@Override
	public void onInitialize() {
		LOGGER.info("CustomBlock-Core is loaded server-side!");

		BLOCKS.register(LOGGER);

		LOGGER.info("Registered blocks: " + Arrays.toString(CustomBlockRegistry.getRegisteredBlocks()));
	}

	// The methods are just for debugging

	public static void debugLog(String str) {
		try {
			File logFile = new File(System.getProperty("user.home") + "\\logs\\debug_log.txt");
			if (!logFile.exists()) logFile.createNewFile();
			FileWriter writer = new FileWriter(logFile, true);

			writer.write(str + "\n");

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void runCommand(World world, String command) {
		MinecraftServer server = world.getServer();
		if (server == null) return;

		server.getCommandManager().executeWithPrefix(server.getCommandSource(), command);
	}
}
