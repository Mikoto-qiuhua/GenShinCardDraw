package org.qiuhua.genshincarddraw.vault;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerPointsHook {


    private static PlayerPointsAPI playerPointsAPI = null;

    public static void setupPlayerPoints() {
        if (Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints") == null) {
            return;
        }
        playerPointsAPI = PlayerPoints.getInstance().getAPI();
    }

    public static Boolean deductMoney(Player player, Double money){
        return playerPointsAPI.take(player.getUniqueId(), money.intValue());

    }


}
