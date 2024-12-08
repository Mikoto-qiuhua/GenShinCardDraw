package org.qiuhua.genshincarddraw.database;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.qiuhua.genshincarddraw.Main;
import org.qiuhua.genshincarddraw.config.Config;
import org.qiuhua.genshincarddraw.playerdata.PlayerData;
import org.qiuhua.genshincarddraw.playerdata.PlayerDataManager;
import org.qiuhua.genshincarddraw.prizepool.PrizePoolManager;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SqlDataControl {

    //存储指定玩家的全部数据
    public static void savePlayerData(Player player){
        UUID uuid = player.getUniqueId();
        //如果玩家有数据
        if(PlayerDataManager.isPlayerData(uuid)){
            SqlControl.insert(uuid);
        }
    }
    public static void savePlayerData(UUID uuid){
        //如果玩家有数据
        if(PlayerDataManager.isPlayerData(uuid)){
            SqlControl.insert(uuid);
        }
    }

    //加载指定玩家的全部数据
    public static void loadPlayerData(Player player){
        UUID uuid = player.getUniqueId();
        SqlControl.loadData(uuid);
    }

    public static void autoSave() {
        int autoTime = Config.getConfig().getInt("Database.autoSave");
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                //定时查表防止连接超时
                SqlControl.keepConnectionAlive();
                //存储全部缓存的玩家数据
                ConcurrentHashMap<UUID, PlayerData> allMaster = PlayerDataManager.getAllPlayerData();
                for(UUID uuid : allMaster.keySet()){
                    SqlDataControl.savePlayerData(uuid);
                }
                Main.getMainPlugin().getLogger().info("保存完成");
            }
        }, (autoTime * 60L) * 20L,   (autoTime * 60L) * 20L);
    }



}
