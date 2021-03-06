package net.gobbob.mobends.standard.client;

import net.gobbob.mobends.core.Core;
import net.gobbob.mobends.standard.client.event.RenderingEventHandler;
import net.gobbob.mobends.standard.client.renderer.entity.RenderBendsSpectralArrow;
import net.gobbob.mobends.standard.client.renderer.entity.RenderBendsTippedArrow;
import net.gobbob.mobends.standard.main.CommonProxy;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
	
	@Override
	public void preInit()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntitySpectralArrow.class, RenderBendsSpectralArrow::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTippedArrow.class, RenderBendsTippedArrow::new);
	}

	@Override
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(new RenderingEventHandler());
	}
	
	@Override
	public void postInit() {}
	
	@Override
	public void createCore()
	{
		Core.createAsClient();
	}
}
