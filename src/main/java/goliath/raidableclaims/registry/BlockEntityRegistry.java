package goliath.raidableclaims.registry;

import goliath.raidableclaims.RaidableClaims;
import goliath.raidableclaims.block.entity.ClaimBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RaidableClaims.MODID);
    public static final RegistryObject<BlockEntityType<ClaimBlockEntity>> CLAIM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "claim_block_entity", () -> BlockEntityType.Builder.of(ClaimBlockEntity::new, BlockRegistry.CLAIM_BLOCK.get()).build(null)
    );



    public static void registerAll(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
