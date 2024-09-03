package net.fg83.mobdmz;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class DMZ {
    private String level;
    private World world;
    private double factor = 0;

    private int x1 = 0;
    private int x2 = 0;
    private int y1 = 0;
    private int y2 = 0;
    private int z1 = 0;
    private int z2 = 0;

    public DMZ(String level, double factor, int x1, int x2, int y1, int y2, int z1, int z2){
        this.level = level;
        this.factor = factor;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.z1 = z1;
        this.z2 = z2;

        this.bindWorld();
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public double getFactor() {
        return this.factor;
    }
    public void setFactor(double factor) {
        this.factor = factor;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }
    public void setX2(int x2) {
        this.x2 = x2;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }
    public void setY2(int y2) {
        this.y2 = y2;
    }

    public void setZ1(int z1) {
        this.z1 = z1;
    }
    public void setZ2(int z2) {
        this.z2 = z2;
    }

    public void bindWorld(){
        this.world = Bukkit.getServer().getWorld(this.level);
    }

    public boolean contains(Location location){
        int minX;
        int minY;
        int minZ;
        int maxX;
        int maxY;
        int maxZ;

        if (this.x1 < this.x2) {
            minX = this.x1;
            maxX = this.x2;
        } else {
            minX = this.x2;
            maxX = this.x1;
        }
        if (this.z1 < this.z2) {
            minZ = this.z1;
            maxZ = this.z2;
        } else {
            minZ = this.z2;
            maxZ = this.z1;
        }
        if (this.y1 < this.y2) {
            minY = this.y1;
            maxY = this.y2;
        } else {
            minY = this.y2;
            maxY = this.y1;
        }

        if (location.getX() >= minX && location.getX() <= maxX + 0.5 && location.getY() >= minY && location.getY() <= maxY && location.getZ() >= minZ && location.getZ() <= maxZ + 0.5) {
            return this.world.equals(location.getWorld());
        }
        return false;
    }
}
