package org.qiuhua.genshincarddraw.playerdata;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {
    private static final ConcurrentHashMap<UUID, PlayerData> allMaster = new ConcurrentHashMap<>();

    //获取玩家数据
    public static PlayerData getPlayerData(Player player){
        UUID uuid = player.getUniqueId();
        if(!allMaster.containsKey(uuid)){
            allMaster.put(uuid, new PlayerData());
        }
        return allMaster.get(uuid);
    }

    //获取玩家数据
    public static PlayerData getPlayerData(UUID uuid){
        if(!allMaster.containsKey(uuid)){
            allMaster.put(uuid, new PlayerData());
        }
        return allMaster.get(uuid);
    }

    public static Boolean isPlayerData(UUID uuid){
        return allMaster.containsKey(uuid);
    }

    //获取全部数据
    public static ConcurrentHashMap<UUID, PlayerData> getAllPlayerData(){
        return allMaster;
    }


    public static void removePlayerData(UUID uuid){
        allMaster.remove(uuid);
    }
}
