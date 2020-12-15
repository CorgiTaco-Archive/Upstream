package corgitaco.upstream.mixin;

import corgitaco.upstream.Upstream;
import corgitaco.upstream.config.UpstreamNoiseConfig;
import corgitaco.upstream.util.FastNoise;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.Layer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Layer.class)
public class MixinLayer {

    private static long seed;
    private static FastNoise noise;
    private static FastNoise noise2;
    private static double max = -1000;
    private static double min = 10000;

    private static void getMinAndMax(double noiseVal) {
        if (noiseVal > max) {
            max = noiseVal;
            Upstream.LOGGER.info("Upstream: Max Noise: " + max);
        }
        if (noiseVal < min) {
            min = noiseVal;
            Upstream.LOGGER.info("Upstream: Min noise: " + min);
        }
    }


    @SuppressWarnings("ConstantConditions")
    @Inject(method = "func_242936_a", at = @At("RETURN"), cancellable = true)
    private void injectUpstreamRivers(Registry<Biome> registry, int x, int z, CallbackInfoReturnable<Biome> cir) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        setupNoise((int) server.getWorld(World.OVERWORLD).getWorld().getSeed());

        double noiseVal = noise.GetNoise(x, z) * 10;
        float noise2Val = noise2.GetNoise(x, z);
        if (UpstreamNoiseConfig.debugLatestLog.get())
            getMinAndMax(noiseVal);

        double curvingMultiplier = UpstreamNoiseConfig.curvingMultiplier.get();
        if (noiseVal > UpstreamNoiseConfig.minThreshold.get() + Math.abs(noise2Val * curvingMultiplier) && noiseVal < UpstreamNoiseConfig.maxThreshold.get() + Math.abs(noise2Val * curvingMultiplier)) {

            ResourceLocation key = registry.getKey(cir.getReturnValue());
            if (Upstream.biomeToRiverMap.containsKey(key.toString())) {
                ResourceLocation riverKey = new ResourceLocation(Upstream.biomeToRiverMap.get(key.toString()));
                Optional<Biome> optional = registry.getOptional(riverKey);
                optional.ifPresent(cir::setReturnValue);
            }
        }
    }

    private static void setupNoise(long serverSeed) {
        if (seed != serverSeed || noise == null || noise2 == null) {
            seed = serverSeed;
            noise = new FastNoise((int) seed);
            noise.SetFractalType(FastNoise.FractalType.RigidMulti);
            noise.SetNoiseType(FastNoise.NoiseType.CubicFractal);
            noise.SetGradientPerturbAmp(1);
            noise.SetFractalOctaves(5);
            noise.SetFractalGain(0.3f);
            noise.SetFrequency(UpstreamNoiseConfig.noise1Frequency.get().floatValue());

            noise2 = new FastNoise((int) seed);
            noise2.SetFractalType(FastNoise.FractalType.Billow);
            noise2.SetNoiseType(FastNoise.NoiseType.SimplexFractal);
            noise2.SetGradientPerturbAmp(1);
            noise2.SetFractalOctaves(5);
            noise2.SetFractalGain(0.3f);
            noise2.SetFrequency(UpstreamNoiseConfig.noise2Frequency.get().floatValue());
        }
    }
}
