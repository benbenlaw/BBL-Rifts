package com.benbenlaw.rifts.screen;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.screen.crusher.RiftCrusherMenu;
import com.benbenlaw.rifts.screen.furnace.RiftFurnaceMenu;
import com.benbenlaw.rifts.screen.generator.RiftGeneratorMenu;
import com.benbenlaw.rifts.screen.infuser.RiftInfuserMenu;
import com.benbenlaw.rifts.screen.pylon.RiftPylonMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EpochopolisMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, Rifts.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<RiftGeneratorMenu>> RIFT_GENERATOR_MENU = MENUS.register("rift_generator_menu",
            () -> IMenuTypeExtension.create(RiftGeneratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<RiftInfuserMenu>> RIFT_INFUSER_MENU = MENUS.register("rift_infuser_menu",
            () -> IMenuTypeExtension.create(RiftInfuserMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<RiftCrusherMenu>> RIFT_CRUSHER_MENU = MENUS.register("rift_crusher_menu",
            () -> IMenuTypeExtension.create(RiftCrusherMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<RiftFurnaceMenu>> RIFT_FURNACE_MENU = MENUS.register("rift_furnace_menu",
            () -> IMenuTypeExtension.create(RiftFurnaceMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<RiftPylonMenu>> RIFT_PYLON_MENU = MENUS.register("rift_pylon_menu",
            () -> IMenuTypeExtension.create(RiftPylonMenu::new));

}

