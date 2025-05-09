package top.brmc.ampura16.skygiants.nms.v1_8_R3;

import net.minecraft.server.v1_8_R3.EntityGiantZombie;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;

public class CustomGiantPathfinderGoalMeleeAttack extends PathfinderGoalMeleeAttack {

    private final EntityGiantZombie giant;
    private final double followRange;

    public CustomGiantPathfinderGoalMeleeAttack(EntityGiantZombie giant, Class<? extends EntityLiving> targetClass, double speed, boolean longMemory) {
        super(giant, targetClass, speed, longMemory);
        this.giant = giant;
        this.followRange = 5.0D; // 设置攻击距离为 5.0D
    }

    @Override
    protected double a(EntityLiving target) {
        return this.followRange;
    }

    public EntityGiantZombie getGiant() {
        return giant;
    }
}
