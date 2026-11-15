package com.benbenlaw.rifts.entity;

import com.mojang.serialization.Codec;
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

    public static final Supplier<AttachmentType<Boolean>> RIFT_FLIGHT_GRANTED = ATTACHMENTS.register("rift_flight_granted",
            () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL.fieldOf("granted"))
                    .build());

    public static final Supplier<AttachmentType<GlobalPos>> PENDING_CONVERTER_LINK = ATTACHMENTS.register("pending_converter_link",
            () -> AttachmentType.builder(() -> GlobalPos.of(Level.OVERWORLD, BlockPos.ZERO)).build());

    public static final Supplier<AttachmentType<GlobalPos>> RIFT_ENTRANCE = ATTACHMENTS.register("rift_entrance",
            () -> AttachmentType.builder(() -> GlobalPos.of(Level.OVERWORLD, BlockPos.ZERO))
                    .serialize(GlobalPos.CODEC.fieldOf("pos"))
                    .copyOnDeath()
                    .build());
}
