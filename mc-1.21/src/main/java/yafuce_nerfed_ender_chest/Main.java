package yafuce_nerfed_ender_chest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import yafuce_nerfed_ender_chest.config.ConfigIO;

public class Main implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("yafuce's Nerfed Ender Chest");

    @Override
    public void onInitialize() {
        ConfigIO.load();
        if (ConfigIO.CURRENT.costEchestOpening) EnderChestAccessManager.register();
        LOGGER.info("yafuce's Nerfed Ender Chest for MC 1.21.x initializing");
    }
}
