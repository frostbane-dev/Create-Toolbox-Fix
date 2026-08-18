package com.stackultra.createtoolboxfix.mixin;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ToolboxStackLimitMixin {

    @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
    private void create$toolboxStackLimit(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (isToolbox(self)) {
            boolean empty = isToolboxEmpty(self);
            cir.setReturnValue(empty ? 64 : 1);
        }
    }

    @Inject(method = "isSameItemSameComponents", at = @At("HEAD"), cancellable = true)
    private static void create$ignoreEmptyToolboxComponents(ItemStack self, ItemStack other, CallbackInfoReturnable<Boolean> cir) {
        if (!hasToolboxComponents(self) && !hasToolboxComponents(other)) {
            return; // no intervenir
        }

        if (isToolbox(self) && isToolbox(other)) {
            boolean selfEmpty = isToolboxEmpty(self);
            boolean otherEmpty = isToolboxEmpty(other);

            if (selfEmpty && otherEmpty && self.getItem() == other.getItem()) {
                if (areOtherComponentsEqual(self, other)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    private static boolean hasToolboxComponents(ItemStack stack) {
        return stack.has(AllDataComponents.TOOLBOX_INVENTORY) || stack.has(AllDataComponents.TOOLBOX_UUID);
    }

    private static boolean isToolbox(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ToolboxBlock;
    }

    private static boolean isToolboxEmpty(ItemStack stack) {
        if (stack.has(AllDataComponents.TOOLBOX_INVENTORY)) {
            ToolboxInventory inv = stack.get(AllDataComponents.TOOLBOX_INVENTORY);
            if (inv != null) {
                for (int i = 0; i < inv.getSlots(); i++) {
                    if (!inv.getStackInSlot(i).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean areOtherComponentsEqual(ItemStack a, ItemStack b) {
        ItemStack copyA = a.copy();
        ItemStack copyB = b.copy();
        copyA.remove(AllDataComponents.TOOLBOX_INVENTORY);
        copyA.remove(AllDataComponents.TOOLBOX_UUID);
        copyB.remove(AllDataComponents.TOOLBOX_INVENTORY);
        copyB.remove(AllDataComponents.TOOLBOX_UUID);
        return ItemStack.isSameItemSameComponents(copyA, copyB);
    }
}