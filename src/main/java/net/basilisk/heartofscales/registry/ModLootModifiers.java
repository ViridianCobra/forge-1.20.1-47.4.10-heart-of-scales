package net.basilisk.heartofscales.registry;

import com.mojang.serialization.Codec;
import net.basilisk.heartofscales.HeartOfScales;
import net.basilisk.heartofscales.loot.AddItemModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, HeartOfScales.MOD_ID);

    public static final RegistryObject<Codec<AddItemModifier>> ADD_ITEM =
            LOOT_MODIFIERS.register("add-item", () -> AddItemModifier.CODEC);

    private ModLootModifiers() {}
}
