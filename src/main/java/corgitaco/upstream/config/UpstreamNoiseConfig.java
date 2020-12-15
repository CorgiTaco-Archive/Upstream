package corgitaco.upstream.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import corgitaco.upstream.Upstream;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class UpstreamNoiseConfig {

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;

    public static final ForgeConfigSpec.DoubleValue noise1Frequency;
    public static final ForgeConfigSpec.DoubleValue noise2Frequency;
    public static final ForgeConfigSpec.DoubleValue curvingMultiplier;
    public static final ForgeConfigSpec.DoubleValue minThreshold;
    public static final ForgeConfigSpec.DoubleValue maxThreshold;
    public static final ForgeConfigSpec.BooleanValue debugLatestLog;
    public static final ForgeConfigSpec.BooleanValue hasVanillaRivers;

    static {
        COMMON_BUILDER.push("Upstream_Noise");
        noise1Frequency = COMMON_BUILDER.comment("How spread out rivers generate.").defineInRange("RiverSpread", 0.002, -1000, 1000);
        noise2Frequency = COMMON_BUILDER.comment("How frequent rivers curve.").defineInRange("CurvingFrequency", 0.002, -1000, 1000);
        curvingMultiplier = COMMON_BUILDER.comment("River curving multiplier.").defineInRange("CurvingMultiplier", 0.4, 0, 1000);
        minThreshold = COMMON_BUILDER.comment("Minimum Noise Threshold for generation. Cannot be greater than MaxThreshold").defineInRange("MinThreshold", 5.591, -1000, 1000);
        maxThreshold = COMMON_BUILDER.comment("Maximum Noise Threshold for generation. Cannot be less than MinThreshold").defineInRange("MaxThreshold", 5.8, -1000, 1000);
        debugLatestLog = COMMON_BUILDER.comment("Print the lowest and highest noise values in latest.log?\nUseful for getting the range for Min & Max thresholds.").define("PrintLatestLog", false);
        hasVanillaRivers = COMMON_BUILDER.comment("Generate Vanilla Rivers?").define("VanillaRivers", false);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec config, Path path) {
        Upstream.LOGGER.info("Loading config: " + path);
        CommentedFileConfig file = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }
}
