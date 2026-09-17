package net.basilisk.heartofscales;

import net.basilisk.heartofscales.registry.ModBlockEntities;
import net.basilisk.heartofscales.registry.ModBlocks;
import net.basilisk.heartofscales.registry.ModCreativeTabs;
import net.basilisk.heartofscales.registry.ModDataComponents;
import net.basilisk.heartofscales.registry.ModItems;
import net.basilisk.heartofscales.registry.ModLootModifiers;
import net.basilisk.heartofscales.species.ModRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(HeartOfScales.MOD_ID)
public class HeartOfScales {
    public static final String MOD_ID = "heart_of_scales";

    public HeartOfScales(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);
        modEventBus.addListener(ModRegistries::registerDatapackRegistries);
    }
}
