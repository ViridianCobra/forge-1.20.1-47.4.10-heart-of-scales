package net.basilisk.heartofscales.entity;

import net.basilisk.heartofscales.block.DragonBeaconBlock;
import net.basilisk.heartofscales.entity.ai.FleeCarelessPlayerGoal;
import net.basilisk.heartofscales.genome.DragonGenome;
import net.basilisk.heartofscales.item.DragonStaffItem;
import net.basilisk.heartofscales.nbt.GenomeNbt;
import net.basilisk.heartofscales.species.DragonSpecies;
import net.basilisk.heartofscales.species.ModRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.Set;

public class DragonEntity extends TamableAnimal implements GeoEntity {
    private static final EntityDataAccessor<String> DATA_SUBSPECIES =
            SynchedEntityData.defineId(DragonEntity.class, EntityDataSerializers.STRING);
    private static final String TAG_GENOME = "Genome";
    private static final String TAG_TAME_PROGRESS = "TameProgress";
    private static final EntityDataAccessor<Byte> DATA_COMMAND =
            SynchedEntityData.defineId(DragonEntity.class, EntityDataSerializers.BYTE);
    private static final String TAG_BEACON = "Beacon";
    private static final String TAG_BEACON_DIMENSION = "BeaconDimension";
    private static final String TAG_COMMAND = "Command";
    // Wandering dragons stay in a 33 x 33 box centred on their home beacon, from 3 below it to 17 above
    private static final int HOME_RANGE_HORIZONTAL = 16;
    private static final int HOME_RANGE_DOWN = 3;
    private static final int HOME_RANGE_UP = 17;
    private static final int HOME_CHECK_INTERVAL = 20;
    private static final int TAME_THRESHOLD = 100;
    private static final int FOOD_TAME_STEP = 15;
    private static final int FAVOURITE_FOOD_TAME_STEP = 40;
    private static final RawAnimation SIT = RawAnimation.begin().thenLoop("misc.sit");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private DragonGenome genome = DragonGenome.defaultGenome();
    private boolean genomeAssigned;
    private int tameProgress;
    @Nullable
    private GlobalPos home;

