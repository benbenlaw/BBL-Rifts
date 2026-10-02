package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.item.RiftsMaterials;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RiftsEquipmentAssetProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;

    public RiftsEquipmentAssetProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> assets = new HashMap<>();
        assets.put(RiftsMaterials.RIFT_STEEL_ASSET, EquipmentClientInfo.builder()
                .addHumanoidLayers(Rifts.identifier("rift_steel"))
                .build());

        return DataProvider.saveAll(cache, EquipmentClientInfo.CODEC, key -> pathProvider.json(key.identifier()), assets);
    }

    @Override
    public String getName() {
        return "Equipment Assets";
    }
}
