package goliath.raidableclaims.blocks;

import goliath.raidableclaims.RCRegistry;
import goliath.raidableclaims.RaidableClaims;
import goliath.raidableclaims.blocks.entity.custom.ClaimTowerBlockEntity;
import goliath.raidableclaims.client.gui.menu.ClaimTowerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

public class ClaimTowerBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public BlockEntity blockEntity;
    public String owner_UUID;
    // TODO Owner is not persistent.
    public ClaimTowerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        this.owner_UUID = "";
    }

    // Block Methods

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
        if (entity == null) return;
        this.owner_UUID = entity.getStringUUID();

    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide()) {
            RaidableClaims.LOGGER.info("Claim Tower was right clicked on Client");
        } else {
            RaidableClaims.LOGGER.info("Claim Tower was right clicked on Server");
        }

        if (!player.getStringUUID().equals(this.owner_UUID)) {
            RaidableClaims.LOGGER.info("Player is not the owner of this claim tower");
            return InteractionResult.FAIL;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!level.isClientSide()) {
            minecraft.gui.getChat().addMessage(new TextComponent("Owner of Claim Tower Block at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ": " + this.owner_UUID));
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof ClaimTowerBlockEntity) {
                NetworkHooks.openGui(((ServerPlayer) player), this.getMenuProvider(state, level, pos), pos);
            } else {
                throw new IllegalStateException("Container provider is missing");
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos)
    {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ClaimTowerBlockEntity) {
            return new SimpleMenuProvider((containerID, inventory, player) -> new ClaimTowerMenu(containerID, inventory, blockEntity), new TextComponent("Claim Tower"));
}       else {
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
        blockEntity = new ClaimTowerBlockEntity(pos, state);
        return blockEntity;
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