    public DragonEntity(EntityType<? extends DragonEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_SUBSPECIES, DragonGenome.DEFAULT_SUBSPECIES);
        entityData.define(DATA_COMMAND, (byte) DragonCommand.FOLLOW.ordinal());
    }

    public DragonGenome getGenome() {
        return genome;
    }

    public void setGenome(DragonGenome genome) {
        this.genome = genome;
        this.genomeAssigned = true;
        entityData.set(DATA_SUBSPECIES, genome.subspecies());
    }

    /** Synced to clients, unlike the full genome. */
    public String getSubspecies() {
        return entityData.get(DATA_SUBSPECIES);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (!genomeAssigned) {
            level.registryAccess().registry(ModRegistries.DRAGON_SPECIES)
                    .flatMap(registry -> registry.getRandom(random))
                    .ifPresent(species -> setGenome(new DragonGenome(species.key().location().toString())));
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put(TAG_GENOME, GenomeNbt.save(genome, new CompoundTag()));
        tag.putInt(TAG_TAME_PROGRESS, tameProgress);
        tag.putString(TAG_COMMAND, getCommand().id());
        if (home != null) {
            tag.put(TAG_BEACON, NbtUtils.writeBlockPos(home.pos()));
            tag.putString(TAG_BEACON_DIMENSION, home.dimension().location().toString());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(TAG_GENOME, Tag.TAG_COMPOUND)) {
            setGenome(GenomeNbt.load(tag.getCompound(TAG_GENOME)));
        }
        tameProgress = tag.getInt(TAG_TAME_PROGRESS);
        home = readHome(tag);

        DragonCommand command;
        if (tag.contains(TAG_COMMAND, Tag.TAG_STRING)) {
            command = DragonCommand.byId(tag.getString(TAG_COMMAND));
        } else {
            // Saved before commands existed: keep doing whatever it was doing
            command = isOrderedToSit() ? DragonCommand.SIT : home != null ? DragonCommand.WANDER : DragonCommand.FOLLOW;
        }
        if (command == DragonCommand.WANDER && home == null) command = DragonCommand.FOLLOW;
        entityData.set(DATA_COMMAND, (byte) command.ordinal());
    }

    @Nullable
    private GlobalPos readHome(CompoundTag tag) {
        if (!tag.contains(TAG_BEACON, Tag.TAG_COMPOUND)) return null;
        ResourceLocation dimension = tag.contains(TAG_BEACON_DIMENSION, Tag.TAG_STRING)
                ? ResourceLocation.tryParse(tag.getString(TAG_BEACON_DIMENSION)) : null;
        ResourceKey<Level> dimensionKey = dimension != null ? ResourceKey.create(Registries.DIMENSION, dimension) : level().dimension();
        return GlobalPos.of(dimensionKey, NbtUtils.readBlockPos(tag.getCompound(TAG_BEACON)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(2, new FleeCarelessPlayerGoal(this, 8.0f, 1.2, 1.6));
        goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 1.0));
        goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f, false) {
            @Override
            public boolean canUse() {
                return getCommand() == DragonCommand.FOLLOW && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return getCommand() == DragonCommand.FOLLOW && super.canContinueToUse();
            }
        });
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public DragonCommand getCommand() {
        return DragonCommand.byOrdinal(entityData.get(DATA_COMMAND));
    }

    /** Returns false, changing nothing, if the dragon cannot carry the command out: wandering needs a home in this dimension. */
    public boolean setCommand(DragonCommand command) {
        if (command == DragonCommand.WANDER && !isHomeInThisDimension()) return false;
        entityData.set(DATA_COMMAND, (byte) command.ordinal());
        setOrderedToSit(command == DragonCommand.SIT);
        jumping = false;
        navigation.stop();
        if (isWalkingHome()) DragonHomecoming.track(this);
        return true;
    }

    public boolean hasHome() {
        return home != null;
    }

    /** Lower half of this dragon's home beacon, or null. */
    @Nullable
    public GlobalPos getHome() {
        return home;
    }

    public boolean isHomeInThisDimension() {
        return home != null && home.dimension() == level().dimension();
    }

    /** Gives the dragon a home and sends it there to wander. */
    public void setHome(GlobalPos home) {
        this.home = GlobalPos.of(home.dimension(), home.pos().immutable());
        setCommand(DragonCommand.WANDER);
    }

    public void clearHome() {
        this.home = null;
        if (getCommand() == DragonCommand.WANDER) setCommand(DragonCommand.FOLLOW);
    }

    private boolean isHomeBound() {
        return getCommand() == DragonCommand.WANDER && isHomeInThisDimension();
    }

    /** Told to wander but not back inside its home area yet. */
    public boolean isWalkingHome() {
        return isHomeBound() && !isWithinRestriction();
    }

    /** Skips the rest of the walk. Used by DragonHomecoming once nobody is around to see it. */
    public void teleportHome() {
        if (!isHomeBound() || !(level() instanceof ServerLevel serverLevel)) return;
        BlockPos beacon = home.pos();
        serverLevel.getChunkAt(beacon);
        Vec3 spot = findLandingSpot(serverLevel, beacon);
        teleportTo(serverLevel, spot.x, spot.y, spot.z, Set.of(), getYRot(), getXRot());
        navigation.stop();
        serverLevel.sendParticles(ParticleTypes.POOF, spot.x, spot.y + getBbHeight() / 2, spot.z, 20, 0.5, 0.5, 0.5, 0.02);
    }

    private Vec3 findLandingSpot(ServerLevel serverLevel, BlockPos beacon) {
        for (int radius = 2; radius <= 4; radius++) {
            for (BlockPos pos : BlockPos.betweenClosed(beacon.offset(-radius, -1, -radius), beacon.offset(radius, 2, radius))) {
                boolean onRing = Math.max(Math.abs(pos.getX() - beacon.getX()), Math.abs(pos.getZ() - beacon.getZ())) == radius;
                if (!onRing || !serverLevel.getBlockState(pos.below()).isFaceSturdy(serverLevel, pos.below(), Direction.UP)) continue;
                Vec3 spot = Vec3.atBottomCenterOf(pos);
                if (serverLevel.noCollision(this, getDimensions(getPose()).makeBoundingBox(spot))) return spot;
            }
        }
        return Vec3.atBottomCenterOf(beacon.above(2));
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        // A dragon loaded into an area that is not ticking never gets to run its own check
        if (!level().isClientSide && isWalkingHome()) DragonHomecoming.track(this);
    }

    @Override
    public boolean hasRestriction() {
        return isHomeBound() || super.hasRestriction();
    }

    @Override
    public BlockPos getRestrictCenter() {
        return isHomeBound() ? home.pos() : super.getRestrictCenter();
    }

    // Vanilla only uses this to decide whether the dragon is close enough for the restriction to matter,
    // so it has to reach the corners of the box
    @Override
    public float getRestrictRadius() {
        return isHomeBound() ? HOME_RANGE_HORIZONTAL * 1.5f : super.getRestrictRadius();
    }

    @Override
    public boolean isWithinRestriction(BlockPos pos) {
        if (!isHomeBound()) return super.isWithinRestriction(pos);
        BlockPos beacon = home.pos();
        return Math.abs(pos.getX() - beacon.getX()) <= HOME_RANGE_HORIZONTAL
                && Math.abs(pos.getZ() - beacon.getZ()) <= HOME_RANGE_HORIZONTAL
                && pos.getY() >= beacon.getY() - HOME_RANGE_DOWN
                && pos.getY() <= beacon.getY() + HOME_RANGE_UP;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || home == null || tickCount % HOME_CHECK_INTERVAL != 0) return;
        if (isWalkingHome()) DragonHomecoming.track(this);
        if (isHomeInThisDimension() && level().isLoaded(home.pos())) {
            BlockState state = level().getBlockState(home.pos());
            boolean beaconPresent = state.getBlock() instanceof DragonBeaconBlock
                    && state.getValue(DragonBeaconBlock.HALF) == DoubleBlockHalf.LOWER;
            if (!beaconPresent) clearHome();
        }
    }

    @Override
    protected Component getTypeName() {
        return Component.translatable("subspecies." + getSubspecies().replace(':', '.'));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof DragonStaffItem staff) return staff.useOnDragon(stack, player, this);
        if (!isTame()) {
            Optional<DragonSpecies> species = ModRegistries.species(level().registryAccess(), getSubspecies());
            if (species.isEmpty() || !species.get().isFood(stack)) return super.mobInteract(player, hand);
            if (level().isClientSide) return InteractionResult.CONSUME;

            boolean favourite = species.get().isFavouriteFood(stack);
            usePlayerItem(player, hand, stack);
            tameProgress = Math.min(TAME_THRESHOLD, tameProgress + (favourite ? FAVOURITE_FOOD_TAME_STEP : FOOD_TAME_STEP));
            if (tameProgress >= TAME_THRESHOLD && !ForgeEventFactory.onAnimalTame(this, player)) {
                tame(player);
                navigation.stop();
                level().broadcastEntityEvent(this, EntityEvent.TAMING_SUCCEEDED);
            } else {
                level().broadcastEntityEvent(this, EntityEvent.TAMING_FAILED);
            }
            return InteractionResult.SUCCESS;
        }

        // Tamed dragons are commanded with the staff only
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Movement", 5, state -> {
            if (isInSittingPose()) return state.setAndContinue(SIT);
            return state.setAndContinue(state.isMoving() ? DefaultAnimations.WALK : DefaultAnimations.IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
