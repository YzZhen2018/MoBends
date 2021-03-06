package net.gobbob.mobends.core.data;

import java.util.HashMap;
import java.util.List;

import net.gobbob.mobends.core.animation.controller.Controller;
import net.gobbob.mobends.core.client.event.DataUpdateHandler;
import net.gobbob.mobends.core.client.model.IBendsModel;
import net.gobbob.mobends.core.util.GUtil;
import net.gobbob.mobends.core.util.SmoothOrientation;
import net.gobbob.mobends.core.util.SmoothVector3f;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class EntityData<E extends Entity> implements IBendsModel
{
	protected int entityID;
	protected final E entity;

	protected double positionX, positionY, positionZ;
	protected double prevMotionX, prevMotionY, prevMotionZ;
	protected double motionX, motionY, motionZ;
	protected final HashMap<String, Object> nameToPartMap = new HashMap<>();

	public SmoothVector3f renderOffset;
    public SmoothOrientation renderRotation;
    public SmoothOrientation centerRotation;
	
    public boolean onGround = true;
    public Boolean onGroundOverride = null;
    public Boolean stillnessOverride = null;
    
	public EntityData(E entity)
	{
		this.entity = entity;
		if (this.entity != null)
		{
			this.entityID = entity.getEntityId();
		}
		this.motionX = this.prevMotionX = 0.0D;
		this.motionY = this.prevMotionY = 1.0D;
		this.motionZ = this.prevMotionZ = 0.0D;
		
		this.initModelPose();
	}
	
	public void overrideOnGroundState(boolean state)
	{
		this.onGroundOverride = state;
	}
	
	public void unsetOnGroundStateOverride()
	{
		this.onGroundOverride = null;
	}
	
	public void overrideStillness(boolean stillness)
	{
		this.stillnessOverride = stillness;
	}
	
	public void unsetStillnessOverride()
	{
		this.stillnessOverride = null;
	}

	public void initModelPose()
	{
		this.renderOffset = new SmoothVector3f();
		this.renderRotation = new SmoothOrientation();
		this.centerRotation = new SmoothOrientation();
		
		this.nameToPartMap.put("renderRotation", renderRotation);
		this.nameToPartMap.put("centerRotation", centerRotation);
	}

	/*
	 * Updates all the model's parts to be in their next frame. Called in
	 * EntityData.update();
	 */
	public void updateParts(float ticksPerFrame)
	{
		this.renderOffset.update(ticksPerFrame);
		this.renderRotation.update(ticksPerFrame);
		this.centerRotation.update(ticksPerFrame);
	}

	public boolean calcOnGround()
	{
		if (this.onGroundOverride != null)
			return this.onGroundOverride;
		
		Entity entity = Minecraft.getMinecraft().world.getEntityByID(this.entityID);
		if (entity == null)
			return false;

		List<AxisAlignedBB> list = entity.world.getCollisionBoxes(entity, entity.getEntityBoundingBox().offset(0, -0.025f, 0));
		return list.size() > 0;
	}
	
	public boolean calcCollidedHorizontally()
	{
		Entity entity = Minecraft.getMinecraft().world.getEntityByID(this.entityID);
		if (entity == null)
			return false;

		List<AxisAlignedBB> list = entity.world.getCollisionBoxes(entity,
				entity.getEntityBoundingBox().offset(this.motionX, 0, this.motionZ));
		return list.size() > 0;
	}

	
	public double getPositionX() { return this.positionX; }
	public double getPositionY() { return this.positionY; }
	public double getPositionZ() { return this.positionZ; }
	public double getMotionX() { return this.motionX; }
	public double getMotionY() { return this.motionY; }
	public double getMotionZ() { return this.motionZ; }
	public double getPrevMotionX() { return this.prevMotionX; }
	public double getPrevMotionY() { return this.prevMotionY; }
	public double getPrevMotionZ() { return this.prevMotionZ; }
	public double getInterpolatedMotionX() { return this.prevMotionX + (this.motionX - this.prevMotionX) * DataUpdateHandler.partialTicks; }
	public double getInterpolatedMotionY() { return this.prevMotionY + (this.motionY - this.prevMotionY) * DataUpdateHandler.partialTicks; }
	public double getInterpolatedMotionZ() { return this.prevMotionZ + (this.motionZ - this.prevMotionZ) * DataUpdateHandler.partialTicks; }
	
	public boolean isOnGround()
	{
		return this.onGround;
	}
	
	public boolean isStillHorizontally()
	{
		return this.stillnessOverride != null ?
				this.stillnessOverride :
				(Math.abs(motionX) < 1e-9 && Math.abs(motionZ) < 1e-9);
	}

	public abstract Controller<?> getController();
	
	/*
	 * Called during the render tick in EntityDatabase.updateRender()
	 */
	public void update(float partialTicks)
	{
		if (this.entity == null)
			return;

		this.updateParts(DataUpdateHandler.ticksPerFrame);
	}

	public E getEntity()
	{
		return this.entity;
	}

	public float getLookAngle()
	{
		Vec3d lookVec = this.entity.getLookVec();
		return (float) GUtil.angleFromCoordinates(lookVec.x, lookVec.z);
	}
	
	private float getWorldMovementAngle()
	{
		return (float) GUtil.angleFromCoordinates(this.motionX, this.motionZ);
	}

	public float getMovementAngle()
	{
		if (isStillHorizontally())
			return 0;
		return this.getWorldMovementAngle() - this.getLookAngle();
	}
	
	public double getForwardMomentum()
	{
		if (isStillHorizontally())
			return 0;
		Vec3d lookVec = this.entity.getLookVec();
		Vec3d lookVecHorizontal = new Vec3d(lookVec.x, 0, lookVec.z).normalize();
		return lookVecHorizontal.x * this.motionX + lookVecHorizontal.z * this.motionZ;
	}
	
	public double getSidewaysMomentum()
	{
		if (isStillHorizontally())
			return 0;
		Vec3d rightVec = entity.getLookVec().rotateYaw(-GUtil.PI / 2.0F);
		Vec3d rightVecHorizontal = new Vec3d(rightVec.x, 0, rightVec.z).normalize();
		return rightVecHorizontal.x * this.motionX + rightVecHorizontal.z * this.motionZ;
	}
	
	private static final float STRAFING_THRESHOLD = 30.0f;

	public boolean isStrafing()
	{
		float angle = this.getMovementAngle();
		return (angle >= STRAFING_THRESHOLD && angle <= 180.0f - STRAFING_THRESHOLD)
				|| (angle >= -180.0f + STRAFING_THRESHOLD && angle <= -STRAFING_THRESHOLD);
	}
	
	/**
	 * @return True, if the player is sufficiently underwater.
	 */
	public boolean isUnderwater()
	{
		if (!this.entity.isInWater())
			return false;
		
		int blockX = MathHelper.floor(this.entity.posX);
		int blockY = MathHelper.floor(this.entity.posY + 2);
		int blockZ = MathHelper.floor(this.entity.posZ);
		IBlockState state = Minecraft.getMinecraft().world.getBlockState(new BlockPos(blockX, blockY, blockZ));
		return state.getBlock() instanceof BlockStaticLiquid;
	}

	public double getPrevMotionMagnitude()
	{
		return Math.sqrt(this.prevMotionX * this.prevMotionX + this.prevMotionY * this.prevMotionY + this.prevMotionZ * this.prevMotionZ);
	}
	
	public double getMotionMagnitude()
	{
		return Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
	}
	
	public double getInterpolatedMotionMagnitude()
	{
		return interpolateMagitude(this.getMotionMagnitude(), this.getPrevMotionMagnitude());
	}
	
	public double getXZMotionMagnitude() {
		return Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
	}
	
	public double getPrevXZMotionMagnitude() {
		return Math.sqrt(this.prevMotionX * this.prevMotionX + this.prevMotionZ * this.prevMotionZ);
	}
	
	public double getInterpolatedXZMotionMagnitude()
	{
		return interpolateMagitude(this.getXZMotionMagnitude(), this.getPrevXZMotionMagnitude());
	}
	
	private static double interpolateMagitude(double magnitude, double prevMagnitude)
	{
		return prevMagnitude + (magnitude - prevMagnitude) * DataUpdateHandler.partialTicks;
	}

	public void updateClient()
	{
		this.prevMotionX = this.motionX;
		this.prevMotionY = this.motionY;
		this.prevMotionZ = this.motionZ;

		this.motionX = this.entity.posX - this.positionX;
		this.motionY = this.entity.posY - this.positionY;
		this.motionZ = this.entity.posZ - this.positionZ;

		this.positionX = this.entity.posX;
		this.positionY = this.entity.posY;
		this.positionZ = this.entity.posZ;
	}
	
	@Override
	public Object getPartForName(String name)
	{
		return nameToPartMap.get(name);
	}
	
	public void onTicksRestart() {}
}
