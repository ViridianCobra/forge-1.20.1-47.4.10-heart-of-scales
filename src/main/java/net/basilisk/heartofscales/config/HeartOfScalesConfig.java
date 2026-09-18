package net.basilisk.heartofscales.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class HeartOfScalesConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue HATCH_TIME_TICKS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        HATCH_TIME_TICKS = builder
                .comment("How long an egg takes to hatch in a nest, in ticks (20 ticks = 1 second). Default is 8 minutes.")
                .defineInRange("hatchTimeTicks", 9600, 1, Integer.MAX_VALUE);
        SPEC = builder.build();
    }

    private HeartOfScalesConfig() {}
}
