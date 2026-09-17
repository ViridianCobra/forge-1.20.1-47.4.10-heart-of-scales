package net.basilisk.heartofscales.item;

import net.basilisk.heartofscales.genome.DragonGenome;
import net.basilisk.heartofscales.registry.ModDataComponents;
import net.basilisk.heartofscales.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class DragonEggItem extends BlockItem {
    public DragonEggItem(Block block, Properties properties) {
        super(block, properties);
    }

    /** Genome carried by an egg stack, or the default genome if it has none (e.g. given without components). */
    public static DragonGenome genomeOf(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.GENOME.get(), DragonGenome.defaultGenome());
    }

    public static ItemStack withGenome(DragonGenome genome) {
        ItemStack stack = new ItemStack(ModItems.DRAGON_EGG.get());
        stack.set(ModDataComponents.GENOME.get(), genome);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        String key = "subspecies." + genomeOf(stack).subspecies().replace(':', '.');
        tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
    }
}
