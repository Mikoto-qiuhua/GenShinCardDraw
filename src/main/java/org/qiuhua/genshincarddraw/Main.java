package org.qiuhua.genshincarddraw;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.qiuhua.genshincarddraw.card.CardDataManager;
import org.qiuhua.genshincarddraw.command.GenShinCardDrawCommand;
import org.qiuhua.genshincarddraw.config.Config;
import org.qiuhua.genshincarddraw.config.Tool;
import org.qiuhua.genshincarddraw.database.SqlControl;
import org.qiuhua.genshincarddraw.database.SqlDataControl;
import org.qiuhua.genshincarddraw.listener.InventoryListener;
import org.qiuhua.genshincarddraw.listener.PlayerListener;
import org.qiuhua.genshincarddraw.listener.UnrealListener;
import org.qiuhua.genshincarddraw.playerdata.PlayerData;
import org.qiuhua.genshincarddraw.playerdata.PlayerDataManager;
import org.qiuhua.genshincarddraw.prizepool.PrizePoolManager;
import org.qiuhua.genshincarddraw.vault.EconomyHook;
import org.qiuhua.genshincarddraw.vault.PlayerPointsHook;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Main extends JavaPlugin {

    private static Main mainPlugin;

    public static Main getMainPlugin() {
        return mainPlugin;
    }


    //启动时运行
    @Override
    public void onEnable() {
        //设置主插件
        mainPlugin = this;
        Tool.saveAllConfig();
        CardDataManager.load();
        PrizePoolManager.load();
        Config.reload();
        new GenShinCardDrawCommand().register();
        new PlayerListener().register();
        new UnrealListener().register();
        new InventoryListener().register();
        //加载数据库
        SqlControl.loadSQL();
        SqlControl.createTable();
        SqlDataControl.autoSave();
        EconomyHook.setupEconomy();
        PlayerPointsHook.setupPlayerPoints();
    }


    //关闭时运行
    @Override
    public void onDisable() {
        //存储全部缓存的玩家数据
        ConcurrentHashMap<UUID, PlayerData> allMaster = PlayerDataManager.getAllPlayerData();
        for(UUID uuid : allMaster.keySet()){
            SqlDataControl.savePlayerData(uuid);
        }
        Main.getMainPlugin().getLogger().info("保存完成");
    }


    //执行重载命令时运行
    @Override
    public void reloadConfig() {
        CardDataManager.load();
        PrizePoolManager.load();
        Config.reload();
    }
}