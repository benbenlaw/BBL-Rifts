package com.benbenlaw.rifts.screen;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.screen.charger.RiftChargerMenu;
import com.benbenlaw.rifts.screen.crusher.RiftCrusherMenu;
import com.benbenlaw.rifts.screen.furnace.RiftFurnaceMenu;
import com.benbenlaw.rifts.screen.generator.RiftGeneratorMenu;
import com.benbenlaw.rifts.screen.infuser.RiftInfuserMenu;
import com.benbenlaw.rifts.screen.pylon.RiftPylonMenu;
import com.benbenlaw.rifts.screen.quarry.RiftQuarryMenu;
import com.benbenlaw.rifts.screen.surge.RiftSurgeCoreMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class RiftsMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, Rifts.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<RiftGeneratorMenu>> RIFT_GENERATOR_MENU = MENUS.register("rift_generator_menu",
            () -> IMenuTypeExtension.create(RiftGeneratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<RiftInfuserMenu>> RIFT_INFUSER_MENU = MENUS.register("rift_infuser_menu",
            () -> IMenuTypeExtension.create(RiftInfuserMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<RiftChargerMenu>> RIFT_CHARGER_MENU = MENUS.register("rift_charger_menu",
            () -> IMenuTypeExtension.create(RiftChargerMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<RiftSurgeCoreMenu>> RIFT_SURGE_CORE_MENU = MENUS.register("rift_surge_core_menu",
            () -> IMenuTypeExtension.create(RiftSurgeCoreMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<RiftQuarryMenu>> RIFT_QUARRY_MENU = MENUS.register("rift_quarry_menu",
            () -> IMenuTypeExtension.create(RiftQuarryMenu::new));

    public static final List<DeferredHolder<MenuType<?>, MenuType<RiftFurnaceMenu>>> RIFT_FURNACE_MENUS = new ArrayList<>();
    public static final List<DeferredHolder<MenuType<?>, MenuType<RiftCrusherMenu>>> RIFT_CRUSHER_MENUS = new ArrayList<>();

    static {
        for (int tier = 0; tier < MachineLayouts.TIER_NAMES.length; tier++) {
            int menuTier = tier;
            RIFT_FURNACE_MENUS.add(MENUS.register(MachineLayouts.TIER_NAMES[tier] + "_rift_furnace_menu",
                    () -> IMenuTypeExtension.create((id, inventory, data) -> new RiftFurnaceMenu(menuTier, id, inventory, data))));
            RIFT_CRUSHER_MENUS.add(MENUS.register(MachineLayouts.TIER_NAMES[tier] + "_rift_crusher_menu",
                    () -> IMenuTypeExtension.create((id, inventory, data) -> new RiftCrusherMenu(menuTier, id, inventory, data))));
        }
    }

    public static final DeferredHolder<MenuType<?>, MenuType<RiftPylonMenu>> RIFT_PYLON_MENU = MENUS.register("rift_pylon_menu",
            () -> IMenuTypeExtension.create(RiftPylonMenu::new));

}

