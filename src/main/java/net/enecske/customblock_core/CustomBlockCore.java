package net.enecske.customblock_core;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;

public class CustomBlockCore implements ModInitializer {
	public static final String MODID = "customblock_core";

	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final CustomBlocks BLOCKS = new CustomBlocks();

	@Override
	public void onInitialize() {
		LOGGER.info("CustomBlock-Core is loaded server-side!");

		BLOCKS.register(LOGGER);
	}

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
}
