package goliath.raidableclaims.registry;

import goliath.raidableclaims.RaidableClaims;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RaidableClaims.MODID);
    public static class ModCreativeTab extends CreativeModeTab {
        public static final ModCreativeTab instance = new ModCreativeTab(CreativeModeTab.TABS.length, RaidableClaims.MODID);
        private ModCreativeTab(int index, String label) {
            super(index, label);
        }
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(BlockRegistry.CLAIM_BLOCK.get());
        }

    }
}
