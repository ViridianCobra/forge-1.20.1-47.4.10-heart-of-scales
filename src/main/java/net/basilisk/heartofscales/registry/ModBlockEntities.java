package net.basilisk.heartofscales.registry;

import net.basilisk.heartofscales.HeartOfScales;
import net.basilisk.heartofscales.block.entity.DragonEggBlockEntity;
import net.basilisk.heartofscales.block.entity.NestBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, HeartOfScales.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DragonEggBlockEntity>> DRAGON_EGG = BLOCK_ENTITIES.register("dragon-egg",
            () -> BlockEntityType.Builder.of(DragonEggBlockEntity::new, ModBlocks.DRAGON_EGG.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NestBlockEntity>> NEST = BLOCK_ENTITIES.register("nest-block",
            () -> BlockEntityType.Builder.of(NestBlockEntity::new, ModBlocks.NEST.get()).build(null));

    private ModBlockEntities() {}
}
