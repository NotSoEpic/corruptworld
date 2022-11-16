package com.dindcrzy.corruptworld;

import com.dindcrzy.corruptworld.blocks.CBlocks;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CorruptWorld implements ModInitializer {
	public static final String MOD_ID = "corruptworld";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	@Override
	public void onInitialize() {
		LOGGER.info("Hello Corrupt world! >:)");
		CBlocks.register();
	}
	
	public static Identifier ModId(String s) {
		return new Identifier(MOD_ID, s);
	}
}
