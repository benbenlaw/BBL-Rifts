package com.benbenlaw.rifts.item;

import com.benbenlaw.rifts.Rifts;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EpochopolisItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Rifts.MOD_ID);

    public static final DeferredItem<Item> DISPLACER = ITEMS.registerItem("displacer", DisplacerItem::new);
}
