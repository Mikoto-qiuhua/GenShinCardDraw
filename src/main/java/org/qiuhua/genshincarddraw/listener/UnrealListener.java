package org.qiuhua.genshincarddraw.listener;

import com.daxton.unrealcore.common.type.MouseActionType;
import com.daxton.unrealcore.common.type.MouseButtonType;
import com.daxton.unrealcore.display.event.gui.PlayerGUICloseEvent;
import com.daxton.unrealcore.display.event.gui.module.PlayerButtonEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.qiuhua.UnrealGUIPro.api.UnrealGUIProApi;
import org.qiuhua.genshincarddraw.Main;
import org.qiuhua.genshincarddraw.config.Config;
import org.qiuhua.genshincarddraw.playerdata.PlayerData;
import org.qiuhua.genshincarddraw.playerdata.PlayerDataManager;
import org.qiuhua.genshincarddraw.prizepool.PrizePoolManager;
import org.qiuhua.genshincarddraw.unrealguipro.GuiManager;


public class UnrealListener implements Listener {

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, Main.getMainPlugin());
    }

    //玩家关闭界面事件
    @EventHandler
    public void onPlayerGUICloseEvent(PlayerGUICloseEvent event){
        Player player = event.getPlayer();
        String guiName = event.getGuiName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                PlayerData playerData = PlayerDataManager.getPlayerData(player);
                //当玩家关闭了那个抽卡动画界面时
                if(playerData.getGuiName() != null && playerData.getTask() != null && playerData.getGuiName().equals(guiName)){
                    //立即结算物品
                    playerData.getTask().cancel();
                    playerData.setTask(null);
                    playerData.setGuiName(null);
                    GuiManager.checkoutGui(player);
                    return;
                }
                //当玩家关闭了历史记录的页面时
                if(guiName.equals(Config.getConfig().getString("HistoryRecordGui"))){
                    String id = playerData.getPrizePoolId();
                    if(id != null){
                        UnrealGUIProApi.open(player, id);
                    }
                    return;
                }
                //当玩家关闭了当前的祈愿界面时
                if(guiName.equals(playerData.getPrizePoolId())){
                    playerData.setPrizePoolId(null);
                    return;
                }
                //当玩家关闭了抽卡结果的界面时
                if(guiName.equals(Config.getCheckoutGui1()) || guiName.equals(Config.getCheckoutGui10())){
                    String prizePoolId = playerData.getPrizePoolId();
                    if(prizePoolId != null){
                        UnrealGUIProApi.open(player, prizePoolId);
                    }
                }
            }
        });

    }



    //按钮事件
    @EventHandler
    public void onPlayerButtonEvent(PlayerButtonEvent event){
        Player player = event.getPlayer();
        String b = event.getModuleID();
        if(event.getButton() == MouseButtonType.Left && event.getAction() == MouseActionType.Off){
            Bukkit.getScheduler().runTaskAsynchronously(Main.getMainPlugin(), new Runnable() {
                @Override
                public void run() {
                    PlayerData playerData = PlayerDataManager.getPlayerData(player);
                    //如果是单抽
                    if(b.equals(Config.getButton1())){
                        PrizePoolManager.usePrizePool(player, playerData.getPrizePoolId(), 1);
                    } else if (b.equals(Config.getButton10())) {
                        //如果是10连抽
                        PrizePoolManager.usePrizePool(player, playerData.getPrizePoolId(), 10);
                    } else if (b.equals(Config.getHistoryRecordButton())) {
                        //如果是打开历史记录的那个按钮
                        String id = playerData.getPrizePoolId();
                        GuiManager.openRecordGui(player, id);
                    }
                }
            });
        }
    }


}
