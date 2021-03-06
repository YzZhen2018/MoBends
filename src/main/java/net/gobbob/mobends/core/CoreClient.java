package net.gobbob.mobends.core;

import net.gobbob.mobends.core.animatedentity.AnimatedEntityRegistry;
import net.gobbob.mobends.core.client.event.DataUpdateHandler;
import net.gobbob.mobends.core.client.event.EntityRenderHandler;
import net.gobbob.mobends.core.client.event.KeyboardHandler;
import net.gobbob.mobends.core.modcomp.RFPR;
import net.gobbob.mobends.core.pack.PackManager;
import net.gobbob.mobends.core.pack.variable.BendsVariable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

public class CoreClient extends Core
{
	
	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		
		Configuration config = configuration.getConfiguration();
		
		PackManager.initialize(config);
		BendsVariable.init();
		KeyboardHandler.initKeyBindings();

		MinecraftForge.EVENT_BUS.register(new EntityRenderHandler());
		MinecraftForge.EVENT_BUS.register(new DataUpdateHandler());
		MinecraftForge.EVENT_BUS.register(new KeyboardHandler());
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
		
		Configuration config = configuration.getConfiguration();
		
		AnimatedEntityRegistry.applyConfiguration(config);
		RFPR.init();
	}
	
}
