package net.basilisk.heartofscales.registry;

import net.basilisk.heartofscales.HeartOfScales;
import net.basilisk.heartofscales.block.AmorberryBushBlock;
import net.basilisk.heartofscales.block.DracipCropBlock;
import net.basilisk.heartofscales.block.DragonBeaconBlock;
import net.basilisk.heartofscales.block.DragonEggBlock;
import net.basilisk.heartofscales.block.MutationMushroomBlock;
import net.basilisk.heartofscales.block.NestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
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

    public static final RegistryObject<Block> MUTATION_MUSHROOM = BLOCKS.register("mutation-mushroom",
            () -> new MutationMushroomBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM)
                    .lightLevel(state -> 7)
                    .emissiveRendering((state, level, pos) -> true)));

    public static final RegistryObject<Block> DRAGON_BEACON = BLOCKS.register("dragon-beacon",
            () -> new DragonBeaconBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.STONE)
                    .pushReaction(PushReaction.BLOCK)
                    .noOcclusion()));

    private ModBlocks() {}
}
