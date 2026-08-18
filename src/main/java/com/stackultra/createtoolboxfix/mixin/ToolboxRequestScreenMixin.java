package com.stackultra.createtoolboxfix.mixin;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestScreen;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin({StockKeeperRequestScreen.class})
public abstract class ToolboxRequestScreenMixin {

    @Shadow
    private List<BigItemStack> itemsToOrder;

    @Overwrite
    private void revalidateOrders() {
        // No hacemos nada: dejamos que el servidor valide y extraiga.
    }

    @Overwrite
    private BigItemStack getOrderForItem(ItemStack stack) {
        for (BigItemStack entry : this.itemsToOrder) {
            if (areSameForOrder(entry.stack, stack)) {
                return entry;
            }
        }
        return null;
    }

    private static boolean areSameForOrder(ItemStack a, ItemStack b) {
        if (isToolbox(a) && isToolbox(b)) {
            boolean aEmpty = isToolboxEmpty(a);
            boolean bEmpty = isToolboxEmpty(b);

            if (aEmpty && bEmpty) {
                // Vacías: agrupar por color
                return a.getItem() == b.getItem();
            } else if (!aEmpty && !bEmpty) {
                // Llenas: comparar contenido exacto ignorando UUID
                return areToolboxContentsEqual(a, b);
            }
            return false;
        }
        return ItemStack.isSameItemSameComponents(a, b);
    }

    private static boolean areToolboxContentsEqual(ItemStack a, ItemStack b) {
        ToolboxInventory invA = a.getOrDefault(AllDataComponents.TOOLBOX_INVENTORY, null);
        ToolboxInventory invB = b.getOrDefault(AllDataComponents.TOOLBOX_INVENTORY, null);
        if (invA == null || invB == null) return false;
        if (invA.getSlots() != invB.getSlots()) return false;
        for (int i = 0; i < invA.getSlots(); i++) {
            ItemStack stackA = invA.getStackInSlot(i);
            ItemStack stackB = invB.getStackInSlot(i);
            if (!ItemStack.isSameItemSameComponents(stackA, stackB)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isToolbox(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ToolboxBlock;
    }

    private static boolean isToolboxEmpty(ItemStack stack) {
        ToolboxInventory inv;
        if (stack.has(AllDataComponents.TOOLBOX_INVENTORY) && (inv = (ToolboxInventory) stack.get(AllDataComponents.TOOLBOX_INVENTORY)) != null) {
            for (int i = 0; i < inv.getSlots(); i++) {
                if (!inv.getStackInSlot(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }
}