package corgitaco.upstream.config;

import corgitaco.upstream.Upstream;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;


@Config(name = Upstream.MOD_ID + "/" + Upstream.MOD_ID + "-noise")
public class UpstreamNoiseConfig implements ConfigData {


    @Comment(value = "How spread out rivers generate.")
    public double RiverSpread = 0.002;

    @Comment(value = "How frequent rivers curve.")
    public double CurvingFrequency = 0.002;

    @Comment(value = "River curving multiplier.")
    public double CurvingMultiplier = 0.4;

    @Comment(value = "Minimum Noise Threshold for generation. Cannot be greater than MaxThreshold")
    public double MinThreshold = 5.591;

    @Comment(value = "Maximum Noise Threshold for generation. Cannot be less than MinThreshold")
    public double MaxThreshold = 5.8;

    @Comment(value = "Print the lowest and highest noise values in latest.log?\\nUseful for getting the range for Min & Max thresholds.")
    public boolean PrintLatestLog = false;

    @Comment(value = "Generate Vanilla Rivers")
    public boolean VanillaRivers = false;

}
