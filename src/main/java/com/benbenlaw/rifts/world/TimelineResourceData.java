package com.benbenlaw.rifts.world;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TimelineResourceData extends SavedData {

    private static final Codec<TimelineResourceData> CODEC = DepletedResource.CODEC.listOf()
            .fieldOf("depleted")
            .codec()
            .xmap(TimelineResourceData::unpack, TimelineResourceData::pack);

    public static final SavedDataType<TimelineResourceData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("chronopolis", "timeline_resources"),
            TimelineResourceData::new,
            CODEC,
            DataFixTypes.LEVEL
    );

    private final Map<BlockPos, DepletedResource> depleted = new Object2ObjectOpenHashMap<>();

    private TimelineResourceData() {
    }

    private static TimelineResourceData unpack(List<DepletedResource> list) {
        TimelineResourceData result = new TimelineResourceData();
        for (DepletedResource resource : list) {
            result.depleted.put(resource.pos(), resource);
        }
        return result;
    }

    private List<DepletedResource> pack() {
        return List.copyOf(this.depleted.values());
    }

    public boolean isDepleted(BlockPos pos) {
        return depleted.containsKey(pos);
    }

    public DepletedResource get(BlockPos pos) {
        return depleted.get(pos);
    }

    public Collection<DepletedResource> allDepleted() {
        return depleted.values();
    }

    public void markDepleted(BlockPos pos, Identifier resourceType, Identifier depletedInEra) {
        depleted.put(pos, new DepletedResource(pos, resourceType, System.currentTimeMillis(), depletedInEra));
        setDirty();
    }

    public static TimelineResourceData get(ServerLevel anyLevel) {
        ServerLevel anchor = anyLevel.getServer().getLevel(Level.OVERWORLD);
        return anchor.getDataStorage().computeIfAbsent(TYPE);
    }
}