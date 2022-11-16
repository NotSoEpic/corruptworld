package com.dindcrzy.corruptworld.blocks;

import com.dindcrzy.corruptworld.CorruptWorld;
import com.dindcrzy.corruptworld.blocks.purifier.PurifierBlock;
import com.dindcrzy.corruptworld.blocks.purifier.PurifierBlockEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

public class CBlocks {
    public static final PurifierBlock PURIFIER = new PurifierBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final BlockEntityType<PurifierBlockEntity> PURIFIER_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            CorruptWorld.ModId("purifier_block_entity"),
            FabricBlockEntityTypeBuilder.create(PurifierBlockEntity::new, PURIFIER).build()
    );
    
    public static final CorruptVineBlock CORRUPT_VINE = new CorruptVineBlock(
            FabricBlockSettings.of(Material.PLANT)
                    .strength(2.0f)
                    .noCollision()
                    .ticksRandomly()
                    .velocityMultiplier(0.9f)
                    .nonOpaque()
                    .sounds(BlockSoundGroup.VINE));
    
    public static void register() {
        register("purifier", PURIFIER);
        register("corrupt_vine", CORRUPT_VINE);
    }
    
    private static void register(String id, Block block) {
        Registry.register(Registry.BLOCK, CorruptWorld.ModId(id), block);
        Registry.register(Registry.ITEM, CorruptWorld.ModId(id), new BlockItem(block, new FabricItemSettings().group(ItemGroup.MISC)));
    }
}
