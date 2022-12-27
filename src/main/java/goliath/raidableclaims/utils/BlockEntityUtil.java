package goliath.raidableclaims.utils;

import goliath.raidableclaims.RaidableClaims;
import goliath.raidableclaims.network.RaidableClaimsPacketHandler;
import goliath.raidableclaims.network.message.MessageRequestNBT;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class BlockEntityUtil
{
    /**
     * Sends an update packet to clients tracking a tile entity.
     *
     * @param blockEntity the tile entity to update
     */
    public static void sendUpdatePacket(BlockEntity blockEntity)
    {
        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(blockEntity);
        if(packet != null)
        {
            sendUpdatePacket(blockEntity.getLevel(), blockEntity.getBlockPos(), packet);
        }
        else
        {
            RaidableClaims.LOGGER.error(blockEntity.getClass().getName() + ".getUpdatePacket() returned null!");
        }
    }

    /**
     * Sends an update packet to clients tracking a tile entity with a specific CompoundNBT
     *
     * @param blockEntity the tile entity to update
     */
    public static void sendUpdatePacket(BlockEntity blockEntity, CompoundTag compound)
    {
        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(blockEntity, be -> compound);
        sendUpdatePacket(blockEntity.getLevel(), blockEntity.getBlockPos(), packet);
    }

    private static void sendUpdatePacket(Level world, BlockPos pos, ClientboundBlockEntityDataPacket packet)
    {
        if(world instanceof ServerLevel)
        {
            //CurrencyMod.LOGGER.info("Sending Tile Entity Update Packet to the connected clients.");
            ServerLevel server = (ServerLevel) world;
            List<ServerPlayer> players = server.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false);
            players.forEach(player -> player.connection.send(packet));
        }
        else
        {
            RaidableClaims.LOGGER.error("Cannot send Tile Entity Update Packet from a client.");
        }
    }

    public static void requestUpdatePacket(BlockEntity be) {
        requestUpdatePacket(be.getLevel(), be.getBlockPos());
    }

    public static void requestUpdatePacket(Level level, BlockPos pos)
    {
        if(level.isClientSide)
            RaidableClaimsPacketHandler.instance.sendToServer(new MessageRequestNBT(pos));
    }

}
