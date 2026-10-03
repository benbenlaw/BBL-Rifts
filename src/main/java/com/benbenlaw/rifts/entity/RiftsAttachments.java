package com.benbenlaw.rifts.entity;

import com.benbenlaw.rifts.Rifts;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class RiftsAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Rifts.MOD_ID);

    // Where the player entered the rift dimension from; only meaningful when hasData is true
    public static final Supplier<AttachmentType<GlobalPos>> RIFT_ENTRANCE = ATTACHMENTS.register("rift_entrance",
            () -> AttachmentType.builder(() -> GlobalPos.of(Level.OVERWORLD, BlockPos.ZERO))
                    .serialize(GlobalPos.CODEC.fieldOf("pos"))
                    .copyOnDeath()
                    .build());
}
