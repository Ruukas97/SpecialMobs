package fathertoast.specialmobs.entity.zombie;

import fathertoast.specialmobs.bestiary.*;
import fathertoast.specialmobs.loot.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public
class EntityFireZombie extends Entity_SpecialZombie
{
	@SuppressWarnings( "unused" )
	public static
	BestiaryInfo GET_BESTIARY_INFO( )
	{
		BestiaryInfo info = new BestiaryInfo( 0xdc1a00 );
		info.weightExceptions = BestiaryInfo.DEFAULT_THEME_FIRE;
		return info;
	}
	
	private static final ResourceLocation[] TEXTURES = {
		new ResourceLocation( GET_TEXTURE_PATH( "fire" ) )
	};
	
	public static ResourceLocation LOOT_TABLE;
	
	@SuppressWarnings( "unused" )
	public static
	void BUILD_LOOT_TABLE( LootTableBuilder loot )
	{
		ADD_BASE_LOOT( loot );
		loot.addCommonDrop( "common", "Fire charge", Items.FIRE_CHARGE );
		loot.addUncommonDrop( "uncommon", "Coal", Items.COAL );
	}
	
	public
	EntityFireZombie( World world )
	{
		super( world );
		getSpecialData( ).setImmuneToFire( true );
		getSpecialData( ).setDamagedByWater( true );
	}
	
	@Override
	public
	ResourceLocation[] getDefaultTextures( ) { return TEXTURES; }
	
	@Override
	protected
	ResourceLocation getLootTable( ) { return LOOT_TABLE; }
	
	/** Called to modify the mob's attributes based on the variant. */
	@Override
	protected
	void applyTypeAttributes( )
	{
		experienceValue += 1;
	}
	
	@Override
	protected
	boolean shouldBurnInDay( ) { return false; }
	
	@Override
	protected
	SoundEvent getAmbientSound( ) { return SoundEvents.ENTITY_HUSK_AMBIENT; }
	
	@Override
	protected
	SoundEvent getHurtSound( DamageSource damageSource ) { return SoundEvents.ENTITY_HUSK_HURT; }
	
	@Override
	protected
	SoundEvent getDeathSound( ) { return SoundEvents.ENTITY_HUSK_DEATH; }
	
	@Override
	protected
	SoundEvent getStepSound( ) { return SoundEvents.ENTITY_HUSK_STEP; }
	
	// Overridden to modify attack effects.
	@Override
	protected
	void onTypeAttack( Entity target )
	{
		target.setFire( 10 );
	}
	
	@Nonnull
	@Override
	protected
	EntityArrow getArrow( float distanceFactor )
	{
		EntityArrow arrow = super.getArrow( distanceFactor );
		arrow.setFire( 100 );
		return arrow;
	}
	
	// Called when the entity is attacked.
	@Override
	public
	boolean attackEntityFrom( DamageSource damageSource, float damage )
	{
		if( damageSource.getImmediateSource( ) instanceof EntitySnowball ) {
			damage = Math.max( 2.0F, damage );
		}
		return super.attackEntityFrom( damageSource, damage );
	}
	
	// Returns true if this mob should be rendered on fire.
	@Override
	public
	boolean isBurning( ) { return isEntityAlive( ) && !isWet( ); }
}
