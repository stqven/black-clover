package net.dohaw.blackclover.runnable.particle;

import net.dohaw.blackclover.util.SpellUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Draws particles from one location to another.
 */
public class LineParticleRunner extends BukkitRunnable {

    protected Location start, end;
    protected Particle.DustOptions dustOptions;
    protected double spread, count;

    public LineParticleRunner(Location start, Location end, Particle.DustOptions dustOptions, double spread){
        this.start = start;
        this.end = end;
        this.dustOptions = dustOptions;
        this.spread = spread;
    }

    /**
     * This does nothing since the locations are static in this case.
     */
    public void updateLocations(){}

    protected Location getParticleLocation(){
        Vector dir = end.clone().subtract(start).toVector();
        Vector currentDir = dir.clone().multiply(count);
        return start.clone().add(currentDir);
    }

    /**
     * Draws the line.
     */
    public void drawLine(){
        Location particleLoc = getParticleLocation();
        boolean isWithinABlock = isCloseToEnd(particleLoc);
        while(!isWithinABlock){
            SpellUtils.spawnParticle(particleLoc, Particle.REDSTONE, dustOptions, 30, 0, 0, 0);
            count += spread;
            particleLoc = getParticleLocation();
            isWithinABlock = isCloseToEnd(particleLoc);
        }
        count = 0;
    }

    @Override
    public void run() {
        updateLocations();
        drawLine();
    }

    protected boolean isCloseToEnd(Location particleLoc){
        return particleLoc.distance(end) <= 1;
    }

}
