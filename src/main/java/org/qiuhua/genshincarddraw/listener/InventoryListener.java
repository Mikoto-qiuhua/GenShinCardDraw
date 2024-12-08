package org.qiuhua.genshincarddraw.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.qiuhua.UnrealGUIPro.api.UnrealGUIProApi;
import org.qiuhua.genshincarddraw.Main;
import org.qiuhua.genshincarddraw.config.Config;
import org.qiuhua.genshincarddraw.playerdata.PlayerData;
import org.qiuhua.genshincarddraw.playerdata.PlayerDataManager;
import org.qiuhua.genshincarddraw.unrealguipro.InventoryTool;

public class InventoryListener implements Listener {


    public void register() {
        Bukkit.getPluginManager().registerEvents(this, Main.getMainPlugin());
    }



    //当玩家点击物品栏中的格子时触发事件事件
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event){
        Inventory inv = event.getInventory();
        //界面判断
        if(InventoryTool.isCheckoutInventoryHolder(inv)){
            event.setCancelled(true);
        }
    }

    //拖拽物品事件
    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event){
        Inventory inv = event.getInventory();
        if(InventoryTool.isCheckoutInventoryHolder(inv)){
            event.setCancelled(true);
        }
    }


}
