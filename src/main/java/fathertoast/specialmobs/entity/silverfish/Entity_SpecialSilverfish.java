package fathertoast.specialmobs.entity.silverfish;

import fathertoast.specialmobs.*;
import fathertoast.specialmobs.bestiary.*;
import fathertoast.specialmobs.config.*;
import fathertoast.specialmobs.entity.*;
import fathertoast.specialmobs.loot.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;
import java.util.List;

public
class Entity_SpecialSilverfish extends EntitySilverfish implements ISpecialMob
{
	@SuppressWarnings( "unused" )
	public static
	BestiaryInfo GET_BESTIARY_INFO( )
	{
		BestiaryInfo info = new BestiaryInfo( 0x303030 );
		return info;
	}
	
	private static final DataParameter< Float > SCALE = EntityDataManager.createKey( Entity_SpecialSilverfish.class, DataSerializers.FLOAT );
	
	private static final ResourceLocation[] TEXTURES = {
		new ResourceLocation( "textures/entity/silverfish.png" )
	};
	
	static
	String GET_TEXTURE_PATH( String type )
	{
		return SpecialMobsMod.TEXTURE_PATH + "silverfish/" + type + ".png";
	}
	
	static
	void ADD_BASE_LOOT( LootTableBuilder loot )
	{
		loot.addLootTable( "main", "Base loot", LootTableList.ENTITIES_SILVERFISH );
	}
	
	// This mob's special mob data.
	private SpecialMobData specialData;
	
	public
	Entity_SpecialSilverfish( World world ) { super( world ); }
	
	@Override
	protected
	void initEntityAI( )
	{
		super.initEntityAI( );
		initTypeAI( );
	}
	
	protected
	void initTypeAI( ) { }
	
	@Override
	protected
	void entityInit( )
	{
		super.entityInit( );
		specialData = new SpecialMobData( this, SCALE );
	}
	
	@Override
	protected
	void applyEntityAttributes( )
	{
		super.applyEntityAttributes( );
		
		float prevMax = getMaxHealth( );
		applyTypeAttributes( );
		setHealth( getMaxHealth( ) + getHealth( ) - prevMax );
	}
	
	/** Called to modify the mob's attributes based on the variant. */
	protected
	void applyTypeAttributes( ) { }
	
	// START ISpecialMob ================
	
	/**
	 * @return this mob's special data
	 */
	@Override
	public final
	SpecialMobData getSpecialData( ) { return specialData; }
	
	/**
	 * Gets the experience that should be dropped by the entity.
	 */
	@Override
	public final
	int getExperience( ) { return experienceValue; }
	
	/**
	 * Sets the experience that should be dropped by the entity.
	 */
	@Override
	public final
	void setExperience( int xp ) { experienceValue = xp; }
	
	/**
	 * Sets the entity's immunity to fire status.
	 */
	@Override
	public final
	void setImmuneToFire( boolean immune ) { isImmuneToFire = immune; }
	
	/**
	 * @return All the textures for the entity.
	 */
	@Override
	public
	ResourceLocation[] getDefaultTextures( ) { return TEXTURES; }
	
	// END ISpecialMob ================
	
	// Overridden to modify attack effects.
	protected
	void onTypeAttack( Entity target ) { }
	
	// START SpecialMobData integration ================
	
	// Called each tick while this entity is alive.
	@Override
	public
	void onLivingUpdate( )
	{
		super.onLivingUpdate( );
		getSpecialData( ).onUpdate( );
	}
	
	// Called when this entity is first spawned to initialize it.
	@Override
	@Nullable
	public
	IEntityLivingData onInitialSpawn( DifficultyInstance difficulty, @Nullable IEntityLivingData data )
	{
		data = super.onInitialSpawn( difficulty, data );
		
		if( rand.nextFloat( ) < Config.get( ).SILVERFISH.AGGRESSIVE_CHANCE ) {
			// Immediately start calling for reinforcements if it can find a player
			if( getAttackTarget( ) == null ) {
				List< EntityPlayer > nearbyPlayers = world.getEntitiesWithinAABB(
					EntityPlayer.class, getEntityBoundingBox( ).expand( 16.0, 8.0, 16.0 )
				);
				for( EntityPlayer player : nearbyPlayers ) {
					if( player != null && canEntityBeSeen( player ) ) {
						setAttackTarget( player );
						break;
					}
				}
			}
			if( getAttackTarget( ) != null ) {
				attackEntityFrom( DamageSource.MAGIC, 0.0F );
			}
		}
		
		return data;
	}
	
	// Called to attack the target.
	@Override
	public
	boolean attackEntityAsMob( Entity target )
	{
		if( super.attackEntityAsMob( target ) ) {
			onTypeAttack( target );
			return true;
		}
		return false;
	}
	
	@Override
	public
	float getEyeHeight( ) { return super.getEyeHeight( ) * getSpecialData( ).getBaseScaleForPreScaledValues( ); }
	
	// Saves this entity to NBT.
	@Override
	public
	void writeEntityToNBT( NBTTagCompound tag )
	{
		super.writeEntityToNBT( tag );
		NBTTagCompound saveTag = SpecialMobData.getSaveLocation( tag );
		getSpecialData( ).writeToNBT( saveTag );
	}
	
	// Reads this entity from NBT.
	@Override
	public
	void readEntityFromNBT( NBTTagCompound tag )
	{
		super.readEntityFromNBT( tag );
		NBTTagCompound saveTag = SpecialMobData.getSaveLocation( tag );
		getSpecialData( ).readFromNBT( saveTag );
	}
	
	// Sets this entity on fire.
	@Override
	public
	void setFire( int time )
	{
		if( !getSpecialData( ).isImmuneToBurning( ) ) {
			super.setFire( time );
		}
	}
	
	// Returns the current armor level of this mob.
	@Override
	public
	boolean canBeLeashedTo( EntityPlayer player ) { return !getLeashed( ) && getSpecialData( ).allowLeashing( ); }
	
	// Sets the entity inside a web block.
	@Override
	public
	void setInWeb( )
	{
		if( !getSpecialData( ).isImmuneToWebs( ) ) {
			super.setInWeb( );
		}
	}
	
	// Called when the mob falls. Calculates and applies fall damage.
	@Override
	public
	void fall( float distance, float damageMultiplier ) { super.fall( distance, damageMultiplier * getSpecialData( ).getFallDamageMultiplier( ) ); }
	
	// Return whether this entity should NOT trigger a pressure plate or a tripwire.
	@Override
	public
	boolean doesEntityNotTriggerPressurePlate( ) { return getSpecialData( ).ignorePressurePlates( ); }
	
	// True if the entity can breathe underwater.
	@Override
	public
	boolean canBreatheUnderwater( ) { return getSpecialData( ).canBreatheInWater( ); }
	
	// True if the entity can be pushed by flowing water.
	@Override
	public
	boolean isPushedByWater( ) { return !getSpecialData( ).ignoreWaterPush( ); }
	
	// Returns true if the potion can be applied.
	@Override
	public
	boolean isPotionApplicable( PotionEffect effect ) { return getSpecialData( ).isPotionApplicable( effect ); }
	
	// END SpecialMobData integration ================
}
