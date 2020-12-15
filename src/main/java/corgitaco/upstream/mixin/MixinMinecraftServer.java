package corgitaco.upstream.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import corgitaco.upstream.Upstream;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
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
    protected DynamicRegistries.Impl field_240767_f_;

    @Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/Thread;Lnet/minecraft/util/registry/DynamicRegistries$Impl;Lnet/minecraft/world/storage/SaveFormat$LevelSave;Lnet/minecraft/world/storage/IServerConfiguration;Lnet/minecraft/resources/ResourcePackList;Ljava/net/Proxy;Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/resources/DataPackRegistries;Lcom/mojang/authlib/minecraft/MinecraftSessionService;Lcom/mojang/authlib/GameProfileRepository;Lnet/minecraft/server/management/PlayerProfileCache;Lnet/minecraft/world/chunk/listener/IChunkStatusListenerFactory;)V")
    private void addBYGFeatures(Thread thread, DynamicRegistries.Impl impl, SaveFormat.LevelSave session, IServerConfiguration saveProperties, ResourcePackList resourcePackManager, Proxy proxy, DataFixer dataFixer, DataPackRegistries serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, PlayerProfileCache userCache, IChunkStatusListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        configHandler();
    }

    private void configHandler() {
        Map<String, String> biomeRiverMap = new HashMap<>();
        Optional<MutableRegistry<Biome>> biomeMutableRegistry = this.field_240767_f_.func_230521_a_(Registry.BIOME_KEY);
        if (biomeMutableRegistry.isPresent()) {
            for (Map.Entry<RegistryKey<Biome>, Biome> biomeEntry : biomeMutableRegistry.get().getEntries()) {
                Biome biome = biomeEntry.getValue();
                String biomeKey = biomeEntry.getKey().getLocation().toString();
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
