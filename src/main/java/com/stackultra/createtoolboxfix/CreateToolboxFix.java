package com.stackultra.createtoolboxfix;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(CreateToolboxFix.MODID)
public class CreateToolboxFix {

    public static final String MODID = "create_toolbox_fix";

    public CreateToolboxFix(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Registro del comportamiento del dispensador para la toolbox
        Item toolboxItem = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("create", "toolbox"));
        if (toolboxItem != null) {
            DispenserBlock.registerBehavior(toolboxItem, new ToolboxDispenseBehavior());
        }
    }
}