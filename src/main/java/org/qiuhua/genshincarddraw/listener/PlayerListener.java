package org.qiuhua.genshincarddraw.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.qiuhua.genshincarddraw.Main;
import org.qiuhua.genshincarddraw.config.Config;
import org.qiuhua.genshincarddraw.database.SqlDataControl;
import org.qiuhua.genshincarddraw.playerdata.PlayerData;
import org.qiuhua.genshincarddraw.playerdata.PlayerDataManager;

public class PlayerListener implements Listener {

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, Main.getMainPlugin());
    }

    //玩家进服事件
    @EventHandler
    public void onPlayerJoinEvent (PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(!PlayerDataManager.isPlayerData(player.getUniqueId())){
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getMainPlugin(), new Runnable() {
                @Override
                public void run() {
                    if (Bukkit.getPlayer(player.getUniqueId()) != null){
                        //在数据库读取数据
                        SqlDataControl.loadPlayerData(player);
                        PlayerData playerData = PlayerDataManager.getPlayerData(player);
                        if(!playerData.getCacheCard().isEmpty()){
                            playerData.cacheCardResult(player);
                        }
                    }
                }
            }, Config.getConfig().getInt("LoadDataDelay"));
            return;

        }
        PlayerData playerData = PlayerDataManager.getPlayerData(player);
        if(!playerData.getCacheCard().isEmpty()){
            playerData.cacheCardResult(player);
        }

    }


    //玩家退出游戏事件
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        Player player = event.getPlayer();
        //是mysql退出就存储数据 并且移除相关的数据缓存
        if(Config.getConfig().getString("Database.type").equalsIgnoreCase("mysql")){
            SqlDataControl.savePlayerData(player);
            PlayerDataManager.removePlayerData(player.getUniqueId());
        }
    }

}
