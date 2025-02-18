package net.dohaw.blackclover.grimmoire.spell.type.gravity;

import net.dohaw.blackclover.config.GrimmoireConfig;
import net.dohaw.blackclover.grimmoire.Grimmoire;
import net.dohaw.blackclover.grimmoire.spell.CastSpellWrapper;
import net.dohaw.blackclover.grimmoire.spell.SpellType;
import net.dohaw.blackclover.playerdata.PlayerData;
import net.dohaw.blackclover.util.SpellUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Levitate extends CastSpellWrapper {

    private int castDistance;
    private double duration;

    public Levitate(GrimmoireConfig grimmoireConfig) {
        super(SpellType.LEVITATE, grimmoireConfig);
    }

    @Override
    public boolean cast(Event e, PlayerData pd) {
        Player player = pd.getPlayer();
        if(!player.isSneaking()){
            giveEffects(player);
            return true;
        }else{
            Entity targetEntity = SpellUtils.getEntityInLineOfSight(e, player, castDistance);
            if(SpellUtils.isTargetValid(player, targetEntity)){
                assert targetEntity != null;
                giveEffects((LivingEntity) targetEntity);
                return true;
            }
        }

        return false;
    }

    @Override
    public void prepareShutdown() { }

    private void giveEffects(LivingEntity le){

        le.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, (int) (duration * 20), 0));
        SpellUtils.playSound(le, Sound.ENTITY_ENDER_DRAGON_FLAP);
        SpellUtils.spawnParticle(le, Particle.END_ROD, 30, 1, 1, 1);

        Bukkit.getScheduler().runTaskLater(Grimmoire.instance, () -> {
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, (int) (duration * 20), 0));
        }, (long) (duration * 20));

    }

    @Override
    public void loadSettings() {
        super.loadSettings();
        this.duration = grimmoireConfig.getDoubleSetting(KEY, "Duration");
        this.castDistance = grimmoireConfig.getIntegerSetting(KEY, "Cast Distance");
    }

}
