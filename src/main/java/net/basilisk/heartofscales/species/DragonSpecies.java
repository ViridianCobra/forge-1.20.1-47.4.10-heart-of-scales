package net.basilisk.heartofscales.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * One subspecies, loaded from data/<ns>/heart_of_scales/dragon_species/<name>.json.
 * Fields grow as the spec needs them (favourite food, hatch condition, habitat...).
 */
public record DragonSpecies(int eggTint) {
    private static final Codec<Integer> HEX_COLOUR = Codec.STRING.comapFlatMap(
            s -> {
                try {
                    return DataResult.success(Integer.parseInt(s.startsWith("#") ? s.substring(1) : s, 16));
                } catch (NumberFormatException e) {
                    return DataResult.error(() -> "Not a hex colour: " + s);
                }
            },
            i -> String.format("#%06X", i));

    public static final Codec<DragonSpecies> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            HEX_COLOUR.fieldOf("egg_tint").forGetter(DragonSpecies::eggTint)
    ).apply(instance, DragonSpecies::new));
}
