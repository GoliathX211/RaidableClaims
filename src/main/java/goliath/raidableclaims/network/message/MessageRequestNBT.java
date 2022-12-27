package goliath.raidableclaims.network.message;

import goliath.raidableclaims.utils.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageRequestNBT {
    private final BlockPos pos;

    public MessageRequestNBT(BlockEntity tileEntity) {
        this.pos = tileEntity.getBlockPos();
    }

    public MessageRequestNBT(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(MessageRequestNBT message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos);
    }

    public static MessageRequestNBT decode(FriendlyByteBuf buffer) {
        return new MessageRequestNBT(buffer.readBlockPos());
    }


    public static void handle(MessageRequestNBT message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() ->
        {
            //RaidableClaims.LOGGER.info("NBT Update Request received.");
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                BlockEntity blockEntity = player.level.getBlockEntity(message.pos);
                if (blockEntity != null) {
                    BlockEntityUtil.sendUpdatePacket(blockEntity);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
