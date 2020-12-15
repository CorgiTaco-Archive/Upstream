package corgitaco.upstream.mixin;

import corgitaco.upstream.config.UpstreamNoiseConfig;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.RiverLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RiverLayer.class)
public class MixinRiverLayer {

    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void cancelVanillaRiverGeneration(INoiseRandom context, int north, int west, int south, int east, int center, CallbackInfoReturnable<Integer> cir) {
        if (!UpstreamNoiseConfig.hasVanillaRivers.get())
            cir.cancel();
    }
}
