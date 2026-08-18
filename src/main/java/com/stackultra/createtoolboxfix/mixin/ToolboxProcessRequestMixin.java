package com.stackultra.createtoolboxfix.mixin;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin({PackagerLinkBlockEntity.class})
public abstract class ToolboxProcessRequestMixin {

    @Redirect(
            method = "processRequest",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/packager/InventorySummary;getCountOf(Lnet/minecraft/world/item/ItemStack;)I"
            )
    )
    private int create$toolboxStockCount(InventorySummary summary, ItemStack stack) {
        if (isToolbox(stack)) {
            boolean stackEmpty = isToolboxEmpty(stack);
            int total = 0;
            for (List<BigItemStack> list : summary.getItemMap().values()) {
                for (BigItemStack entry : list) {
                    if (isToolbox(entry.stack) && entry.stack.getItem() == stack.getItem()) {
                        boolean entryEmpty = isToolboxEmpty(entry.stack);
                        if (stackEmpty && entryEmpty) {
                            total += entry.count;
                        } else if (!stackEmpty && !entryEmpty && areToolboxContentsEqual(entry.stack, stack)) {
                            total += entry.count;
                        }
                    }
                }
            }
            return total;
        }
        return summary.getCountOf(stack);
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