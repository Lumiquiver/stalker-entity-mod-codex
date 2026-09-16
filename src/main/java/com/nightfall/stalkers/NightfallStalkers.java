package com.nightfall.stalkers;

import com.nightfall.stalkers.entity.StalkerEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.entity.SpawnRestriction;

/** Entry point and common registrations for Nightfall Stalkers. */
public final class NightfallStalkers implements ModInitializer {
    public static final String MOD_ID = "nightfall";
    public static final EntityType<StalkerEntity> STALKER = Registry.register(
            Registries.ENTITY_TYPE,
            id("stalker"),
            EntityType.Builder.create(StalkerEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.72f, 2.85f)
                    .maxTrackingRange(64)
                    .build()
    );

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(STALKER, StalkerEntity.createStalkerAttributes());
        SpawnRestriction.register(STALKER, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, StalkerEntity::canSpawn);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_OVERWORLD), SpawnGroup.MONSTER,
                STALKER, 18, 1, 1);
    }
}
