package net.basilisk.heartofscales;

import net.basilisk.heartofscales.registry.ModBlockEntities;
import net.basilisk.heartofscales.registry.ModBlocks;
import net.basilisk.heartofscales.registry.ModCreativeTabs;
import net.basilisk.heartofscales.registry.ModItems;
import net.basilisk.heartofscales.registry.ModLootModifiers;
import net.basilisk.heartofscales.registry.ModStructureTypes;
import net.basilisk.heartofscales.species.ModRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(HeartOfScales.MOD_ID)
public class HeartOfScales {
    public static final String MOD_ID = "heart_of_scales";

    public HeartOfScales(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);
        ModStructureTypes.STRUCTURE_TYPES.register(modEventBus);
        modEventBus.addListener(ModRegistries::registerDatapackRegistries);
    }
}
