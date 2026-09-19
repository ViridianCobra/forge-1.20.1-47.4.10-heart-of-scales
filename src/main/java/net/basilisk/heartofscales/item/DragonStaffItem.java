package net.basilisk.heartofscales.item;

import net.basilisk.heartofscales.block.DragonBeaconBlock;
import net.basilisk.heartofscales.entity.DragonCommand;
import net.basilisk.heartofscales.entity.DragonEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The only way to command tamed dragons. Crouch-right-click cycles the mode; right-clicking a dragon
 * applies it. In assign mode, click a dragon and then a beacon to give it a home; unassign mode takes it away.
 */
public class DragonStaffItem extends Item {
    private static final String TAG_SELECTED = "SelectedDragon";
    private static final String TAG_MODE = "Mode";

    public enum Mode {
        FOLLOW("follow", DragonCommand.FOLLOW),
        SIT("sit", DragonCommand.SIT),
        WANDER("wander", DragonCommand.WANDER),
        ASSIGN("assign", null),
        UNASSIGN("unassign", null);

        private final String id;
        @Nullable
        private final DragonCommand command;

        Mode(String id, @Nullable DragonCommand command) {
            this.id = id;
            this.command = command;
        }

        Component displayName() {
            return Component.translatable("staff.heart_of_scales.mode." + id);
        }

        Mode next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    public DragonStaffItem(Properties properties) {
        super(properties);
    }

    public static Mode getMode(ItemStack staff) {
        CompoundTag tag = staff.getTag();
        if (tag != null) {
            for (Mode mode : Mode.values()) {
                if (mode.id.equals(tag.getString(TAG_MODE))) return mode;
            }
        }
        return Mode.FOLLOW;
    }

    /** Called from DragonEntity.mobInteract, ahead of the dragon's own click handling. */
    public InteractionResult useOnDragon(ItemStack staff, Player player, DragonEntity dragon) {
        if (dragon.level().isClientSide) return InteractionResult.SUCCESS;

        Mode mode = getMode(staff);
        if (!dragon.isTame() || !dragon.isOwnedBy(player)) {
            message(player, "staff.heart_of_scales.not_yours");
        } else if (mode == Mode.ASSIGN) {
            staff.getOrCreateTag().putUUID(TAG_SELECTED, dragon.getUUID());
            message(player, "staff.heart_of_scales.selected", dragon.getDisplayName());
        } else if (mode == Mode.UNASSIGN) {
            boolean hadHome = dragon.hasHome();
            dragon.clearHome();
            message(player, hadHome ? "staff.heart_of_scales.unassigned" : "staff.heart_of_scales.no_home_to_remove", dragon.getDisplayName());
        } else if (mode == Mode.WANDER && !dragon.hasHome()) {
            message(player, "staff.heart_of_scales.no_home", dragon.getDisplayName());
        } else if (mode == Mode.WANDER && !dragon.isHomeInThisDimension()) {
            message(player, "staff.heart_of_scales.home_elsewhere", dragon.getDisplayName());
        } else if (dragon.setCommand(mode.command)) {
            message(player, "staff.heart_of_scales.commanded." + mode.id, dragon.getDisplayName());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        if (!(state.getBlock() instanceof DragonBeaconBlock) || player == null) return InteractionResult.PASS;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.SUCCESS;

        ItemStack staff = context.getItemInHand();
        if (getMode(staff) != Mode.ASSIGN) {
            message(player, "staff.heart_of_scales.needs_assign_mode");
        } else if (!hasSelection(staff)) {
            message(player, "staff.heart_of_scales.none_selected");
        } else if (!(serverLevel.getEntity(staff.getTag().getUUID(TAG_SELECTED)) instanceof DragonEntity dragon) || !dragon.isAlive()) {
            message(player, "staff.heart_of_scales.not_found");
        } else if (!dragon.isOwnedBy(player)) {
            message(player, "staff.heart_of_scales.not_yours");
        } else {
            dragon.setHome(GlobalPos.of(level.dimension(), DragonBeaconBlock.lowerPos(state, pos)));
            clearSelection(staff);
            message(player, "staff.heart_of_scales.assigned", dragon.getDisplayName());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack staff = player.getItemInHand(hand);
        if (!player.isSecondaryUseActive()) return InteractionResultHolder.pass(staff);
        if (!level.isClientSide) {
            Mode mode = getMode(staff).next();
            staff.getOrCreateTag().putString(TAG_MODE, mode.id);
            clearSelection(staff);
            message(player, "staff.heart_of_scales.mode", mode.displayName());
        }
        return InteractionResultHolder.sidedSuccess(staff, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack staff, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("staff.heart_of_scales.mode", getMode(staff).displayName()).withStyle(ChatFormatting.GRAY));
        if (hasSelection(staff)) {
            tooltip.add(Component.translatable("staff.heart_of_scales.tooltip.selected").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasSelection(stack);
    }

    private static boolean hasSelection(ItemStack staff) {
        CompoundTag tag = staff.getTag();
        return tag != null && tag.hasUUID(TAG_SELECTED);
    }

    private static void clearSelection(ItemStack staff) {
        staff.removeTagKey(TAG_SELECTED);
    }

    private static void message(Player player, String key, Object... args) {
        player.displayClientMessage(Component.translatable(key, args), true);
    }
}
