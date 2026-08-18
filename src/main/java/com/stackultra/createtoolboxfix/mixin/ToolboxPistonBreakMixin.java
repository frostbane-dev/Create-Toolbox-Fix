package com.stackultra.createtoolboxfix.mixin;

import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class ToolboxPistonBreakMixin {

    @Inject(method = "getPistonPushReaction", at = @At("HEAD"), cancellable = true)
    private void create$breakToolboxLikeShulker(CallbackInfoReturnable<PushReaction> cir) {
        BlockState state = (BlockState) (Object) this;
        if (state.getBlock() instanceof ToolboxBlock) {
            cir.setReturnValue(PushReaction.DESTROY);
        }
    }
}