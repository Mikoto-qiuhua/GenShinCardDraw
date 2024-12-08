package org.qiuhua.genshincarddraw.unrealguipro;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.qiuhua.genshincarddraw.card.CardDataManager;
import org.qiuhua.genshincarddraw.playerdata.PlayerData;
import org.qiuhua.genshincarddraw.playerdata.PlayerDataManager;

import java.util.List;

public class InventoryTool {
    public static boolean isCheckoutInventoryHolder (InventoryHolder holder) {
        return holder instanceof CheckoutInventoryHolder;
    }
    public static boolean isCheckoutInventoryHolder (Inventory inventory) {
        return inventory.getHolder() instanceof CheckoutInventoryHolder;
    }


    //加载界面物品数据
    public static void loadGuiItem(Inventory inventory, Player player){
        PlayerData playerData = PlayerDataManager.getPlayerData(player);
        List<String> cache = playerData.getCacheCard();
        int slot = 0;
        for(String cardId : cache){
            inventory.setItem(slot, CardDataManager.spawnItem(cardId));
            slot ++;
        }
    }
}
