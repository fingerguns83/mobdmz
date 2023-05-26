package net.fg83.mobdmz;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MobDMZ extends JavaPlugin implements Listener, CommandExecutor {

    MobDMZ plugin = this;

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
            if (location.getBlock().getType().equals(Material.NETHER_PORTAL)) {
                if (getConfig().getBoolean("disable_portal_spawns")) {
                    if (!newSpawn.getEntityType().equals(EntityType.ZOMBIFIED_PIGLIN)) {
                        newSpawn.setCancelled(true);
                        return;
                    }
                    if (!getConfig().getBoolean("allow_ziglin_portal_spawns")) {
                        newSpawn.setCancelled(true);
                        return;
                    }
                }
            }
            if (getConfig().getConfigurationSection(mobName) == null){
                getLogger().info("Config for " + mobName + "is missing!");
            }
            else {
                boolean hasDMZ = getConfig().getBoolean(mobName + ".dmz");
                if (hasDMZ){
                    DMZ dmz = new DMZ(plugin, mobName);
                    if (dmz.contains(location)){
                        newSpawn.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityBlockChange(EntityChangeBlockEvent event){
        if (event.getEntity() instanceof Enderman) {
            if (getConfig().getBoolean("prevent_enderman_grief.enabled")){
                if (getConfig().getBoolean("prevent_enderman_grief.global")) {
                    event.setCancelled(true);
                }
                else {
                    Location location = event.getBlock().getLocation();
                    DMZ dmz = new DMZ(plugin, "prevent_enderman_grief");
                    if (dmz.contains(location)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event){
        if (event.getEntity() instanceof EnderDragon){
            if (getConfig().getBoolean("prevent_dragon_grief.enabled")) {
                if (getConfig().getBoolean("prevent_dragon_grief.enabled")){
                    if (getConfig().getBoolean("prevent_dragon_grief.global")){
                        event.blockList().clear();
                        return;
                    }
                    else {
                        DMZ dmz = new DMZ(plugin, "prevent_dragon_grief");
                        if (dmz.contains(event.getLocation())) {
                            event.blockList().clear();
                            return;
                        }
                    }
                }
            }
        }
        if (event.getEntity() instanceof Creeper){
            if (getConfig().getBoolean("prevent_creeper_grief.enabled")){
                if (getConfig().getBoolean("prevent_creeper_grief.global")){
                    event.blockList().clear();
                    return;
                }
                else {
                    DMZ dmz = new DMZ(plugin, "prevent_creeper_grief");
                    if (dmz.contains(event.getLocation())) {
                        event.blockList().clear();
                        return;
                    }
                }
            }
        }
        if (event.getEntity() instanceof Fireball && !(event.getEntity() instanceof WitherSkull)){
            if (getConfig().getBoolean("prevent_ghast_grief.enabled")){
                if (getConfig().getBoolean("prevent_ghast_grief.global")){
                    event.blockList().clear();
                    return;
                }
                else {
                    DMZ dmz = new DMZ(plugin, "prevent_ghast_grief");
                    if (dmz.contains(event.getLocation())) {
                        event.blockList().clear();
                        return;
                    }
                }
            }
        }
        if (event.getEntity() instanceof WitherSkull){
            if (getConfig().getBoolean("prevent_wither_grief.enabled")){
                if (getConfig().getBoolean("prevent_wither_grief.global")){
                    event.blockList().clear();
                }
                else {
                    Location location = event.getLocation();
                    DMZ dmz = new DMZ(plugin, "prevent_wither_grief");
                    if (dmz.contains(event.getLocation())){
                        event.blockList().clear();
                    }
                }
            }
        }
    }
}
