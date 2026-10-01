package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.block.custom.RiftPipeBlock;
import com.benbenlaw.rifts.item.RiftsItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

import static net.minecraft.client.data.models.BlockModelGenerators.*;

public class RiftsModelProvider extends net.minecraft.client.data.models.ModelProvider {

    private static final ModelTemplate PIPE_CORE = pipeTemplate("template_rift_pipe_core", "_core");
    private static final ModelTemplate PIPE_ARM = pipeTemplate("template_rift_pipe_arm", "_arm");
    private static final ModelTemplate PIPE_INVENTORY = pipeTemplate("template_rift_pipe_inventory", "_inventory");

    public RiftsModelProvider(PackOutput output) {
        super(output, Rifts.MOD_ID);
    }

    private static ModelTemplate pipeTemplate(String parent, String suffix) {
        return new ModelTemplate(Optional.of(Rifts.identifier("block/" + parent)), Optional.of(suffix), TextureSlot.TEXTURE);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        machine(blockModels, RiftsBlocks.RIFT_GENERATOR.get());
        machine(blockModels, RiftsBlocks.RIFT_INFUSER.get());

        blockModels.createTrivialCube(RiftsBlocks.RIFT_STORAGE.get());
        blockModels.createTrivialCube(RiftsBlocks.RIFT_STEEL_BLOCK.get());

        pylon(blockModels, RiftsBlocks.BASIC_RIFT_PYLON.get());
        pylon(blockModels, RiftsBlocks.ADVANCED_RIFT_PYLON.get());
        pylon(blockModels, RiftsBlocks.ELITE_RIFT_PYLON.get());
        pylon(blockModels, RiftsBlocks.ULTIMATE_RIFT_PYLON.get());

        pipe(blockModels, RiftsBlocks.RIFT_PIPE.get());

        itemModels.generateFlatItem(RiftsItems.DISPLACER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_SCANNER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_WRENCH.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_STEEL_INGOT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_STEEL_NUGGET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_ELEMENTAL_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
    }

    private static void machine(BlockModelGenerators blockModels, Block block) {
        MultiVariant model = plainVariant(TexturedModel.ORIENTABLE_ONLY_TOP.create(block, blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, model).with(ROTATION_FACING));
    }

    private static void pylon(BlockModelGenerators blockModels, Block block) {
        blockModels.createTrivialBlock(block, TexturedModel.COLUMN);
    }

    private static void pipe(BlockModelGenerators blockModels, Block block) {
        TextureMapping texture = TextureMapping.defaultTexture(block);
        MultiVariant core = plainVariant(PIPE_CORE.create(block, texture, blockModels.modelOutput));
        MultiVariant arm = plainVariant(PIPE_ARM.create(block, texture, blockModels.modelOutput));
        Identifier inventory = PIPE_INVENTORY.create(block, texture, blockModels.modelOutput);

        blockModels.blockStateOutput.accept(MultiPartGenerator.multiPart(block)
                .with(core)
                .with(condition(RiftPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.NORTH), true), arm)
                .with(condition(RiftPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.EAST), true), arm.with(Y_ROT_90))
                .with(condition(RiftPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.SOUTH), true), arm.with(Y_ROT_180))
                .with(condition(RiftPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.WEST), true), arm.with(Y_ROT_270))
                .with(condition(RiftPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.UP), true), arm.with(X_ROT_270))
                .with(condition(RiftPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.DOWN), true), arm.with(X_ROT_90)));

        blockModels.registerSimpleItemModel(block, inventory);
    }
}
