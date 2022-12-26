package goliath.raidableclaims.player;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import goliath.raidableclaims.RaidableClaims;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


// Heavily copied from Lightman's Currency by Lightman314.
public class PlayerReference {
    public final UUID PLAYER_UUID;
    public String username;

    public PlayerReference(UUID playerUUID, String username) {
        this.PLAYER_UUID = playerUUID;
        this.username = username;
    }
    public MutableComponent getNameComponent(boolean isClient) {
        return new TextComponent(this.getUsername(isClient));
    }
    public String getUsername(boolean isClient) {
        if (isClient) {
            return this.username;
        } else {
            String playerName = getPlayerName(this.PLAYER_UUID);
            if (playerName == null || playerName.isBlank()) {
                return this.username;
            }
            return playerName;
        }
    }
    public static String getPlayerName(UUID playerID) {
        try {
            String name = UsernameCache.getLastKnownUsername(playerID);
            if(name != null) return name;
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if(server != null)
            {
                GameProfile profile = server.getProfileCache().get(playerID).orElse(null);
                if(profile != null) return profile.getName();
            }
        } catch(Throwable t) {
            RaidableClaims.LOGGER.error("Error getting player name.", t);
        }
        return null;
    }
    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        compound.putUUID("id", this.PLAYER_UUID);
        compound.putString("name", this.getUsername(false));
        return compound;
    }
    public static PlayerReference load(CompoundTag compound) {
        try {
            UUID id = compound.getUUID("id");
            String name = compound.getString("name");
            return of(id, name);
        } catch(Exception e) {
            RaidableClaims.LOGGER.error("Error loading PlayerReference from tag.", e);
            return null;
        }
    }

    public static PlayerReference of(UUID playerID, String name) {
        if(playerID == null)
            throw new RuntimeException("Cannot make a PlayerReference from a null player ID!");
        return new PlayerReference(playerID, name);
    }
}
