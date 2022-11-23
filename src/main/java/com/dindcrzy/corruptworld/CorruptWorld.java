package com.dindcrzy.corruptworld;

import com.dindcrzy.corruptworld.blocks.CBlocks;
import com.dindcrzy.corruptworld.entities.CEntity;
import com.dindcrzy.corruptworld.entities.ExtraDataTracker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.impl.tag.convention.TagRegistration;
import net.minecraft.block.Block;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CorruptWorld implements ModInitializer {
	public static final String MOD_ID = "corruptworld";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	@Override
	public void onInitialize() {
		LOGGER.info("Hello Corrupt world! >:)");
		CBlocks.register();
		CEntity.initialize();
	}
	
	public static Identifier ModId(String s) {
		return new Identifier(MOD_ID, s);
	}

}
