package com.benbenlaw.rifts.block;

import com.benbenlaw.rifts.Rifts;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class RiftsWoodTypes {

    public static final WoodType RIFT = WoodType.register(new WoodType(Rifts.MOD_ID + ":rift", BlockSetType.OAK));
}
