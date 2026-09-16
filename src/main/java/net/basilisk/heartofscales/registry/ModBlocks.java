package net.basilisk.heartofscales.registry;

import net.basilisk.heartofscales.HeartOfScales;
import net.basilisk.heartofscales.block.AmorberryBushBlock;
import net.basilisk.heartofscales.block.DracipCropBlock;
import net.basilisk.heartofscales.block.DragonEggBlock;
import net.basilisk.heartofscales.block.NestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, HeartOfScales.MOD_ID);

    public static final RegistryObject<Block> DRAGON_EGG = BLOCKS.register("dragon-egg",
            () -> new DragonEggBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(0.5f)
                    .sound(SoundType.GRASS)
                    .noOcclusion()));

    public static final RegistryObject<Block> NEST = BLOCKS.register("nest-block",
            () -> new NestBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(0.5f)
                    .sound(SoundType.DECORATED_POT)
                    .noOcclusion()));

    public static final RegistryObject<Block> DRACIP = BLOCKS.register("dracip",
            () -> new DracipCropBlock(BlockBehaviour.Properties.copy(Blocks.BEETROOTS)));

    public static final RegistryObject<Block> AMORBERRY_BUSH = BLOCKS.register("amorberry-bush",
            () -> new AmorberryBushBlock(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)));

    private ModBlocks() {}
}
