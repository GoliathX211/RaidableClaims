package goliath.raidableclaims.block.entity;

import com.mojang.logging.LogUtils;
import goliath.raidableclaims.block.ClaimBlock;
import goliath.raidableclaims.network.ClaimBlockSync;
import goliath.raidableclaims.network.RaidableClaimsPacketHandler;
import goliath.raidableclaims.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.List;

public class ClaimBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    public int maxHealth = 1000;
    public int health;


    public ClaimBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.CLAIM_BLOCK_ENTITY.get(), pos, blockState);
        this.health = 1000;
    }

    public ClaimBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        ClaimBlockEntity claimBlockEntity = (ClaimBlockEntity) blockEntity;
        if (!level.isClientSide()) {
            if (level.getGameTime() % 5 == 0) {
                if (claimBlockEntity.health <= 0) {
                    // set the active property to false
                    //claimBlockEntity.level.setBlockAndUpdate(pos, claimBlockEntity.getBlockState().setValue(ClaimBlock.ACTIVE, false));
                    //state.setValue(ClaimBlock.ACTIVE, false);
                    level.setBlockAndUpdate(pos, state.setValue(ClaimBlock.ACTIVE, false));
                }
                if (state.getValue(ClaimBlock.ACTIVE)) {
                    claimBlockEntity.damageClaim();
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("max_health", this.maxHealth);
        nbt.putInt("health", this.health);
        nbt.putBoolean("active", this.getBlockState().getValue(ClaimBlock.ACTIVE));
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.maxHealth = nbt.getInt("max_health");
        this.health = nbt.getInt("health");
        this.getBlockState().setValue(ClaimBlock.ACTIVE, nbt.getBoolean("active"));

    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            /*LOGGER.info("Sending packet");
            RaidableClaimsPacketHandler.INSTANCE.send(
                    PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)),
                    new ClaimBlockSync(this.maxHealth, this.health, this.worldPosition, this.getBlockState().getValue(ClaimBlock.ACTIVE))
            );*/
        }
    }

    private void damageClaim() {
        if (this.health == 0) return;
        BlockPos topCorner = this.worldPosition.offset(5, 5, 5);
        BlockPos bottomCorner = this.worldPosition.offset(-5, -5, -5);
        AABB box = new AABB(topCorner, bottomCorner);

        assert this.level != null; // TODO: REMOVE
        List<Entity> entities = this.level.getEntities(null, box);
        for (Entity entity : entities) {
            if (entity instanceof Player player) {
                player.sendSystemMessage(Component.literal("You are raiding a claim. Health: " + this.health));
                if (this.health - 50 <= 0) {
                    player.sendSystemMessage(Component.literal("Claim destroyed"));
                    this.health = 0;
                } else {
                    this.health -= 50;
                }
            }
        }
    }

    private void notifyPlayers() {
        BlockPos topCorner = this.worldPosition.offset(5, 5, 5);
        BlockPos bottomCorner = this.worldPosition.offset(-5, -5, -5);
        AABB box = new AABB(topCorner, bottomCorner);

        assert this.level != null; // TODO: REMOVE
        List<Entity> entities = this.level.getEntities(null, box);
        for (Entity entity : entities) {
            if (entity instanceof Player player) {
                player.sendSystemMessage(Component.literal("You are in a claim"));
            }
        }
    }

    /*
    @Override
    public Component getDisplayName() {
        return Component.literal("Claim Block");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player pPlayer) {
        return new ClaimBlockMenu(id, inventory, this, this.data);
    }
    */
}
