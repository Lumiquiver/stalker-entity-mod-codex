package com.nightfall.stalkers.client;

import com.nightfall.stalkers.NightfallStalkers;
import com.nightfall.stalkers.client.render.StalkerEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

/** Client-only registrations. */
public final class NightfallStalkersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(NightfallStalkers.STALKER, StalkerEntityRenderer::new);
    }
}
