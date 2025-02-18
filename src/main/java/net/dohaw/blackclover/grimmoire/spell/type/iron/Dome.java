package net.dohaw.blackclover.grimmoire.spell.type.iron;

import net.dohaw.blackclover.config.GrimmoireConfig;
import net.dohaw.blackclover.exception.UnexpectedPlayerData;
import net.dohaw.blackclover.grimmoire.Grimmoire;
import net.dohaw.blackclover.grimmoire.spell.CastSpellWrapper;
import net.dohaw.blackclover.grimmoire.spell.DependableSpell;
import net.dohaw.blackclover.grimmoire.spell.SpellType;
import net.dohaw.blackclover.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Dome extends CastSpellWrapper implements DependableSpell, Listener {

    public Dome(GrimmoireConfig grimmoireConfig) {
        super(SpellType.DOME, grimmoireConfig);
    }

    ArrayList<Material> replaceableBlocks = new ArrayList<Material>(Arrays.asList(new Material[]{
            Material.GRASS}));

    @Override
    public boolean cast(Event e, PlayerData pd) throws UnexpectedPlayerData {
        Player p = pd.getPlayer();
        List<Location> sphere = getSphere(p.getLocation(), 4, true);
        sphere.get(86).add(0, 1, 0);
        Location light = sphere.get(86);
        Block blight = p.getWorld().getBlockAt(light.getBlockX(), light.getBlockY(), light.getBlockZ());
        HashMap<Location, Material> prevBlocks = new HashMap<>();
        for (Location loc : sphere) {
            Block b = p.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
            if (b.getType() == Material.AIR || replaceableBlocks.contains(b.getType())) {
                prevBlocks.put(b.getLocation(), b.getType());
                b.setType(Material.GLASS);
            }
        }
        p.playSound(light, Sound.BLOCK_GLASS_HIT, 10, 10);
        Bukkit.getServer().getScheduler().runTaskLater(Grimmoire.instance, () -> {
            for (Location loc : prevBlocks.keySet()) {
                Block b = p.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                b.setType(prevBlocks.get(loc));
            }
            p.playSound(light, Sound.BLOCK_GLASS_BREAK, 10, 10);
        }, (20L) * 30);
        return false;
    }

    public static List<Location> getSphere(Location centerBlock, int radius, boolean hollow) {
        if (centerBlock == null) {
            return new ArrayList<>();
        }
        List<Location> circleBlocks = new ArrayList<Location>();
        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();
        for(int x = bx - radius; x <= bx + radius; x++) {
            for(int y = by - radius; y <= by + radius; y++) {
                for(int z = bz - radius; z <= bz + radius; z++) {

                    double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));

                    if(distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {

                        Location l = new Location(centerBlock.getWorld(), x, y, z);

                        circleBlocks.add(l);

                    }

                }
            }
        }
        return circleBlocks;
    }

    @Override
    public void initDependableData() {}

    @Override
    public void prepareShutdown() {}
}
