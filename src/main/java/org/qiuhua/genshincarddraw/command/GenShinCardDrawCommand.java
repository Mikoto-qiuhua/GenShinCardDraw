package org.qiuhua.genshincarddraw.command;

import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.display.been.module.display.TextModuleData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.qiuhua.genshincarddraw.Main;
import org.qiuhua.genshincarddraw.config.Config;
import org.qiuhua.genshincarddraw.prizepool.PrizePoolManager;
import org.qiuhua.genshincarddraw.unrealguipro.GuiManager;


import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GenShinCardDrawCommand implements CommandExecutor, TabExecutor {

    public void register() {
        Bukkit.getPluginCommand("GenShinCardDraw").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(args.length == 3 && args[0].equalsIgnoreCase("open")){
            Player player = Bukkit.getPlayer(args[1]);
            if(player == null){
                return true;
            }
            String prizePoolId = args[2];
            if(prizePoolId == null || prizePoolId.equals("")){
                return true;
            }
            if(sender.hasPermission("GenShinCardDraw.open." + prizePoolId)){
                GuiManager.openPrizePool(player, prizePoolId);
            }
            return true;
        }
        if(args.length == 3 && args[0].equalsIgnoreCase("record")){
            Player player = Bukkit.getPlayer(args[1]);
            if(player == null){
                return true;
            }
            String prizePoolId = args[2];
            if(prizePoolId == null || prizePoolId.equals("")){
                return true;
            }
            if(sender.hasPermission("GenShinCardDraw.record." + prizePoolId)){
                GuiManager.openRecordGui(player, prizePoolId);
            }
            return true;
        }
        if(sender.hasPermission("GenShinCardDraw.reload") && args.length == 1 && args[0].equalsIgnoreCase("reload")){
            Main.getMainPlugin().reloadConfig();
            if(sender instanceof Player){
                sender.sendMessage("[GenShinCardDraw]文件已全部重新加载");
            }else {
                Main.getMainPlugin().getLogger().info("文件已全部重新加载");
            }
        }
        return true;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            List<String> result = new ArrayList<>();
            //当参数长度是1时
            if(args.length == 1){
                if (player.hasPermission("GenShinCardDraw.reload")){
                    result.add("reload");
                }
                result.add("open");
                result.add("record");
                return result;
            }
            //当参数长度是3
            if(args.length == 3){
                for(String prizePoolId : PrizePoolManager.getPrizePoolMap().keySet()){
                    if (player.hasPermission("GenShinCardDraw.open." + prizePoolId) || player.hasPermission("GenShinCardDraw.record." + prizePoolId)){
                        result.add(prizePoolId);
                    }
                }
                return result;
            }
        }


        return null;
    }

}
