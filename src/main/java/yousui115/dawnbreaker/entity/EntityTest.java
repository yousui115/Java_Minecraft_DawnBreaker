package yousui115.dawnbreaker.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityTest extends EntityLiving
{

    public EntityTest(World worldIn) {
        super(worldIn);
        // TODO 自動生成されたコンストラクター・スタブ
    }

    @Override
    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
        // TODO 自動生成されたメソッド・スタブ
        super.dropLoot(wasRecentlyHit, lootingModifier, source);
    }
}
