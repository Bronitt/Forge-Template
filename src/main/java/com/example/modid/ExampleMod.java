package com.example.modid;

import com.example.modid.ExampleMod.Constants;
import com.example.modid.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.VERSION)
public class ExampleMod {

    public static final Logger LOGGER = LogManager.getLogger(Constants.MOD_NAME);

    @SidedProxy(
            clientSide = "com.example.modid.proxy.ClientProxy",
            serverSide = "com.example.modid.proxy.CommonProxy"
    )
    public static CommonProxy proxy;

    @Instance
    public static ExampleMod instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    public static class Constants {
        public static final String MOD_ID = "modid";
        public static final String MOD_NAME = "Mod Name";
        public static final String VERSION = "1.0";
    }
}
