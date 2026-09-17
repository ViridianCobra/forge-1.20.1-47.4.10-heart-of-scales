package net.basilisk.heartofscales.nbt;

import net.basilisk.heartofscales.genome.DragonGenome;
import net.minecraft.nbt.CompoundTag;

public final class GenomeNbt {
    private static final String TAG_SUBSPECIES = "Subspecies";

    public static CompoundTag save(DragonGenome genome, CompoundTag tag) {
        tag.putString(TAG_SUBSPECIES, genome.subspecies());
        return tag;
    }

    public static DragonGenome load(CompoundTag tag) {
        return tag.contains(TAG_SUBSPECIES)
                ? new DragonGenome(tag.getString(TAG_SUBSPECIES))
                : DragonGenome.defaultGenome();
    }

    public static void remove(CompoundTag tag) {
        tag.remove(TAG_SUBSPECIES);
    }

    private GenomeNbt() {}
}
