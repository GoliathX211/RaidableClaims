package goliath.raidableclaims.network;

import goliath.raidableclaims.RaidableClaims;
import goliath.raidableclaims.network.message.MessageRequestNBT;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RaidableClaimsPacketHandler {
    public static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel instance;
    private static int nextId = 0;

    public static void init() {

        instance = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(RaidableClaims.MODID, "network"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .simpleChannel();

        register(MessageRequestNBT.class, MessageRequestNBT::encode, MessageRequestNBT::decode, MessageRequestNBT::handle);

    }
    private static <T> void register(Class<T> clazz, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf,T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        instance.registerMessage(nextId++, clazz, encoder, decoder, handler);
    }

    public static PacketDistributor.PacketTarget getTarget(Player player) {
        if(player instanceof ServerPlayer)
            return getTarget((ServerPlayer)player);
        return null;
    }

    public static PacketDistributor.PacketTarget getTarget(ServerPlayer player) {
        return PacketDistributor.PLAYER.with(() -> player);
    }
}
