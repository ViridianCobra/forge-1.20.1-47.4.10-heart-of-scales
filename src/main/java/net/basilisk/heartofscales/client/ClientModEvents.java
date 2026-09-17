package net.basilisk.heartofscales.client;

import net.basilisk.heartofscales.HeartOfScales;
import net.basilisk.heartofscales.block.entity.DragonEggBlockEntity;
import net.basilisk.heartofscales.block.entity.NestBlockEntity;
import net.basilisk.heartofscales.client.hud.DragonScaleOverlay;
import net.basilisk.heartofscales.genome.DragonGenome;
import net.basilisk.heartofscales.item.DragonEggItem;
import net.basilisk.heartofscales.registry.ModBlocks;
import net.basilisk.heartofscales.registry.ModItems;
import net.basilisk.heartofscales.species.ModRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = HeartOfScales.MOD_ID, value = Dist.CLIENT)
public final class ClientModEvents {
    private static final int NO_TINT = 0xFFFFFF;

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (level == null || pos == null) return NO_TINT;
            BlockEntity be = level.getBlockEntity(pos);
            if (be == null || be.getLevel() == null) return NO_TINT;
            DragonGenome egg = null;
            if (be instanceof DragonEggBlockEntity eggBe) egg = eggBe.getGenome();
            if (be instanceof NestBlockEntity nest) egg = nest.getEgg();
            return egg == null ? NO_TINT : ModRegistries.eggTint(be.getLevel().registryAccess(), egg);
        }, ModBlocks.DRAGON_EGG.get(), ModBlocks.NEST.get());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (Minecraft.getInstance().level == null) return NO_TINT;
            return ModRegistries.eggTint(Minecraft.getInstance().level.registryAccess(), DragonEggItem.genomeOf(stack));
        }, ModItems.DRAGON_EGG.get());
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAboveAll(DragonScaleOverlay.ID, new DragonScaleOverlay());
    }

    private ClientModEvents() {}
}
