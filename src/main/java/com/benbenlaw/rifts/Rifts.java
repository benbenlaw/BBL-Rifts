package com.benbenlaw.rifts;

import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.block.entity.renderer.RiftGeneratorBlockEntityRenderer;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.entity.EpochopolisEntities;
import com.benbenlaw.rifts.item.EpochopolisCreativeTab;
import com.benbenlaw.rifts.item.RiftsDataComponents;
import com.benbenlaw.rifts.item.RiftsItems;
import com.benbenlaw.rifts.particle.RiftAbsorbParticle;
import com.benbenlaw.rifts.particle.RiftsParticles;
import com.benbenlaw.rifts.recipe.RiftsRecipeTypes;
import com.benbenlaw.rifts.screen.EpochopolisMenuTypes;
import com.benbenlaw.rifts.screen.generator.RiftGeneratorScreen;
import com.benbenlaw.rifts.screen.infuser.RIftInfuserScreen;
import com.benbenlaw.rifts.screen.pylon.RiftPylonScreen;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Rifts.MOD_ID)
public class Rifts {
    public static final String MOD_ID = "rifts";
    public static final Logger LOGGER = LogManager.getLogger();

    public Rifts(final IEventBus eventBus, final ModContainer modContainer) {

        RiftsBlocks.BLOCKS.register(eventBus);
        RiftsBlockEntities.BLOCK_ENTITIES.register(eventBus);
        RiftsItems.ITEMS.register(eventBus);
        RiftsDataComponents.COMPONENTS.register(eventBus);
        EpochopolisCreativeTab.CREATIVE_MODE_TABS.register(eventBus);
        EpochopolisEntities.ENTITY_TYPES.register(eventBus);
        EpochopolisMenuTypes.MENUS.register(eventBus);
        RiftsParticles.PARTICLES.register(eventBus);
        RiftsRecipeTypes.SERIALIZER.register(eventBus);
        RiftsRecipeTypes.TYPES.register(eventBus);

        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.STARTUP, RiftsStartupConfig.SPEC, "bbl/rifts-startup.toml");
    }

    public void commonSetup(RegisterPayloadHandlersEvent event) {
        //AbilityLockNetworking.registerNetworking(event);
    }

    @EventBusSubscriber(modid = Rifts.MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(RiftsBlockEntities.RIFT_GENERATOR_BLOCK_ENTITY.get(), RiftGeneratorBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerParticleProviders(final RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(RiftsParticles.RIFT_ABSORB.get(), RiftAbsorbParticle.Provider::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(EpochopolisMenuTypes.RIFT_GENERATOR_MENU.get(), RiftGeneratorScreen::new);
            event.register(EpochopolisMenuTypes.RIFT_INFUSER_MENU.get(), RIftInfuserScreen::new);
            event.register(EpochopolisMenuTypes.RIFT_PYLON_MENU.get(), RiftPylonScreen::new);
        }
    }

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }


}

