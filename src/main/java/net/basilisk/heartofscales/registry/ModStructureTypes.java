package net.basilisk.heartofscales.registry;

import net.basilisk.heartofscales.HeartOfScales;
import net.basilisk.heartofscales.worldgen.CaveNestStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModStructureTypes {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, HeartOfScales.MOD_ID);

    public static final RegistryObject<StructureType<CaveNestStructure>> CAVE_NEST =
            STRUCTURE_TYPES.register("cave-nest", () -> () -> CaveNestStructure.CODEC);

    private ModStructureTypes() {}
}
