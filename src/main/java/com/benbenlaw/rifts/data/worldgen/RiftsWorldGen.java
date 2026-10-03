package com.benbenlaw.rifts.data.worldgen;

import com.benbenlaw.rifts.Rifts;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Musics;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TimelineTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import com.benbenlaw.rifts.block.RiftsBlocks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.timeline.Timeline;

import java.util.List;
import java.util.Optional;

public class RiftsWorldGen {

    private static final double ISLAND_THRESHOLD = 0.2;
    private static final double THICKNESS_SCALE = 45.0;
    private static final double TOP_SLOPE = 6.0;
    private static final double BOTTOM_SLOPE = 1.0;
    private static final double BASE_Y = 110.0;
    private static final double HEIGHT_VARIATION = 40.0;
    private static final double DETAIL_STRENGTH = 4.0;

    public static final ResourceKey<NormalNoise.NoiseParameters> ISLAND_MASK_NOISE = noiseKey("island_mask");
    public static final ResourceKey<NormalNoise.NoiseParameters> ISLAND_HEIGHT_NOISE = noiseKey("island_height");
    public static final ResourceKey<NormalNoise.NoiseParameters> ISLAND_DETAIL_NOISE = noiseKey("island_detail");

    public static final ResourceKey<DimensionType> RIFT_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, Rifts.identifier("rift"));
    public static final ResourceKey<Biome> RIFT_BIOME = ResourceKey.create(Registries.BIOME, Rifts.identifier("rift_wastes"));
    public static final ResourceKey<LevelStem> RIFT_STEM = ResourceKey.create(Registries.LEVEL_STEM, Rifts.identifier("rift"));
    public static final ResourceKey<NoiseGeneratorSettings> RIFT_NOISE_SETTINGS = ResourceKey.create(Registries.NOISE_SETTINGS, Rifts.identifier("rift_islands"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> RIFT_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Rifts.identifier("rift_tree"));
    public static final ResourceKey<PlacedFeature> RIFT_TREES = ResourceKey.create(Registries.PLACED_FEATURE, Rifts.identifier("rift_trees"));
    public static final ResourceKey<Level> RIFT_LEVEL = ResourceKey.create(Registries.DIMENSION, Rifts.identifier("rift"));

    private static ResourceKey<NormalNoise.NoiseParameters> noiseKey(String name) {
        return ResourceKey.create(Registries.NOISE, Rifts.identifier(name));
    }

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.NOISE, context -> {
                context.register(ISLAND_MASK_NOISE, new NormalNoise.NoiseParameters(-6, List.of(1.0, 1.0, 0.5)));
                context.register(ISLAND_HEIGHT_NOISE, new NormalNoise.NoiseParameters(-5, List.of(1.0, 0.5)));
                context.register(ISLAND_DETAIL_NOISE, new NormalNoise.NoiseParameters(-3, List.of(1.0, 0.5)));
            })
            .add(Registries.NOISE_SETTINGS, context -> {
                HolderGetter<NormalNoise.NoiseParameters> noises = context.lookup(Registries.NOISE);
                NoiseGeneratorSettings base = NoiseGeneratorSettings.floatingIslands(context);

                DensityFunction zero = DensityFunctions.zero();
                NoiseRouter router = new NoiseRouter(
                        zero, zero, zero, zero, zero, zero, zero, zero, zero, zero, zero,
                        islandDensity(noises),
                        zero, zero, zero);

                context.register(RIFT_NOISE_SETTINGS, new NoiseGeneratorSettings(
                        base.noiseSettings(),
                        RiftsBlocks.RIFT_STONE.get().defaultBlockState(),
                        Blocks.AIR.defaultBlockState(),
                        router,
                        SurfaceRules.state(RiftsBlocks.RIFT_STONE.get().defaultBlockState()),
                        base.spawnTarget(),
                        0,
                        true,
                        false,
                        false,
                        base.useLegacyRandomSource()));
            })
            .add(Registries.DIMENSION_TYPE, context -> {
                HolderGetter<Timeline> timelines = context.lookup(Registries.TIMELINE);
                HolderGetter<WorldClock> clocks = context.lookup(Registries.WORLD_CLOCK);

                // Based on the End: End skybox and a fixed time, but dark, with a little ambient light and no dragon fight
                context.register(RIFT_DIMENSION_TYPE, new DimensionType(
                        true,
                        true,
                        false,
                        false,
                        1.0,
                        0,
                        256,
                        256,
                        BlockTags.INFINIBURN_END,
                        0.1F,
                        new DimensionType.MonsterSettings(ConstantInt.of(15), 0),
                        DimensionType.Skybox.END,
                        CardinalLighting.Type.DEFAULT,
                        EnvironmentAttributeMap.builder()
                                .set(EnvironmentAttributes.FOG_COLOR, 0xFF0A0612)
                                .set(EnvironmentAttributes.SKY_LIGHT_COLOR, 0xFF3A2A5C)
                                .set(EnvironmentAttributes.SKY_COLOR, 0xFF000000)
                                .set(EnvironmentAttributes.SKY_LIGHT_FACTOR, 0.0F)
                                .set(EnvironmentAttributes.AMBIENT_LIGHT_COLOR, 0xFF4A3A78)
                                .set(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(Musics.END))
                                .set(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                                .set(EnvironmentAttributes.BED_RULE, BedRule.EXPLODES)
                                .set(EnvironmentAttributes.RESPAWN_ANCHOR_WORKS, false)
                                .build(),
                        timelines.getOrThrow(TimelineTags.IN_END),
                        Optional.of(clocks.getOrThrow(WorldClocks.THE_END))
                ));
            })
            .add(Registries.CONFIGURED_FEATURE, context -> FeatureUtils.register(context, RIFT_TREE, Feature.TREE,
                    new TreeConfiguration.TreeConfigurationBuilder(
                            BlockStateProvider.simple(RiftsBlocks.RIFT_LOG.get()),
                            new StraightTrunkPlacer(5, 2, 1),
                            BlockStateProvider.simple(RiftsBlocks.RIFT_LEAVES.get()),
                            new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                            new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build()))
            .add(Registries.PLACED_FEATURE, context -> {
                HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(Registries.CONFIGURED_FEATURE);

                // Pick a random point, find the floor below it, and plant a tree only if that floor is rift stone
                PlacementUtils.register(context, RIFT_TREES, features.getOrThrow(RIFT_TREE),
                        CountPlacement.of(12),
                        InSquarePlacement.spread(),
                        PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                        EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12),
                        RandomOffsetPlacement.vertical(ConstantInt.of(1)),
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), RiftsBlocks.RIFT_STONE.get())),
                        BiomeFilter.biome());
            })
            .add(Registries.BIOME, context -> {
                HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
                HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);

                context.register(RIFT_BIOME, new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.5F)
                        .downfall(0.5F)
                        .setAttribute(EnvironmentAttributes.FOG_COLOR, 0xFF0A0612)
                        .specialEffects(new BiomeSpecialEffects.Builder().waterColor(0x2A1A5E).build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, carvers)
                                .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, RIFT_TREES)
                                .build())
                        .build());
            })
            .add(Registries.LEVEL_STEM, context -> {
                HolderGetter<DimensionType> dimensionTypes = context.lookup(Registries.DIMENSION_TYPE);
                HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
                HolderGetter<NoiseGeneratorSettings> noiseSettings = context.lookup(Registries.NOISE_SETTINGS);

                context.register(RIFT_STEM, new LevelStem(
                        dimensionTypes.getOrThrow(RIFT_DIMENSION_TYPE),
                        new NoiseBasedChunkGenerator(
                                new FixedBiomeSource(biomes.getOrThrow(RIFT_BIOME)),
                                noiseSettings.getOrThrow(RIFT_NOISE_SETTINGS))));
            });

    // Scattered, separate islands: a flat-topped disc with a tapering underside wherever a sparse 2D mask noise is high.
    // Solid where the result is above zero.
    private static DensityFunction islandDensity(HolderGetter<NormalNoise.NoiseParameters> noises) {
        Holder<NormalNoise.NoiseParameters> maskNoise = noises.getOrThrow(ISLAND_MASK_NOISE);
        Holder<NormalNoise.NoiseParameters> heightNoise = noises.getOrThrow(ISLAND_HEIGHT_NOISE);
        Holder<NormalNoise.NoiseParameters> detailNoise = noises.getOrThrow(ISLAND_DETAIL_NOISE);

        DensityFunction mask = DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.noise(maskNoise, 1.0, 0.0)));
        DensityFunction thickness = DensityFunctions.mul(
                DensityFunctions.constant(THICKNESS_SCALE),
                DensityFunctions.add(mask, DensityFunctions.constant(-ISLAND_THRESHOLD)));

        DensityFunction centreY = DensityFunctions.add(
                DensityFunctions.constant(BASE_Y),
                DensityFunctions.mul(
                        DensityFunctions.constant(HEIGHT_VARIATION),
                        DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.noise(heightNoise, 1.0, 0.0)))));

        DensityFunction y = DensityFunctions.yClampedGradient(0, 256, 0, 256);
        DensityFunction aboveCentre = DensityFunctions.add(y, DensityFunctions.mul(DensityFunctions.constant(-1.0), centreY));
        DensityFunction belowCentre = DensityFunctions.mul(DensityFunctions.constant(-1.0), aboveCentre);

        DensityFunction topFalloff = DensityFunctions.mul(
                DensityFunctions.constant(TOP_SLOPE), DensityFunctions.max(aboveCentre, DensityFunctions.zero()));
        DensityFunction bottomFalloff = DensityFunctions.mul(
                DensityFunctions.constant(BOTTOM_SLOPE), DensityFunctions.max(belowCentre, DensityFunctions.zero()));

        DensityFunction detail = DensityFunctions.mul(
                DensityFunctions.constant(DETAIL_STRENGTH), DensityFunctions.noise(detailNoise, 1.0, 1.0));

        DensityFunction density = DensityFunctions.add(
                DensityFunctions.add(thickness, detail),
                DensityFunctions.mul(DensityFunctions.constant(-1.0), DensityFunctions.add(topFalloff, bottomFalloff)));

        return DensityFunctions.interpolated(density);
    }
}
