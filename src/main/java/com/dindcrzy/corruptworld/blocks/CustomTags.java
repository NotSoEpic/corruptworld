package com.dindcrzy.corruptworld.blocks;

import com.dindcrzy.corruptworld.CorruptWorld;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

public class CustomTags {
    public static final TagKey<Block> THORN_CORES = TagKey.of(Registry.BLOCK_KEY, CorruptWorld.ModId("thorn_cores"));
    public static final TagKey<Block> THORN_REPLACEABLE = TagKey.of(Registry.BLOCK_KEY, CorruptWorld.ModId("thorn_replaceable"));
    public static final TagKey<Block> VINE_REPLACEABLE = TagKey.of(Registry.BLOCK_KEY, CorruptWorld.ModId("vine_replaceable"));
    public static final TagKey<Block> SCUM_REPLACEABLE = TagKey.of(Registry.BLOCK_KEY, CorruptWorld.ModId("scum_replaceable"));
}
