package net.basilisk.heartofscales.registry;

import net.basilisk.heartofscales.HeartOfScales;
import net.basilisk.heartofscales.item.DragonEggItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, HeartOfScales.MOD_ID);

    public static final DeferredHolder<Item, Item> DRAGON_EGG = ITEMS.register("dragon-egg",
            () -> new DragonEggItem(ModBlocks.DRAGON_EGG.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> NEST = ITEMS.register("nest-block",
            () -> new BlockItem(ModBlocks.NEST.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> DRACIP_SEEDS = ITEMS.register("dracip-seeds",
            () -> new ItemNameBlockItem(ModBlocks.DRACIP.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> DRACIP_PETALS = ITEMS.register("dracip-petals",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> DRAGON_SCALE = ITEMS.register("dragon-scale",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> AMORBERRY = ITEMS.register("amorberry",
            () -> new ItemNameBlockItem(ModBlocks.AMORBERRY_BUSH.get(), new Item.Properties()));

    private ModItems() {}
}
