package com.dindcrzy.corruptworld.blocks;

import com.dindcrzy.corruptworld.CorruptWorld;
import com.dindcrzy.corruptworld.blocks.purifier.PurifierBlock;
import com.dindcrzy.corruptworld.blocks.purifier.PurifierBlockEntity;
import com.dindcrzy.corruptworld.chunkcorrpution.CardinalChunk;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.LilyPadItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.ColorResolver;

public class CBlocks {
    public static final PurifierBlock PURIFIER = new PurifierBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final BlockEntityType<PurifierBlockEntity> PURIFIER_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            CorruptWorld.ModId("purifier_block_entity"),
            FabricBlockEntityTypeBuilder.create(PurifierBlockEntity::new, PURIFIER).build()
    );
    
    public static final CorruptVineBlock CORRUPT_VINE = new CorruptVineBlock(
            FabricBlockSettings.of(Material.PLANT)
                    .strength(0.4f)
                    .noCollision()
                    .ticksRandomly()
                    .nonOpaque()
                    .sounds(BlockSoundGroup.VINE));
    public static final CorruptScumBlock CORRUPT_SCUM = new CorruptScumBlock(
            FabricBlockSettings.of(Material.PLANT)
                    .strength(0.25f)
                    .ticksRandomly()
                    .nonOpaque()
                    .sounds(BlockSoundGroup.LILY_PAD)
    );
    public static final ThornBlock THORN = new ThornBlock(
            FabricBlockSettings.of(Material.WOOD)
                    .strength(0.4f)
                    .ticksRandomly()
                    .sounds(BlockSoundGroup.NETHER_STEM)
    );
    public static final ThornCoreBlock THORN_CORE = new ThornCoreBlock(
            FabricBlockSettings.of(Material.WOOD)
                    .strength(1f)
                    .ticksRandomly()
                    .sounds(BlockSoundGroup.NETHER_STEM)
    );
    public static final ThornBlossomBlock THORN_BLOSSOM = new ThornBlossomBlock(
            FabricBlockSettings.of(Material.PLANT)
                    .strength(0.6f)
                    .ticksRandomly()
                    .noCollision()
                    .nonOpaque()
                    .sounds(BlockSoundGroup.NETHER_STEM)
    );
    
    public static void register() {
        register("purifier", PURIFIER);
        register("corrupt_vine", CORRUPT_VINE);
        Registry.register(Registry.BLOCK, CorruptWorld.ModId("corrupt_scum"), CORRUPT_SCUM);
        // LilyPadItem has code for placing on top of water
        Registry.register(Registry.ITEM, CorruptWorld.ModId("corrupt_scum"), new LilyPadItem(CORRUPT_SCUM, new FabricItemSettings().group(ItemGroup.MISC)));
        register("thorn", THORN);
        register("thorn_core", THORN_CORE);
        register("thorn_blossom", THORN_BLOSSOM);
    }
    
    private static void register(String id, Block block) {
        Registry.register(Registry.BLOCK, CorruptWorld.ModId(id), block);
        Registry.register(Registry.ITEM, CorruptWorld.ModId(id), new BlockItem(block, new FabricItemSettings().group(ItemGroup.MISC)));
    }
    
    public static BlockColorProvider corruptBlockProvier = (state, view, pos, tintIndex) -> {
        if (pos != null && CardinalChunk.getCorruption(view, pos.getX(), pos.getZ())) {
            return 0x843BA8;
        }
        // todo: make this gray while keeping blending
        return BiomeColors.getGrassColor(view, pos);
    };
    public static ItemColorProvider corruptItemProvider = (stack, tintIndex) -> 0x843BA8;
    
    public static void clientInitialize() {
        BlockRenderLayerMap.INSTANCE.putBlock(CBlocks.CORRUPT_VINE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(CBlocks.CORRUPT_SCUM, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(CBlocks.THORN_BLOSSOM, RenderLayer.getCutout());
        // @see BlockColorsMixin#appendColours
        // ColorProviderRegistry.BLOCK.register(corruptBlockProvier, CORRUPT_VINE, CORRUPT_SCUM);
        ColorProviderRegistry.ITEM.register(corruptItemProvider, CORRUPT_VINE.asItem(), CORRUPT_SCUM.asItem());
    }
}
