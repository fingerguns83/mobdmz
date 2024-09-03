package net.fg83.mobdmz;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MobDMZ extends JavaPlugin implements Listener, CommandExecutor {

    MobDMZ plugin = this;

    boolean debugMode;

    FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.config = getConfig();

        File configDirectory = getDataFolder();
        File levelsFile = new File(configDirectory, "available-levels.txt");
        try (FileWriter writer = new FileWriter(levelsFile)) {
            for (World world : this.getServer().getWorlds()) {
                writer.write(world.getName().toLowerCase().trim() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (config.contains("fingy-debug") && config.getBoolean("fingy-debug")) {
            this.debugMode = true;
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event){
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) {
            String mobName = event.getEntityType().toString().trim();
            Location location = event.getLocation();

            if (location.getBlock().getType().equals(Material.NETHER_PORTAL)) {
                if (config.getBoolean("disable_portal_spawns")) {
                    if (!event.getEntityType().equals(EntityType.ZOMBIFIED_PIGLIN)) {
                        event.setCancelled(true);
                        return;
                    }
                    if (!config.getBoolean("allow_ziglin_portal_spawns")) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            if (config.contains(mobName)){
                if (checkDMZ(location, mobName)){
                    if (debugMode){
                        getServer().getLogger().info("Blocked " + mobName + " Spawn: " + "[" + Objects.requireNonNull(location.getWorld()).getName() + "] (" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ")");
                    }
                    event.setCancelled(true);
                }
                else {
                    if (debugMode){
                        getServer().getLogger().info("Allowed " + mobName + " Spawn: " + "[" + Objects.requireNonNull(location.getWorld()).getName() + "] (" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ")");
                    }
                }
            }

        }
    }

    @EventHandler
    public void onEntityBlockChange(EntityChangeBlockEvent event){
        if (event.getEntity() instanceof Enderman) {
            if (config.getBoolean("prevent_enderman_grief.enabled")){
                if (config.getBoolean("prevent_enderman_grief.global")) {
                    event.setCancelled(true);
                }
                else {
                    Location location = event.getBlock().getLocation();
                    String configKey = "prevent_enderman_grief.blocked-areas";

                    if (config.contains(configKey)){
                        event.setCancelled(checkDMZ(location, configKey));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event){
        String enabledKey = "";
        String globalKey = "";
        String areasKey = "";

        switch (event.getEntity().getType()){
            case ENDER_DRAGON:
                enabledKey = "prevent_dragon_grief.enabled";
                globalKey = "prevent_dragon_grief.global";
                areasKey = "prevent_dragon_grief.blocked-areas";
                break;
            case CREEPER:
                enabledKey = "prevent_creeper_grief.enabled";
                globalKey = "prevent_creeper_grief.global";
                areasKey = "prevent_creeper_grief.blocked-areas";
                break;
            case WITHER_SKULL:
                enabledKey = "prevent_wither_grief.enabled";
                globalKey = "prevent_wither_grief.global";
                areasKey = "prevent_wither_grief.blocked-areas";
                break;
            case FIREBALL:
                enabledKey = "prevent_ghast_grief.enabled";
                globalKey = "prevent_ghast_grief.global";
                areasKey = "prevent_ghast_grief.blocked-areas";
                break;
        }

        if (enabledKey.isEmpty() || globalKey.isEmpty() || areasKey.isEmpty()){
            return;
        }

        if (!config.contains(enabledKey) && !config.contains(globalKey) && !config.contains(areasKey)){
            return;
        }

        if (!config.getBoolean(enabledKey)){
            return;
        }

        if (config.getBoolean(globalKey)){
            event.blockList().clear();
            return;
        }

        if (checkDMZ(event.getLocation(), areasKey)){
            event.blockList().clear();
        }
    }

    public boolean checkDMZ(Location eventLocation, String configKey){
        if (config.contains(configKey)){
            ConfigurationSection section = config.getConfigurationSection(configKey);
            assert section != null;
            Set<String> keys = section.getKeys(false);
            AtomicBoolean blockEvent = new AtomicBoolean(false);
            if (!keys.isEmpty()){
                keys.forEach((key) -> {
                    ConfigurationSection dmzSection = section.getConfigurationSection(key);
                    assert dmzSection != null;

                    if (dmzSection.contains("level") && dmzSection.contains("x1") && dmzSection.contains("y1") && dmzSection.contains("z1") && dmzSection.contains("x2") && dmzSection.contains("y2") && dmzSection.contains("z2")){
                        double factor = 0;
                        if (dmzSection.contains("factor")){
                            factor = dmzSection.getDouble("factor");
                        }

                        DMZ dmz = new DMZ(
                                dmzSection.getString("level"),
                                factor,
                                dmzSection.getInt("x1"),
                                dmzSection.getInt("x2"),
                                dmzSection.getInt("y1"),
                                dmzSection.getInt("y2"),
                                dmzSection.getInt("z1"),
                                dmzSection.getInt("z2")
                        );

                        if (dmz.contains(eventLocation)){
                            if (Math.random() > dmz.getFactor()){
                                blockEvent.set(true);
                            }
                        }
                    }
                });
            }
            return blockEvent.get();
        }
        return false;
    }
}
