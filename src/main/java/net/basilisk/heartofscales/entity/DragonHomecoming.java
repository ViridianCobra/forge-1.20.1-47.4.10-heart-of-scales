package net.basilisk.heartofscales.entity;

import net.basilisk.heartofscales.HeartOfScales;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Sends dragons that are walking home the rest of the way once nobody is around to watch.
 * A dragon cannot do this for itself: when its area stops ticking it stops running code, and by the time
 * it is told it is being unloaded it has already been saved. In between it is frozen but still loaded,
 * and that is when this watcher moves it.
 */
@Mod.EventBusSubscriber(modid = HeartOfScales.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DragonHomecoming {
    // Server thread only
    private static final Set<DragonEntity> WALKING_HOME = Collections.newSetFromMap(new WeakHashMap<>());

    public static void track(DragonEntity dragon) {
        if (!dragon.level().isClientSide) {
            WALKING_HOME.add(dragon);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || WALKING_HOME.isEmpty()) return;
        Iterator<DragonEntity> dragons = WALKING_HOME.iterator();
        while (dragons.hasNext()) {
            DragonEntity dragon = dragons.next();
            if (dragon.isRemoved() || !dragon.isWalkingHome() || !(dragon.level() instanceof ServerLevel level)) {
                dragons.remove();
            } else if (!level.isPositionEntityTicking(dragon.blockPosition())) {
                dragons.remove();
                dragon.teleportHome();
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        WALKING_HOME.clear();
    }

    private DragonHomecoming() {}
}
