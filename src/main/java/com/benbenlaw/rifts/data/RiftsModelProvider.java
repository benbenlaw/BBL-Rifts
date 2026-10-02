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
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

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
        litMachine(blockModels, RiftsBlocks.RIFT_FURNACE.get());
        litMachine(blockModels, RiftsBlocks.RIFT_CRUSHER.get());
        machine(blockModels, RiftsBlocks.RIFT_CHARGER.get());

        blockModels.createTrivialCube(RiftsBlocks.RIFT_STORAGE.get());
        blockModels.createTrivialCube(RiftsBlocks.CREATIVE_RIFT_STORAGE.get());
        blockModels.createTrivialCube(RiftsBlocks.RIFT_STEEL_BLOCK.get());

        blockModels.woodProvider(RiftsBlocks.RIFT_LOG.get()).log(RiftsBlocks.RIFT_LOG.get());

        blockModels.family(RiftsBlocks.RIFT_PLANKS.get())
                .slab(RiftsBlocks.RIFT_PLANK_SLAB.get())
                .stairs(RiftsBlocks.RIFT_PLANK_STAIRS.get());



        pylon(blockModels, RiftsBlocks.BASIC_RIFT_PYLON.get());
        pylon(blockModels, RiftsBlocks.ADVANCED_RIFT_PYLON.get());
        pylon(blockModels, RiftsBlocks.ELITE_RIFT_PYLON.get());
        pylon(blockModels, RiftsBlocks.ULTIMATE_RIFT_PYLON.get());

        pylon(blockModels, RiftsBlocks.BASIC_TICK_ACCELERATOR.get());
        pylon(blockModels, RiftsBlocks.ADVANCED_TICK_ACCELERATOR.get());
        pylon(blockModels, RiftsBlocks.ELITE_TICK_ACCELERATOR.get());
        pylon(blockModels, RiftsBlocks.ULTIMATE_TICK_ACCELERATOR.get());

        pipe(blockModels, RiftsBlocks.RIFT_PIPE.get());

        itemModels.generateFlatItem(RiftsItems.DISPLACER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_SCANNER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_WRENCH.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_STEEL_INGOT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_STEEL_NUGGET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_STEEL_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_STEEL_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_STEEL_AXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_STEEL_SHOVEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_STEEL_HOE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateSpear(RiftsItems.RIFT_STEEL_SPEAR.get());
        itemModels.generateFlatItem(RiftsItems.RIFT_STEEL_HELMET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_STEEL_CHESTPLATE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_STEEL_LEGGINGS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_STEEL_BOOTS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RiftsItems.RIFT_ELEMENTAL_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
    }

    private static void machine(BlockModelGenerators blockModels, Block block) {
        MultiVariant model = plainVariant(TexturedModel.ORIENTABLE_ONLY_TOP.create(block, blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, model).with(ROTATION_FACING));
    }

    private static void litMachine(BlockModelGenerators blockModels, Block block) {
        MultiVariant off = plainVariant(TexturedModel.ORIENTABLE_ONLY_TOP.create(block, blockModels.modelOutput));
        Material frontOn = TextureMapping.getBlockTexture(block, "_front_on");
        MultiVariant on = plainVariant(TexturedModel.ORIENTABLE_ONLY_TOP.get(block)
                .updateTextures(textures -> textures.put(TextureSlot.FRONT, frontOn))
                .createWithSuffix(block, "_on", blockModels.modelOutput));

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(createBooleanModelDispatch(BlockStateProperties.LIT, on, off))
                .with(ROTATION_FACING));
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
