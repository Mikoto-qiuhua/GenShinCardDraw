package org.qiuhua.genshincarddraw.unrealguipro;

import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.been.placeholder.been.TransitionBeen;
import com.daxton.unrealcore.display.been.module.ModuleData;
import com.daxton.unrealcore.display.been.module.control.ContainerModuleData;
import com.daxton.unrealcore.display.been.module.display.TextModuleData;
import com.daxton.unrealcore.display.content.gui.UnrealCoreGUI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;
import org.qiuhua.UnrealGUIPro.api.UnrealGUIProApi;
import org.qiuhua.UnrealGUIPro.gui.UnrealGUIContainer;
import org.qiuhua.genshincarddraw.Main;
import org.qiuhua.genshincarddraw.card.CardDataManager;
import org.qiuhua.genshincarddraw.config.Config;
import org.qiuhua.genshincarddraw.playerdata.PlayerData;
import org.qiuhua.genshincarddraw.playerdata.PlayerDataManager;
import org.qiuhua.genshincarddraw.prizepool.PrizePoolManager;

import java.util.List;
import java.util.Map;


public class GuiManager {


    //打开祈愿池子
    public static void openPrizePool(Player player, String prizePoolId){
        UnrealGUIProApi.open(player, prizePoolId);
        PlayerDataManager.getPlayerData(player).setPrizePoolId(prizePoolId);
    }




    //向玩家打开一定时间的gui 使用guiPro的api
    public static void animationGui(Player player, String guiName, Integer guiTime){
        PlayerData playerData = PlayerDataManager.getPlayerData(player);
        UnrealGUIProApi.open(player, guiName);
        playerData.setGuiName(guiName);
        BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                UnrealCoreAPI.closeGUI(player);
            }
        }, guiTime);
        playerData.setTask(task);
    }

    //结算物品的界面
    public static void checkoutGui(Player player){
        PlayerData playerData = PlayerDataManager.getPlayerData(player);
        Inventory inventory;
        if(playerData.getCacheCard().size() == 1){
            inventory = Bukkit.createInventory(new CheckoutInventoryHolder(), 27, Config.getConfig().getString("CheckoutGui1"));
        }else{
            inventory = Bukkit.createInventory(new CheckoutInventoryHolder(), 27, Config.getConfig().getString("CheckoutGui10"));
        }
        //加载物品
        InventoryTool.loadGuiItem(inventory, player);
        for(String url : Config.getConfig().getStringList("GifReset")){
            UnrealCoreAPI.inst(player).getResourceHelper().resetGIF(url);
        }
        Bukkit.getScheduler().runTask(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                player.openInventory(inventory);
            }
        });
        playerData.cacheCardResult(player);
    }



    //给玩家发一个动画占位符
    public static void animationPapi(Player player){
        //设置动画占位符
        TransitionBeen transitionBeen = new TransitionBeen("GenShinCardDraw", 0, 255, 200, false);
        UnrealCoreAPI.inst(player).getPlaceholderHelper().transitionValueSet("GenShinCardDraw", transitionBeen);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                TransitionBeen transitionBeen = new TransitionBeen("GenShinCardDraw", 255, 1, 200, true);
                UnrealCoreAPI.inst(player).getPlaceholderHelper().transitionValueSet("GenShinCardDraw", transitionBeen);
            }
        },20);
    }


    //查看历史记录
    public static void openRecordGui(Player player, String prizePoolId){
        Bukkit.getScheduler().runTaskAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                UnrealGUIContainer unrealGUIContainer = UnrealGUIProApi.getUnrealGUIContainer(player, Config.getConfig().getString("HistoryRecordGui"));
                String historyRecordText = Config.getConfig().getString("HistoryRecordText");
                TextModuleData textModuleData = GuiManager.getTextModule(unrealGUIContainer.mainGUIData.getModuleDataMap(), historyRecordText);
                List<String> list = PlayerDataManager.getPlayerData(player).getPlayerPrizePoolData(prizePoolId).getHistoryRecord();
                textModuleData.setText(list);
                UnrealCoreAPI.inst(player).getGUIHelper().openCoreGUI(unrealGUIContainer);
            }
        });
    }


    //获取指定文本组件
    public static TextModuleData getTextModule(Map<String, ModuleData> allModuleData, String afterDot){
        //包含-符号时进行分割
        if (afterDot.contains("-")){
            String[] subParts = afterDot.split("-"); // 使用"-"进行分割
            ContainerModuleData containerModule = null;
            ModuleData moduleData = null;
            for(String modelId : subParts){
                //这里肯定是第一次循环执行的在主要的data内拿
                if(containerModule == null){
                    containerModule = (ContainerModuleData) allModuleData.get(modelId);
                    continue;
                }
                //这里开始是第二次循环后的操作
                if(moduleData == null){
                    moduleData = containerModule.getModuleDataMap().get(modelId);
                }else{
                    ContainerModuleData a =  (ContainerModuleData) moduleData;
                    moduleData = a.getModuleDataMap().get(modelId);
                }
                //如果他是一个输入框的话
                if(moduleData instanceof TextModuleData module){
                    return module;
                }
            }
        }
        ModuleData moduleData = allModuleData.get(afterDot);
        return (TextModuleData) moduleData;

    }


}
