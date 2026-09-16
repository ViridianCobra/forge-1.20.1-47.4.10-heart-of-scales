package net.basilisk.heartofscales.block;

import net.basilisk.heartofscales.block.entity.NestBlockEntity;
import net.basilisk.heartofscales.genome.DragonGenome;
import net.basilisk.heartofscales.item.DragonEggItem;
import net.basilisk.heartofscales.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class NestBlock extends Block implements EntityBlock {
    public static final BooleanProperty HAS_EGG = BooleanProperty.create("has_egg");

    private static final VoxelShape NEST = Block.box(0, 0, 0, 16, 3, 16);
    private static final VoxelShape NEST_WITH_EGG = Shapes.or(NEST, Block.box(4, 3, 4, 12, 15, 12));

    public NestBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(HAS_EGG, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_EGG);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HAS_EGG) ? NEST_WITH_EGG : NEST;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NestBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof NestBlockEntity nest)) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);
        boolean hasEgg = state.getValue(HAS_EGG);

        if (!hasEgg && held.is(ModItems.DRAGON_EGG.get())) {
            // Set on both sides so the client's predicted state renders with the right tint on the first frame
            nest.setEgg(DragonEggItem.genomeOf(held));
            if (!level.isClientSide) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                level.setBlock(pos, state.setValue(HAS_EGG, true), Block.UPDATE_ALL);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (hasEgg && held.isEmpty()) {
            if (!level.isClientSide) {
                DragonGenome egg = nest.getEgg() != null ? nest.getEgg() : DragonGenome.defaultGenome();
                nest.setEgg(null);
                level.setBlock(pos, state.setValue(HAS_EGG, false), Block.UPDATE_ALL);
                ItemStack stack = DragonEggItem.withGenome(egg);
                if (!player.addItem(stack)) {
                    player.drop(stack, false);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }
}
