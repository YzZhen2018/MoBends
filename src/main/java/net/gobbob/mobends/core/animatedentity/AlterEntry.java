package net.gobbob.mobends.core.animatedentity;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.gobbob.mobends.core.data.LivingEntityData;
import net.gobbob.mobends.core.math.TransformUtils;
import net.gobbob.mobends.core.math.matrix.IMat4x4d;
import net.gobbob.mobends.standard.client.model.armor.ArmorModelFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public abstract class AlterEntry<T extends EntityLivingBase>
{
	
	/**
	 * The set of registered preview entities. This is used to determine if the system
	 * should refrain from removing an entity's data, since they aren't a part of the world
	 * and the system will think of them as dead.
	 */
	private static final Set<Entity> previewEntities = new HashSet<>();
	
	public static void registerPreviewEntity(Entity entity)
	{
		previewEntities.add(entity);
	}
	
	public static boolean isPreviewEntity(Entity entity)
	{
		return previewEntities.contains(entity);
	}
	
	private String key;
	private String unlocalizedName;
	private String postfix;
	private boolean animate;
	
	protected final IPreviewer<?> previewer;
	protected AnimatedEntity<T> owner;
	protected Map<String, BoneMetadata> boneMetadataMap;
	
	public AlterEntry(String postfix, String unlocalizedName, IPreviewer<?> previewer)
	{
		this.postfix = postfix;
		this.unlocalizedName = unlocalizedName;
		this.previewer = previewer;
		this.boneMetadataMap = new HashMap<>();
	}
	
	public AlterEntry(IPreviewer<?> previewer)
	{
		this("", null, previewer);
	}
	
	void onRegister(AnimatedEntity<T> owner)
	{
		this.owner = owner;
		this.key = this.owner.getKey() + postfix;
		if (this.unlocalizedName == null)
		{
			this.unlocalizedName = this.owner.getUnlocalizedName();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected T createPreviewEntity()
	{
		try 
		{
			EntityLiving entity = (EntityLiving) this.getOwner().entityClass.getConstructor(World.class).newInstance(Minecraft.getMinecraft().world);
			entity.world = Minecraft.getMinecraft().world;
			entity.setLocationAndAngles(0, 0, 0, 0, 0);
			entity.onInitialSpawn(entity.world.getDifficultyForLocation(entity.getPosition()), null);
			AlterEntry.registerPreviewEntity(entity);
			
			return (T) entity;
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void transformModelToCharacterSpace(IMat4x4d matrixOut)
	{
		TransformUtils.scale(matrixOut, -1.0F, -1.0F, 1.0F);
		TransformUtils.translate(matrixOut, 0.0F, -1.501F, 0.0F);
	}
	
	public void setAnimate(boolean animate)
	{
		this.animate = animate;
		ArmorModelFactory.updateMutation();
	}
	
	public boolean isAnimated()
	{
		return this.animate;
	}
	
	public void toggleAnimated()
	{
		this.setAnimate(!this.animate);
	}
	
	public String getUnlocalizedName()
	{
		return this.unlocalizedName;
	}
	
	public AnimatedEntity<?> getOwner()
	{
		return owner;
	}
	
	public IPreviewer<?> getPreviewer()
	{
		return this.previewer;
	}

	public abstract LivingEntityData<?> getDataForPreview();
	
	public String getLocalizedName()
	{
		return I18n.format(this.unlocalizedName);
	}

	public String getKey()
	{
		return this.key;
	}
	
}
