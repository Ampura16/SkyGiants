package top.brmc.ampura16.skygiants.nms.v1_8_R3;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.entity.Giant;
import org.bukkit.inventory.ItemStack;

public class GiantConfigurator {

    public static void configureGiantZombie(EntityGiantZombie nmsGiant) {
        // 清除默认的 AI 目标
        nmsGiant.goalSelector = new PathfinderGoalSelector(nmsGiant.world != null ? nmsGiant.world.methodProfiler : null);
        nmsGiant.targetSelector = new PathfinderGoalSelector(nmsGiant.world != null ? nmsGiant.world.methodProfiler : null);

        // 设置 AI 目标
        nmsGiant.goalSelector.a(0, new PathfinderGoalFloat(nmsGiant)); // 游泳
        nmsGiant.goalSelector.a(2, new CustomGiantPathfinderGoalMeleeAttack(nmsGiant, EntityHuman.class, 1.3D, false)); // 近战攻击
        nmsGiant.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(nmsGiant, 1.0D)); // 向限制区域移动
        nmsGiant.goalSelector.a(7, new PathfinderGoalRandomStroll(nmsGiant, 1.0D)); // 随机游荡
        nmsGiant.goalSelector.a(8, new PathfinderGoalLookAtPlayer(nmsGiant, EntityHuman.class, 8.0F)); // 观察玩家
        nmsGiant.goalSelector.a(8, new PathfinderGoalRandomLookaround(nmsGiant)); // 随机环顾四周

        // 设置目标选择器
        nmsGiant.targetSelector.a(1, new PathfinderGoalHurtByTarget(nmsGiant, true)); // 被攻击时反击
        nmsGiant.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(nmsGiant, EntityHuman.class, true)); // 攻击玩家

        // 设置移动速度
        AttributeInstance speedAttribute = nmsGiant.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.setValue(0.23);
        }

        // 设置装备
        Giant giant = (Giant) nmsGiant.getBukkitEntity();
        giant.getEquipment().setItemInHand(new ItemStack(Material.DIAMOND_SWORD)); // 主手装备钻石剑
        giant.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET)); // 头部装备铁头盔
        giant.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE)); // 胸部装备铁胸甲
        giant.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS)); // 腿部装备铁护腿
        giant.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS)); // 脚部装备铁靴

        // 设置攻击力
        AttributeInstance damageAttribute = nmsGiant.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE);
        if (damageAttribute != null) {
            damageAttribute.setValue(7.0);
        }
    }
}
