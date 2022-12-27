package goliath.raidableclaims.blocks;

import goliath.raidableclaims.RCRegistry;
import goliath.raidableclaims.RaidableClaims;
import goliath.raidableclaims.blocks.entity.custom.ClaimTowerBlockEntity;
import goliath.raidableclaims.client.gui.menu.ClaimTowerMenu;
import goliath.raidableclaims.player.PlayerReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ClaimTowerBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public ClaimTowerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    // Block Methods

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
        if (entity == null) return;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ClaimTowerBlockEntity claimTowerBlockEntity) {
            claimTowerBlockEntity.playerReference = PlayerReference.of(entity);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ClaimTowerBlockEntity blockEntity) {
            // Debug message sent to player
            player.sendMessage(new TextComponent("Owner of Claim Tower Block at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ": " + blockEntity.playerReference.username + " UUID: " + blockEntity.playerReference.PLAYER_UUID), new UUID(0, 0));
            if (!player.getStringUUID().equals(blockEntity.playerReference.PLAYER_UUID.toString())) {
                player.sendMessage(new TextComponent("You are not the owner of this block!"), new UUID(0, 0));
                return InteractionResult.FAIL;
            }
            NetworkHooks.openGui(((ServerPlayer) player), this.getMenuProvider(state, level, pos), pos);
        } else {
            RaidableClaims.LOGGER.error("Container provider is missing");
            return InteractionResult.FAIL;
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ClaimTowerBlockEntity blockEntity) {
            return new SimpleMenuProvider((containerID, inventory, player) -> new ClaimTowerMenu(containerID, inventory, blockEntity), new TextComponent("Claim Tower: " + blockEntity.playerReference.username));
        } else {
            return null;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ClaimTowerBlockEntity) {
                ((ClaimTowerBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
    // Block Entity methods

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ClaimTowerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == RCRegistry.BlockEntityRegistry.CLAIM_TOWER_BLOCK_ENTITY.get() ? ClaimTowerBlockEntity::tick : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
