package goliath.raidableclaims;

import com.google.common.base.Supplier;
import goliath.raidableclaims.blocks.ClaimTowerBlock;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static net.minecraft.world.level.block.Block.*;

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

        /*private static <T extends Block> RegistryObject<T> registerBlock(final String name, final Supplier<? extends T> block) {
            return BLOCKS.register(name, block);
        }
        private static <T extends Block> RegistryObject<T> register(final String name, final Supplier<? extends T> block, Function<RegistryObject<T>, Supplier<? extends Item>> item) {
            RegistryObject<T> obj = registerBlock(name, block);
            ITEMS.register(name, item.apply(obj));
            return obj;

        }*/
        /*public static final RegistryObject<Block> CLAIM_TOWER = register("claim_tower", () -> new ClaimTowerBlock(
                Block.Properties.of(Material.HEAVY_METAL, MaterialColor.COLOR_RED)
                        .explosionResistance(100.0f)),
                object -> () -> new BlockItem(object.get(), new Item.Properties().tab(RaidableClaims.RAIDABLE_CLAIMS_TAB))
        );*/


    }
    public static class ItemRegistry {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RaidableClaims.MODID);
        public static class ModCreativeTab extends CreativeModeTab {
            private ModCreativeTab(int index, String label) {
                super(index, label);
            }

            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Items.BELL);
            }
            public static final ModCreativeTab RAIDABLE_CLAIMS_TAB = new ModCreativeTab(CreativeModeTab.TABS.length, "raidableclaims_creative_tab");
        }

        private static <T extends Item> RegistryObject<T> register(final String name, final Supplier<T> item) {
            return ITEMS.register(name, item);
        }
    }
}
