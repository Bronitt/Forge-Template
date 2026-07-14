package com.example.modid;

import com.example.modid.proxy.CommonProxy;
import com.example.modid.util.LogUtil;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES)
public class ExampleMod {

    public static final Logger LOGGER = LogUtil.getModLogger();

    @SidedProxy(
            modId = Reference.MOD_ID,
            clientSide = Reference.CLIENT_PROXY_CLASS,
            serverSide = Reference.SERVER_PROXY_CLASS
    )
    public static CommonProxy proxy;

    @Mod.Instance
    public static ExampleMod instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
