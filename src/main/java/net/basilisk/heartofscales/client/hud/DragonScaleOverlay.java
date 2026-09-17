package net.basilisk.heartofscales.client.hud;

import net.basilisk.heartofscales.HeartOfScales;
import net.basilisk.heartofscales.block.entity.DragonEggBlockEntity;
import net.basilisk.heartofscales.block.entity.NestBlockEntity;
import net.basilisk.heartofscales.genome.DragonGenome;
import net.basilisk.heartofscales.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

public final class DragonScaleOverlay implements LayeredDraw.Layer {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(HeartOfScales.MOD_ID, "dragon_scale");

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) return;
        if (!hasScaleEquipped(mc)) return;

        DragonGenome genome = lookedAtEgg(mc);
        if (genome == null) return;

        Component name = Component.translatable("subspecies." + genome.subspecies().replace(':', '.'));
        Component label = Component.translatable("hud.heart_of_scales.egg", name).withStyle(ChatFormatting.WHITE);

        int x = graphics.guiWidth() / 2;
        int y = graphics.guiHeight() / 2 + 12;
        graphics.drawCenteredString(mc.font, label, x, y, 0xFFFFFF);
    }

    private static boolean hasScaleEquipped(Minecraft mc) {
        return CuriosApi.getCuriosInventory(mc.player)
                .map(inv -> inv.isEquipped(ModItems.DRAGON_SCALE.get()))
                .orElse(false);
    }

    @Nullable
    private static DragonGenome lookedAtEgg(Minecraft mc) {
        if (!(mc.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) return null;
        BlockEntity be = mc.level.getBlockEntity(hit.getBlockPos());
        if (be instanceof DragonEggBlockEntity egg) return egg.getGenome();
        if (be instanceof NestBlockEntity nest) return nest.getEgg();
        return null;
    }
}
