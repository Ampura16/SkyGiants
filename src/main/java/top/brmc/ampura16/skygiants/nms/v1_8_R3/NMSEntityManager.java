package top.brmc.ampura16.skygiants.nms.v1_8_R3;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class NMSEntityManager {
    private static final NMSEntityManager INSTANCE = new NMSEntityManager();

    // 私有构造函数，防止实例化
    private NMSEntityManager() {}

    // 获取单例实例
    public static NMSEntityManager getInstance() {
        return INSTANCE;
    }

    // 创建巨人僵尸
    public boolean createGiantZombie(Location location) {
        if (location == null || location.getWorld() == null) {
            throw new IllegalArgumentException("Location or world cannot be null");
        }

        // 检查位置是否有效
        if (!isValidSpawnLocation(location)) {
            System.out.println("生成位置无效: " + location);
            return false;
        }

        CraftWorld world = (CraftWorld) location.getWorld();
        EntityGiantZombie nmsGiant = new EntityGiantZombie(world.getHandle());
        nmsGiant.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        // 配置巨人僵尸
        GiantConfigurator.configureGiantZombie(nmsGiant);

        // 将巨人加入到世界中
        boolean added = world.getHandle().addEntity(nmsGiant, CreatureSpawnEvent.SpawnReason.CUSTOM);

        // 强制同步实体到客户端
        if (added) {
            syncEntityToClients(nmsGiant, world.getHandle());
            System.out.println("巨人僵尸已生成，位置: " + location);
        } else {
            System.out.println("巨人僵尸生成失败，位置: " + location);
        }

        return added;
    }

    // 强制同步实体到客户端
    private void syncEntityToClients(EntityGiantZombie nmsGiant, World nmsWorld) {
        for (EntityHuman human : nmsWorld.players) {
            if (human instanceof EntityPlayer) {
                ((EntityPlayer) human).playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(nmsGiant));
            }
        }
    }

    // 检查生成位置是否有效
    private boolean isValidSpawnLocation(Location location) {
        // 检查位置是否在世界的边界内
        if (location.getY() < 0 || location.getY() > 255) {
            return false;
        }

        // 检查位置是否在空气方块中
        return location.getBlock().isEmpty() && location.clone().add(0, 1, 0).getBlock().isEmpty();
    }
}
