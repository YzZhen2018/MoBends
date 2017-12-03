package net.gobbob.mobends.client.renderer.entity;

import net.gobbob.mobends.client.model.entity.ModelBendsPlayer;
import net.gobbob.mobends.client.renderer.entity.layers.LayerBendsCape;
import net.gobbob.mobends.client.renderer.entity.layers.LayerBendsCustomHead;
import net.gobbob.mobends.client.renderer.entity.layers.LayerBendsPlayerArmor;
import net.gobbob.mobends.data.Data_Player;
import net.gobbob.mobends.settings.SettingsBoolean;
import net.gobbob.mobends.settings.SettingsNode;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class RenderBendsPlayer extends RenderPlayer{
    private boolean smallArms;
    
    public RenderBendsPlayer(RenderManager renderManager)
    {
        super(renderManager, false);
        this.smallArms = false;
        this.mainModel = new ModelBendsPlayer(0.0F, false);
        this.layerRenderers.clear();
        this.addLayer(new LayerBendsPlayerArmor(this));
        this.addLayer(new LayerHeldItem(this));
        //this.addLayer(new LayerArrow(this));
        //this.addLayer(new LayerDeadmau5Head(this));
        this.addLayer(new LayerBendsCape(this));
        this.addLayer(new LayerBendsCustomHead((ModelBendsPlayer) this.getPlayerModel()));
    }

    public RenderBendsPlayer(RenderManager renderManager, boolean useSmallArms)
    {
    	super(renderManager, useSmallArms);
    	this.smallArms = useSmallArms;
    	this.mainModel = new ModelBendsPlayer(0.0F, useSmallArms);
    	this.layerRenderers.clear();
    	this.addLayer(new LayerBendsPlayerArmor(this));
        this.addLayer(new LayerHeldItem(this));
        //this.addLayer(new LayerArrow(this));
        //this.addLayer(new LayerDeadmau5Head(this));
        this.addLayer(new LayerBendsCape(this));
        this.addLayer(new LayerBendsCustomHead((ModelBendsPlayer) this.getPlayerModel()));
    }
    
    @Override
    public ModelPlayer getPlayerModel()
    {
    	if(!(this.mainModel instanceof ModelBendsPlayer)){
    		this.mainModel = new ModelBendsPlayer(0.0F, this.smallArms);
    	}
    	return (ModelBendsPlayer)this.mainModel;
    }
    
    @Override
    protected void rotateCorpse(EntityLivingBase p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_)
    {
	    super.rotateCorpse(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
    }
    
    private void func_177137_d(AbstractClientPlayer p_177137_1_)
    {
    	ModelBendsPlayer modelplayer = (ModelBendsPlayer) this.getPlayerModel();

        if (p_177137_1_.isSpectator())
        {
            modelplayer.setInvisible(false);
            modelplayer.bipedHead.showModel = true;
            modelplayer.bipedHeadwear.showModel = true;
        }
        else
        {
            ItemStack itemstack = p_177137_1_.inventory.getCurrentItem();
            modelplayer.setInvisible(true);
            modelplayer.bipedHeadwear.showModel = p_177137_1_.func_175148_a(EnumPlayerModelParts.HAT);
            modelplayer.bipedBodyWear.showModel = p_177137_1_.func_175148_a(EnumPlayerModelParts.JACKET);
            modelplayer.bipedLeftLegwear.showModel = p_177137_1_.func_175148_a(EnumPlayerModelParts.LEFT_PANTS_LEG);
            modelplayer.bipedRightLegwear.showModel = p_177137_1_.func_175148_a(EnumPlayerModelParts.RIGHT_PANTS_LEG);
            modelplayer.bipedLeftArmwear.showModel = p_177137_1_.func_175148_a(EnumPlayerModelParts.LEFT_SLEEVE);
            modelplayer.bipedRightArmwear.showModel = p_177137_1_.func_175148_a(EnumPlayerModelParts.RIGHT_SLEEVE);
            modelplayer.heldItemLeft = 0;
            modelplayer.aimedBow = false;
            modelplayer.isSneak = p_177137_1_.isSneaking();

            if (itemstack == null)
            {
                modelplayer.heldItemRight = 0;
            }
            else
            {
                modelplayer.heldItemRight = 1;

                if (p_177137_1_.getItemInUseCount() > 0)
                {
                    EnumAction enumaction = itemstack.getItemUseAction();

                    if (enumaction == EnumAction.BLOCK)
                    {
                        modelplayer.heldItemRight = 3;
                    }
                    else if (enumaction == EnumAction.BOW)
                    {
                        modelplayer.aimedBow = true;
                    }
                }
            }
        }
    }

    protected ResourceLocation func_180594_a(AbstractClientPlayer p_180594_1_)
    {
        return p_180594_1_.getLocationSkin();
    }

    public void func_82422_c()
    {
        GlStateManager.translate(0.0F, 0.1875F, 0.0F);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    @Override
    protected void preRenderCallback(AbstractClientPlayer p_77041_1_, float p_77041_2_)
    {
        float f1 = 0.9375F;
        GlStateManager.scale(f1, f1, f1);
        
        ((ModelBendsPlayer)this.mainModel).updateWithEntityData(p_77041_1_);
        ((ModelBendsPlayer)this.mainModel).postRenderTranslate(0.0625f);
    
        Data_Player data = Data_Player.get(p_77041_1_.getEntityId());
    
        if(((SettingsBoolean)SettingsNode.getSetting("swordTrail")).data){
			GL11.glPushMatrix();
				float f5 = 0.0625F;
				GL11.glScalef(-f5, -f5, f5);
				data.swordTrail.render((ModelBendsPlayer)this.mainModel);
				GL11.glColor4f(1,1,1,1);
			GL11.glPopMatrix();
        }
        
        ((ModelBendsPlayer)this.mainModel).postRenderRotate(0.0625f);
    }
    
    public void func_177138_b(AbstractClientPlayer p_177138_1_)
    {
        float f = 1.0F;
        GlStateManager.color(f, f, f);
        ModelBendsPlayer modelplayer = (ModelBendsPlayer) this.getPlayerModel();
        this.func_177137_d(p_177138_1_);
        modelplayer.swingProgress = 0.0F;
        modelplayer.isSneak = false;
        modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, p_177138_1_);
        modelplayer.func_178725_a();
    }

    public void func_177139_c(AbstractClientPlayer p_177139_1_)
    {
        float f = 1.0F;
        GlStateManager.color(f, f, f);
        ModelBendsPlayer modelplayer = (ModelBendsPlayer) this.getPlayerModel();
        this.func_177137_d(p_177139_1_);
        modelplayer.isSneak = false;
        modelplayer.swingProgress = 0.0F;
        modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, p_177139_1_);
        modelplayer.func_178726_b();
    }

    /**
     * Sets a simple glTranslate on a LivingEntity.
     */
    protected void renderLivingAt(AbstractClientPlayer p_77039_1_, double p_77039_2_, double p_77039_4_, double p_77039_6_)
    {
    	super.renderLivingAt(p_77039_1_, p_77039_2_, p_77039_4_, p_77039_6_);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLivingBase p_77041_1_, float p_77041_2_)
    {
        this.preRenderCallback((AbstractClientPlayer)p_77041_1_, p_77041_2_);
    }

    /**
     * Sets a simple glTranslate on a LivingEntity.
     */
    protected void renderLivingAt(EntityLivingBase p_77039_1_, double p_77039_2_, double p_77039_4_, double p_77039_6_)
    {
        this.renderLivingAt((AbstractClientPlayer)p_77039_1_, p_77039_2_, p_77039_4_, p_77039_6_);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     */
    public void doRender(EntityLivingBase entity, double x, double y, double z, float p_76986_8_, float partialTicks)
    {
    	//this.mainModel = new ModelBendsPlayer(0.0F, this.smallArms);
        this.func_180596_a((AbstractClientPlayer)entity, x, y, z, p_76986_8_, partialTicks);
    }
    
    @Override
    public ModelBase getMainModel()
    {
        return this.getPlayerModel();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.func_180594_a((AbstractClientPlayer)entity);
    }
    
    @Override
    public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float partialTicks)
    {
    	super.doRender((EntityLivingBase)entity, x, y, z, p_76986_8_, partialTicks);
    }
}