package corgitaco.upstream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import corgitaco.upstream.config.UpstreamNoiseConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Upstream implements ModInitializer {

    public static final String MOD_ID = "upstream";
    public static Logger LOGGER = LogManager.getLogger();
    public static MinecraftServer currentServer;

    public static Map<String, String> biomeToRiverMap;
    public static final Path CONFIG_PATH = new File(String.valueOf(FabricLoader.INSTANCE.getConfigDir().resolve(MOD_ID))).toPath();

    public static UpstreamNoiseConfig NOISE_CONFIG;

    @Override
    public void onInitialize() {
        File dir = new File(CONFIG_PATH.toString());
        if (!dir.exists())
            dir.mkdir();

        AutoConfig.register(UpstreamNoiseConfig.class, JanksonConfigSerializer::new);
        NOISE_CONFIG = AutoConfig.getConfigHolder(UpstreamNoiseConfig.class).getConfig();
    }

    public Upstream() {
    }

    public static void handleUpstreamRiverConfig(Path path, Map<String, String> biomeRiverMap) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.disableHtmlEscaping();
        Gson gson = gsonBuilder.create();
        Map<String, String> sortedMap = new TreeMap<>(Comparator.comparing(String::toString));
        sortedMap.putAll(biomeRiverMap);
        final File CONFIG_FILE = new File(String.valueOf(path));

        if (!CONFIG_FILE.exists()) {
            createRiverJson(path, sortedMap);
        }
        try (Reader reader = new FileReader(path.toString())) {
            Map<String, String> biomeDataListHolder = gson.fromJson(reader, Map.class);
            if (biomeDataListHolder != null) {
                Upstream.biomeToRiverMap = biomeDataListHolder;
            } else
                LOGGER.error(MOD_ID + "-rivers.json could not be read");

        } catch (IOException e) {
            LOGGER.error(MOD_ID + "-rivers.json could not be read");
        }
    }

    public static void createRiverJson(Path path, Map<String, String> biomeRiverMap) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.disableHtmlEscaping();
        Gson gson = gsonBuilder.create();

        String jsonString = gson.toJson(biomeRiverMap);

        try {
            Files.write(path, jsonString.getBytes());
        } catch (IOException e) {
            LOGGER.error(MOD_ID + "-rivers.json could not be created");
        }
    }
}