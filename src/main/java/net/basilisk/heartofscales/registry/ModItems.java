package net.basilisk.heartofscales.registry;

import net.basilisk.heartofscales.HeartOfScales;
import net.basilisk.heartofscales.item.DragonEggItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, HeartOfScales.MOD_ID);

    public static final RegistryObject<Item> DRAGON_EGG = ITEMS.register("dragon-egg",
            () -> new DragonEggItem(ModBlocks.DRAGON_EGG.get(), new Item.Properties()));

    public static final RegistryObject<Item> NEST = ITEMS.register("nest-block",
            () -> new BlockItem(ModBlocks.NEST.get(), new Item.Properties()));

    private ModItems() {}
}
