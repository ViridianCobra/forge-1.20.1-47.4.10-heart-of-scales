package net.basilisk.heartofscales.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.basilisk.heartofscales.registry.ModStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.List;
import java.util.Optional;

/**
 * A jigsaw structure that starts on a cave floor. Samples the generator's terrain column at the chunk
 * centre, finds solid ground with enough air above it between min_y and max_y, and places the start
 * pool there. Skips the chunk if the column has no cave.
 */
public class CaveNestStructure extends Structure {
    public static final Codec<CaveNestStructure> CODEC = RecordCodecBuilder.<CaveNestStructure>mapCodec(instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
            Codec.INT.fieldOf("min_y").forGetter(s -> s.minY),
            Codec.INT.fieldOf("max_y").forGetter(s -> s.maxY),
            Codec.intRange(1, 64).fieldOf("headroom").forGetter(s -> s.headroom),
            Codec.intRange(1, 128).optionalFieldOf("max_distance_from_center", 16).forGetter(s -> s.maxDistanceFromCenter)
    ).apply(instance, CaveNestStructure::new)).codec();

    private final Holder<StructureTemplatePool> startPool;
    private final int minY;
    private final int maxY;
    private final int headroom;
    private final int maxDistanceFromCenter;

    public CaveNestStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, int minY, int maxY, int headroom, int maxDistanceFromCenter) {
        super(settings);
        this.startPool = startPool;
        this.minY = minY;
        this.maxY = maxY;
        this.headroom = headroom;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int x = context.chunkPos().getMiddleBlockX();
        int z = context.chunkPos().getMiddleBlockZ();
        NoiseColumn column = context.chunkGenerator().getBaseColumn(x, z, context.heightAccessor(), context.randomState());

        List<Integer> floors = CaveFloorFinder.floors(minY, maxY, headroom,
                y -> isSolid(column.getBlock(y)),
                y -> column.getBlock(y).isAir());
        if (floors.isEmpty()) return Optional.empty();

        int floorY = floors.get(context.random().nextInt(floors.size()));
        BlockPos start = new BlockPos(x, floorY + 1, z);
        return JigsawPlacement.addPieces(context, startPool, Optional.empty(), 1, start, false, Optional.empty(), maxDistanceFromCenter);
    }

    private static boolean isSolid(BlockState state) {
        return !state.isAir() && state.getFluidState().isEmpty();
    }

    @Override
    public StructureType<?> type() {
        return ModStructureTypes.CAVE_NEST.get();
    }
}
