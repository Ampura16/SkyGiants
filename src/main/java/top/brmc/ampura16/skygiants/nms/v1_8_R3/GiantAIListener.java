package top.brmc.ampura16.skygiants.nms.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftGiant;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class GiantAIListener implements Listener {

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.GIANT) {
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
                CraftGiant craftGiant = (CraftGiant) event.getEntity();
                GiantConfigurator.configureGiantZombie(craftGiant.getHandle());
            }
        }
    }
}
