package net.basilisk.heartofscales.genome;

/**
 * Inherited dragon data, shared by eggs and dragons.
 * Traits grow with the genetics system (colours, pattern, size...).
 *
 * @param subspecies id of the subspecies as "namespace:path", matching a dragon_species datapack entry
 */
public record DragonGenome(String subspecies) {
    public static final String DEFAULT_SUBSPECIES = "heart_of_scales:forest";

    public static DragonGenome defaultGenome() {
        return new DragonGenome(DEFAULT_SUBSPECIES);
    }
}
