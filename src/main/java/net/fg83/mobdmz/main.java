package net.fg83.mobdmz;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class main extends JavaPlugin implements Listener {
    public class DMZ {
        public String level;
        public Integer minX;
        public Integer minZ;
        public Integer maxX;
        public Integer maxZ;
        public DMZ(String level, Integer x1, Integer z1, Integer x2, Integer z2){
            this.level = level;
            this.minX = x1;
            this.minZ = z1;
            this.maxX = x2;
            this.maxZ = z2;
        }
        public Boolean contains(String level, Double queryX, Double queryZ){
            if (queryX >= this.minX && queryX <= this.maxX && queryZ >= this.minZ && queryZ <= this.maxZ){
                if (level.contains(this.level)){
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }

        public String stringify(){
            String min = " (" + this.minX.toString() + ", " + this.minZ.toString() + ") to";
            String max = " (" + this.maxX.toString() + ", " + this.maxZ.toString() + ")";
            return this.level + min + max;
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent newSpawn){
        if (newSpawn.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) {
            String mobName = newSpawn.getEntityType().toString();
            Location location = newSpawn.getLocation();
            if (location.getBlock().getType().equals(Material.NETHER_PORTAL)){
                if (getConfig().getBoolean("disable_portal_spawns")){
                    newSpawn.setCancelled(true);
                }
            }
            if (getConfig().getConfigurationSection(mobName) == null){
                getLogger().info("No config defined for " + mobName + "!");
            }
            else {
                boolean hasDMZ = getConfig().getBoolean(mobName + ".dmz");
                if (hasDMZ){
                    for (String key : getConfig().getConfigurationSection(mobName + ".blocked-areas").getKeys(false)){
                        if (
                            getConfig().getString(mobName + ".blocked-areas." + key + ".level") == null ||
                            getConfig().getString(mobName + ".blocked-areas." + key + ".minX") == null ||
                            getConfig().getString(mobName + ".blocked-areas." + key + ".minZ") == null ||
                            getConfig().getString(mobName + ".blocked-areas." + key + ".maxX") == null ||
                            getConfig().getString(mobName + ".blocked-areas." + key + ".maxZ") == null
                        ){
                            getLogger().info("No DMZ defined for " + mobName + "!");
                            break;
                        }
                        DMZ dmz = new DMZ(
                                getConfig().get(mobName + ".blocked-areas." + key + ".level").toString().toLowerCase(),
                                getConfig().getInt(mobName + ".blocked-areas." + key + ".minX"),
                                getConfig().getInt(mobName + ".blocked-areas." + key + ".minZ"),
                                getConfig().getInt(mobName + ".blocked-areas." + key + ".maxX"),
                                getConfig().getInt(mobName + ".blocked-areas." + key + ".maxZ")
                        );
                        if (dmz.contains(location.getWorld().getName().toLowerCase(), location.getX(), location.getZ())){
                            newSpawn.setCancelled(true);
                            break;
                        }
                    }
                }
            }
        }
    }
}
