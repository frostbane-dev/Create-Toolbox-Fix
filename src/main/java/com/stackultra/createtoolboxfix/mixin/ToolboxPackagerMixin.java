package com.stackultra.createtoolboxfix.mixin;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({PackagerBlockEntity.class})
public abstract class ToolboxPackagerMixin {

    @Redirect(
            method = "attemptToSend(Ljava/util/List;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    private boolean create$toolboxMatchesForRequest(ItemStack extracted, ItemStack requested) {
        if (isToolbox(extracted) && isToolbox(requested)) {
            boolean requestedEmpty = isToolboxEmpty(requested);
            boolean extractedEmpty = isToolboxEmpty(extracted);

            if (requestedEmpty && extractedEmpty) {
                // Vacías: coinciden por color
                return extracted.getItem() == requested.getItem();
            } else if (!requestedEmpty && !extractedEmpty) {
                // Llenas: coinciden solo si el contenido es exactamente igual
                return areToolboxContentsEqual(extracted, requested);
            }
            return false;
        }
        return ItemStack.isSameItemSameComponents(extracted, requested);
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