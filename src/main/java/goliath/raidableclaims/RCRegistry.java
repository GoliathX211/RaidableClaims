package goliath.raidableclaims;

import com.google.common.base.Supplier;
import goliath.raidableclaims.blocks.ClaimTowerBlock;
import goliath.raidableclaims.blocks.entity.custom.ClaimTowerBlockEntity;
import goliath.raidableclaims.client.gui.menu.ClaimTowerMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class RCRegistry {
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class BlockRegistry {

        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RaidableClaims.MODID);
        public static final DeferredRegister<Item> ITEMS = ItemRegistry.ITEMS;

        @SubscribeEvent
        public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
            final IForgeRegistry<Item> registry = event.getRegistry();

            BLOCKS.getEntries().stream().forEach((block) -> {
                final Item.Properties properties = new Item.Properties().tab(ItemRegistry.ModCreativeTab.RAIDABLE_CLAIMS_TAB);
                final BlockItem blockItem = new BlockItem(block.get(), properties);
                blockItem.setRegistryName(block.getId());
                registry.register(blockItem);
            });
        }
        public static RegistryObject<Block> CLAIM_TOWER = BLOCKS.register("claim_tower", () ->
                new ClaimTowerBlock(BlockBehaviour.Properties.of(Material.HEAVY_METAL, MaterialColor.COLOR_RED)
                        .explosionResistance(100.0f)
                ));
    }
    public static class ItemRegistry {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RaidableClaims.MODID);
        public static class ModCreativeTab extends CreativeModeTab {
            private ModCreativeTab(int index, String label) {
                super(index, label);
            }

            @Override
            public ItemStack makeIcon() {
                return new ItemStack(BlockRegistry.CLAIM_TOWER.get());
            }
            public static final ModCreativeTab RAIDABLE_CLAIMS_TAB = new ModCreativeTab(CreativeModeTab.TABS.length, "raidableclaims_creative_tab");
        }

        private static <T extends Item> RegistryObject<T> register(final String name, final Supplier<T> item) {
            return ITEMS.register(name, item);
        }
    }
    public static class BlockEntityRegistry {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, RaidableClaims.MODID);

        public static final RegistryObject<BlockEntityType<ClaimTowerBlockEntity>> CLAIM_TOWER_BLOCK_ENTITY =
                BLOCK_ENTITIES.register("claim_tower_block_entity", () ->
                        BlockEntityType.Builder.of(
                                ClaimTowerBlockEntity::new,
                                BlockRegistry.CLAIM_TOWER.get()).build(null)
                );
    }
    public static class MenuRegistry {
        public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, RaidableClaims.MODID);
        public static final RegistryObject<MenuType<ClaimTowerMenu>> CLAIM_TOWER_MENU = registerMenuType(ClaimTowerMenu::new, "claim_tower_menu");

        private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
            return MENUS.register(name, () -> IForgeMenuType.create(factory));
        }
    }
    public static void registerAll(IEventBus eventBus) {
        BlockRegistry.BLOCKS.register(eventBus);
        ItemRegistry.ITEMS.register(eventBus);
        BlockEntityRegistry.BLOCK_ENTITIES.register(eventBus);
        MenuRegistry.MENUS.register(eventBus);
    }
}
