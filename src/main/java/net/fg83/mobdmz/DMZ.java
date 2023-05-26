package net.fg83.mobdmz;

import org.bukkit.Location;

public class DMZ {
    public MobDMZ plugin;
    public String configKey;

    public DMZ(MobDMZ plugin, String configKey){
        this.plugin = plugin;
        this.configKey = configKey;
    }
    public Boolean contains(Location location) throws NullPointerException {
        try {
            for (String key : plugin.getConfig().getConfigurationSection(this.configKey + ".blocked-areas").getKeys(false)) {
                if (plugin.getConfig().getString(this.configKey + ".blocked-areas." + key + ".level") == null ||
                        plugin.getConfig().getString(this.configKey + ".blocked-areas." + key + ".X1") == null ||
                        plugin.getConfig().getString(this.configKey + ".blocked-areas." + key + ".Y1") == null ||
                        plugin.getConfig().getString(this.configKey + ".blocked-areas." + key + ".Z1") == null ||
                        plugin.getConfig().getString(this.configKey + ".blocked-areas." + key + ".X2") == null ||
                        plugin.getConfig().getString(this.configKey + ".blocked-areas." + key + ".Y2") == null ||
                        plugin.getConfig().getString(this.configKey + ".blocked-areas." + key + ".Z2") == null
                ) {
                    plugin.getLogger().info("Malformed DMZ for " + configKey + "!");
                    break;
                }
                String configLevel = plugin.getConfig().get(this.configKey + ".blocked-areas." + key + ".level").toString().toLowerCase();
                int x1 = plugin.getConfig().getInt(this.configKey + ".blocked-areas." + key + ".X1");
                int y1 = plugin.getConfig().getInt(this.configKey + ".blocked-areas." + key + ".Y1");
                int z1 = plugin.getConfig().getInt(this.configKey + ".blocked-areas." + key + ".Z1");
                int x2 = plugin.getConfig().getInt(this.configKey + ".blocked-areas." + key + ".X2");
                int y2 = plugin.getConfig().getInt(this.configKey + ".blocked-areas." + key + ".Y2");
                int z2 = plugin.getConfig().getInt(this.configKey + ".blocked-areas." + key + ".Z2");

                int minX;
                int minY;
                int minZ;
                int maxX;
                int maxY;
                int maxZ;

                if (x1 < x2) {
                    minX = x1;
                    maxX = x2;
                } else {
                    minX = x2;
                    maxX = x1;
                }
                if (z1 < z2) {
                    minZ = z1;
                    maxZ = z2;
                } else {
                    minZ = z2;
                    maxZ = z1;
                }
                if (y1 < y2) {
                    minY = y1;
                    maxY = y2;
                } else {
                    minY = y2;
                    maxY = y1;
                }
                if (location.getX() >= minX && location.getX() <= maxX + 0.5 && location.getY() >= minY && location.getY() <= maxY && location.getZ() >= minZ && location.getZ() <= maxZ + 0.5) {
                    if (configLevel.contains(location.getWorld().getName().toLowerCase())) {
                        return true;
                    }
                }
            }
            return false;
        }
        catch (NullPointerException e){
            plugin.getLogger().info("Caught NullPointerException. Something is wrong with your config!");
            return false;
        }
    }
}
