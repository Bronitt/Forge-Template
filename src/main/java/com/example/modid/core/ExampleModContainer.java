package com.example.modid.core;

import com.example.modid.Tags;
import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class ExampleModContainer extends DummyModContainer {

    public ExampleModContainer() {
        super(new ModMetadata());
        ModMetadata meta = this.getMetadata();
        meta.modId = Tags.MOD_ID + "-core";
        meta.name = Tags.MOD_NAME + " Core";
        meta.version = Tags.VERSION;
        meta.authorList.add("QSTeam");
        meta.authorList.add("Bron1t");
        meta.description = "Core mod for " + Tags.MOD_NAME;
    }

    @SuppressWarnings("all")
    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

}