package top.brmc.ampura16.skygiants.nms.v1_8_R3;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Giant;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class GiantFactory {

    public static Giant createCustomGiant(Location location) {
        CraftWorld world = (CraftWorld) location.getWorld();
        EntityGiantZombie nmsGiant = new EntityGiantZombie(world.getHandle());
        nmsGiant.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        Giant giant = (Giant) nmsGiant.getBukkitEntity();

        // 配置巨人僵尸
        GiantConfigurator.configureGiantZombie(nmsGiant);

        // 将巨人加入到世界中
        world.getHandle().addEntity(nmsGiant, CreatureSpawnEvent.SpawnReason.CUSTOM);

        return giant;
    }
}
