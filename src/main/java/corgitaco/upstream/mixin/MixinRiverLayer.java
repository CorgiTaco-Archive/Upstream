package corgitaco.upstream.mixin;

import corgitaco.upstream.Upstream;
import net.minecraft.world.biome.layer.NoiseToRiverLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseToRiverLayer.class)
public class MixinRiverLayer {

    @Inject(method = "sample", at = @At("HEAD"), cancellable = true)
    private void cancelVanillaRiverGeneration(LayerRandomnessSource context, int n, int e, int s, int w, int center, CallbackInfoReturnable<Integer> cir) {
        if (!Upstream.NOISE_CONFIG.VanillaRivers)
            cir.cancel();
    }
}
