package net.dohaw.blackclover;

import net.dohaw.blackclover.config.PlayerDataConfig;
import net.dohaw.blackclover.grimmoire.Grimmoire;
import net.dohaw.blackclover.grimmoire.GrimmoireType;
import net.dohaw.blackclover.grimmoire.GrimmoireWrapper;
import net.dohaw.blackclover.playerdata.PlayerData;
import net.dohaw.blackclover.playerdata.PlayerDataManager;
import net.dohaw.blackclover.util.PDCHandler;
import net.dohaw.corelib.ResponderFactory;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class BlackCloverCommand implements CommandExecutor {

    private BlackCloverPlugin plugin;

    public BlackCloverCommand(BlackCloverPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        ResponderFactory rf = new ResponderFactory(sender, plugin.getPrefix());
        if(args.length > 0){

            if(args[0].equalsIgnoreCase("setgrim") && sender.hasPermission("blackclover.change.grimmoire")){

                String alias = args[1];
                String playerName = args[2];
                Player potentialPlayer = Bukkit.getPlayer(playerName);
                if(potentialPlayer != null){

                    GrimmoireWrapper wrapperFromAlias = Grimmoire.getByAlias(alias);
                    if(wrapperFromAlias != null){

                        PlayerDataManager pdm = plugin.getPlayerDataManager();
                        UUID potentialPlayerUUID = potentialPlayer.getUniqueId();
                        pdm.saveData(potentialPlayerUUID);

                        PlayerData playerData = plugin.getPlayerDataManager().getData(potentialPlayerUUID);
                        PlayerDataConfig dataConfig = playerData.getConfig();
                        dataConfig.getConfig().set("Grimmoire Type", wrapperFromAlias.getKEY().toString());
                        dataConfig.saveConfig();

                        plugin.removeRegenBar(potentialPlayer);
                        pdm.removeDataFromMemory(potentialPlayerUUID);

                        pdm.loadData(potentialPlayer);
                        playerData.setMaxRegen(plugin.getMaxRegen(wrapperFromAlias.getTier()));
                        playerData.setRegenAmount(0);

                        ItemStack grimmoire = PDCHandler.getGrimmoire(potentialPlayer);
                        if(grimmoire != null){
                            potentialPlayer.getInventory().remove(grimmoire);
                        }

                        ItemStack newGrimmoire = BlackCloverPlugin.getBaseGrimmoire();
                        wrapperFromAlias.adaptItemStack(newGrimmoire);

                        potentialPlayer.getInventory().setItemInOffHand(newGrimmoire);

                        String newGrimmoireName = wrapperFromAlias.getKEY().toString();
                        String grimmoireColorCode = wrapperFromAlias.getConfig().getDisplayNameColor();
                        if(sender instanceof Player){
                            rf.sendMessage("This player's grimmoire has been changed to " + grimmoireColorCode + newGrimmoireName);
                        }

                        ResponderFactory playerResponder = new ResponderFactory(potentialPlayer, plugin.getPrefix());
                        playerResponder.sendMessage("Your grimmoire has been set to " + grimmoireColorCode + newGrimmoireName);

                    }else{
                        rf.sendMessage("This is not a valid grimmoire alias!");
                    }

                }else{
                    rf.sendMessage("This is not a valid player!");
                }

            }else if(args[0].equalsIgnoreCase("listgrim") && sender.hasPermission("blackclover.list")){
                rf.sendMessage("&0Black&dClover&f Grimmoies:");
                Map<Enum, Wrapper> grimmoires = Grimmoire.wrappers;
                for(Enum key : grimmoires.keySet()){
                    if(key instanceof GrimmoireType){
                        GrimmoireType type = (GrimmoireType) key;
                        String formalName = StringUtils.capitalize(type.toString().toLowerCase());
                        sender.sendMessage(net.dohaw.corelib.StringUtils.colorString("> " + formalName));
                    }
                }
            }

        }



        return false;
    }

}
