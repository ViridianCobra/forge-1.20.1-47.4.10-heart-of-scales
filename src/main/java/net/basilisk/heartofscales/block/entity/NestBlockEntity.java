package net.basilisk.heartofscales.block.entity;

import net.basilisk.heartofscales.genome.DragonGenome;
import net.basilisk.heartofscales.nbt.GenomeNbt;
import net.basilisk.heartofscales.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class NestBlockEntity extends BlockEntity {
    private static final String TAG_EGG = "Egg";

    @Nullable
    private DragonGenome egg;

    public NestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NEST.get(), pos, state);
    }

    @Nullable
    public DragonGenome getEgg() {
        return egg;
    }

    public void setEgg(@Nullable DragonGenome egg) {
        this.egg = egg;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (egg != null) {
            tag.put(TAG_EGG, GenomeNbt.save(egg, new CompoundTag()));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        egg = tag.contains(TAG_EGG) ? GenomeNbt.load(tag.getCompound(TAG_EGG)) : null;
        if (level != null && level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
