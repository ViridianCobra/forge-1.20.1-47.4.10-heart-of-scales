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

public class DragonEggBlockEntity extends BlockEntity {
    private DragonGenome genome = DragonGenome.defaultGenome();

    public DragonEggBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRAGON_EGG.get(), pos, state);
    }

    public DragonGenome getGenome() {
        return genome;
    }

    public void setGenome(DragonGenome genome) {
        this.genome = genome;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        GenomeNbt.save(genome, tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        genome = GenomeNbt.load(tag);
        // Block entity data arrives after the chunk has already re-rendered for the block change, so ask for another pass
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
