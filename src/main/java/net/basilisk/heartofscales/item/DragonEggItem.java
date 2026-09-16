package net.basilisk.heartofscales.item;

import net.basilisk.heartofscales.genome.DragonGenome;
import net.basilisk.heartofscales.nbt.GenomeNbt;
import net.basilisk.heartofscales.registry.ModBlockEntities;
import net.basilisk.heartofscales.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DragonEggItem extends BlockItem {
    public DragonEggItem(Block block, Properties properties) {
        super(block, properties);
    }

    /** Genome carried by an egg stack, or the default genome if it has none (e.g. given without NBT). */
    public static DragonGenome genomeOf(ItemStack stack) {
        CompoundTag tag = BlockItem.getBlockEntityData(stack);
        return tag == null ? DragonGenome.defaultGenome() : GenomeNbt.load(tag);
    }

    public static ItemStack withGenome(DragonGenome genome) {
        ItemStack stack = new ItemStack(ModItems.DRAGON_EGG.get());
        BlockItem.setBlockEntityData(stack, ModBlockEntities.DRAGON_EGG.get(), GenomeNbt.save(genome, new CompoundTag()));
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        String key = "subspecies." + genomeOf(stack).subspecies().replace(':', '.');
        tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
    }
}
