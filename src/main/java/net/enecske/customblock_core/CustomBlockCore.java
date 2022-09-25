package net.enecske.customblock_core;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomBlockCore implements ModInitializer {
	public static final String MODID = "customblock_core";

	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		LOGGER.info("CustomBlock-Core is loaded server-side!");
	}


}
