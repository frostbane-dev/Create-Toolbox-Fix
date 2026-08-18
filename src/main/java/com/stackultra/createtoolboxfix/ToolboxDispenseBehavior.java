package com.stackultra.createtoolboxfix;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.DispenserBlock;

import java.lang.reflect.Method;

public class ToolboxDispenseBehavior implements DispenseItemBehavior {

    private static final Method BLOCK_ITEM_PLACE;

    static {
        try {
            BLOCK_ITEM_PLACE = BlockItem.class.getDeclaredMethod("place", net.minecraft.world.item.context.BlockPlaceContext.class);
            BLOCK_ITEM_PLACE.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No se pudo encontrar el método place en BlockItem", e);
        }
    }

    @Override
    public ItemStack dispense(BlockSource source, ItemStack stack) {
        ServerLevel level = source.level();
        Direction direction = source.state().getValue(DispenserBlock.FACING);
        BlockPos pos = source.pos().relative(direction);

        if (level.isEmptyBlock(pos)) {
            if (stack.getItem() instanceof BlockItem) {
                try {
                    DirectionalPlaceContext context = new DirectionalPlaceContext(
                            level,
                            pos,
                            direction,
                            stack,
                            direction.getOpposite()
                    );

                    InteractionResult result = (InteractionResult) BLOCK_ITEM_PLACE.invoke(stack.getItem(), context);

                    if (result.consumesAction()) {
                        stack.shrink(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return stack;
    }
}