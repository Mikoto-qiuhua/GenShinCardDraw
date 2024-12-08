package org.qiuhua.genshincarddraw.prizepool;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.qiuhua.genshincarddraw.Main;
import org.qiuhua.genshincarddraw.card.CardData;
import org.qiuhua.genshincarddraw.config.Tool;
import org.qiuhua.genshincarddraw.playerdata.PlayerData;
import org.qiuhua.genshincarddraw.playerdata.PlayerDataManager;
import org.qiuhua.genshincarddraw.playerdata.PlayerPrizePoolData;
import org.qiuhua.genshincarddraw.unrealguipro.GuiManager;
import org.qiuhua.genshincarddraw.vault.EconomyHook;
import org.qiuhua.genshincarddraw.vault.PlayerPointsHook;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PrizePoolManager {
    //全部奖池
    private static final ConcurrentHashMap<String, PrizePoolData> prizePoolMap = new ConcurrentHashMap<>();

    private static final File folder = new File(Main.getMainPlugin().getDataFolder(), "PrizePool");


    public static ConcurrentHashMap<String, PrizePoolData> getPrizePoolMap() {
        return prizePoolMap;
    }

    public static void load() {
        // 如果指定文件夹不存在，或者不是一个文件夹，则退出
        if (!folder.exists() || !folder.isDirectory()) {
            Main.getMainPlugin().getLogger().warning("未读取到PrizePool文件夹");
            return;
        }
        prizePoolMap.clear();
        // 遍历指定文件夹内的所有文件
        for (File file : Objects.requireNonNull(folder.listFiles())){
            // 如果不是一个YAML文件，则跳过
            if (!file.getName().endsWith(".yml")) {
                continue;
            }
            String fileName = file.getName().replaceAll(".yml", "");
            //加载配置
            FileConfiguration section = Tool.load(new File(folder,file.getName()));
            boolean isUpPrizePool = section.getBoolean("Up.IsUp");
            //pool池子参数
            ConfigurationSection poolSection = (ConfigurationSection) section.get("Pool");
            //获取全部key
            if (poolSection == null) {
                Main.getMainPlugin().getLogger().warning("奖池配置错误 => " + fileName);
                continue;
            }
            PrizePoolData prizePoolData = new PrizePoolData();
            prizePoolData.setPrizePoolId(fileName);
            prizePoolData.setVault(section.getString("Vault"));
            prizePoolData.setNeedVault(section.getDouble("NeedVault"));
            //如果是up池
            if(isUpPrizePool){
                //up参数
                String level = section.getString("Up.Level");
                Integer guarantyDraw = section.getInt("Up.GuarantyDraw");
                List<String> upItemList = section.getStringList("Up.UpItem");
                upItemList.forEach(str -> {
                    String[] parts = str.split(":");
                    String cardId = parts[0];
                    Integer weight = Integer.valueOf(parts[1]);
                    prizePoolData.putUpItem(cardId, weight);
                });
                prizePoolData.setUpPrizePool(true);
                prizePoolData.setUpLevel(level);
                prizePoolData.setGuarantyDraw(guarantyDraw);
            }
            //处理pool内的池子
            Set<String> scheme = poolSection.getKeys(false);
            Map<PoolData, Integer> poolDataMap = new HashMap<>();
            int allPoolWeight = 0;
            for(String poolLevel : scheme){
                String gui = poolSection.getString(poolLevel + ".Gui");
                Integer guiTime = poolSection.getInt(poolLevel + ".GuiTime");
                Integer weight = poolSection.getInt(poolLevel + ".Weight");
                List<String> poolItemList = poolSection.getStringList(poolLevel + ".Item");
                Map<String, Integer> poolItemMap = new HashMap<>();
                poolItemList.forEach(str -> {
                    String[] parts = str.split(":");
                    String cardId = parts[0];
                    Integer cardWeight = Integer.valueOf(parts[1]);
                    poolItemMap.put(cardId,cardWeight);
                });
                Integer guarantyDraw = poolSection.getInt(poolLevel + ".GuarantyDraw");
                PoolData poolData = new PoolData(poolLevel, weight, guiTime, gui, poolItemMap, guarantyDraw);
                poolDataMap.put(poolData, weight);
                allPoolWeight += weight;
            }
            prizePoolData.setPoolDataMap(poolDataMap);
            prizePoolData.setAllPoolWeight(allPoolWeight);
            prizePoolMap.put(fileName, prizePoolData);
            Main.getMainPlugin().getLogger().info("加载奖池 => " + fileName);
//            Main.getMainPlugin().getLogger().info(prizePoolData.toString());
        }
    }


    //抽取一个指定奖池
    public static void usePrizePool(Player player, String prizePoolId, Integer quantity) {
        PlayerData playerData = PlayerDataManager.getPlayerData(player);
        if (!playerData.getCacheCard().isEmpty()) {
            if(playerData.getTask() != null){
                playerData.getTask().cancel();
            }
            playerData.setTask(null);
            playerData.setGuiName(null);
            playerData.cacheCardResult(player);
            return;
        }
        //检查玩家经济是否足够
        String vaultType = prizePoolMap.get(prizePoolId).getVault();
        Double needVault = prizePoolMap.get(prizePoolId).getNeedVault();
        boolean a = true;
        //如果设置了经济类型
        if(vaultType != null && !vaultType.equals("")){
            if(vaultType.equals("Vault")){
                a = EconomyHook.deductMoney(player, quantity * needVault);
            } else if (vaultType.equals("PlayerPoints")) {
                a = PlayerPointsHook.deductMoney(player, quantity * needVault);
            }
        }
        if(a){
            //本次抽到的卡片列表
            prizePoolMap.get(prizePoolId).getRandomPool(player, quantity);
        }else {
            GuiManager.animationPapi(player);
        }
    }
}
