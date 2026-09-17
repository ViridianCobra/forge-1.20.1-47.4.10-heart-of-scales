package net.basilisk.heartofscales.registry;

import net.basilisk.heartofscales.HeartOfScales;
import net.basilisk.heartofscales.block.AmorberryBushBlock;
import net.basilisk.heartofscales.block.DracipCropBlock;
import net.basilisk.heartofscales.block.DragonEggBlock;
import net.basilisk.heartofscales.block.NestBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, HeartOfScales.MOD_ID);

    public static final DeferredHolder<Block, Block> DRAGON_EGG = BLOCKS.register("dragon-egg",
            () -> new DragonEggBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(0.5f)
                    .sound(SoundType.GRASS)
                    .noOcclusion()));

    public static final DeferredHolder<Block, Block> NEST = BLOCKS.register("nest-block",
            () -> new NestBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(0.5f)
                    .sound(SoundType.DECORATED_POT)
                    .noOcclusion()));

    public static final DeferredHolder<Block, Block> DRACIP = BLOCKS.register("dracip",
            () -> new DracipCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEETROOTS)));

    public static final DeferredHolder<Block, Block> AMORBERRY_BUSH = BLOCKS.register("amorberry-bush",
            () -> new AmorberryBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH)));

    private ModBlocks() {}
}
