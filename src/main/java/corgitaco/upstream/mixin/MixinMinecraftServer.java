package corgitaco.upstream.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import corgitaco.upstream.Upstream;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Shadow
    @Final
    protected DynamicRegistryManager.Impl registryManager;

    @Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/Thread;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/world/SaveProperties;Lnet/minecraft/resource/ResourcePackManager;Ljava/net/Proxy;Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/resource/ServerResourceManager;Lcom/mojang/authlib/minecraft/MinecraftSessionService;Lcom/mojang/authlib/GameProfileRepository;Lnet/minecraft/util/UserCache;Lnet/minecraft/server/WorldGenerationProgressListenerFactory;)V")
    private void addBYGFeatures(Thread thread, DynamicRegistryManager.Impl impl, LevelStorage.Session session, SaveProperties saveProperties, ResourcePackManager resourcePackManager, Proxy proxy, DataFixer dataFixer, ServerResourceManager serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        configHandler();
        Upstream.currentServer = (MinecraftServer) (Object) this;
    }

    private void configHandler() {
        Map<String, String> biomeRiverMap = new HashMap<>();
        Optional<MutableRegistry<Biome>> biomeMutableRegistry = this.registryManager.getOptional(Registry.BIOME_KEY);
        if (biomeMutableRegistry.isPresent()) {
            for (Map.Entry<RegistryKey<Biome>, Biome> biomeEntry : biomeMutableRegistry.get().getEntries()) {
                Biome biome = biomeEntry.getValue();
                String biomeKey = biomeEntry.getKey().getValue().toString();
                if (biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND) {
                    if (biome.getCategory() == Biome.Category.ICY)
                        biomeRiverMap.put(biomeKey, "minecraft:frozen_river");
                    else if (biome.getCategory() == Biome.Category.OCEAN || biome.getCategory() == Biome.Category.SWAMP)
                        biomeRiverMap.put(biomeKey, biomeKey);
                    else
                        biomeRiverMap.put(biomeKey, "minecraft:river");
                }
            }
        }
        Upstream.handleUpstreamRiverConfig(Upstream.CONFIG_PATH.resolve(Upstream.MOD_ID + "-rivers.json"), biomeRiverMap);
    }
}
