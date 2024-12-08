package org.qiuhua.genshincarddraw.prizepool;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.bukkit.entity.Player;
import org.qiuhua.genshincarddraw.Main;
import org.qiuhua.genshincarddraw.playerdata.PlayerData;
import org.qiuhua.genshincarddraw.playerdata.PlayerDataManager;
import org.qiuhua.genshincarddraw.playerdata.PlayerPrizePoolData;
import org.qiuhua.genshincarddraw.unrealguipro.GuiManager;

import java.util.*;


//整个奖池设置
public class PrizePoolData {
    //这个池子的消耗的经济类型
    private String vault = null;
    //消耗的数量
    private Double needVault = 0.0;

    //池子名字
    private String prizePoolId = null;

    //是否是up池
    private Boolean isUpPrizePool = false;

    //up池等级
    private String upLevel = null;

    //up物品列表
    private Map<String, Integer> upItemMap = new HashMap<>();

    //up物品保底次数
    private Integer guarantyDraw = null;

    //小池子列表
    private Map<PoolData, Integer> poolDataMap = new HashMap<>();

    //权重总合
    private Integer allPoolWeight = 0;

    private List<Pair<PoolData, Double>> toolWeights = new ArrayList<>();

    private List<Pair<String, Double>> upItemWeights = new ArrayList<>();

    public Integer getAllPoolWeight() {
        return this.allPoolWeight;
    }
    public void setAllPoolWeight(Integer allPoolWeight) {
        this.allPoolWeight = allPoolWeight;
        for(PoolData key : poolDataMap.keySet()){
            toolWeights.add(new Pair<>(key, poolDataMap.get(key).doubleValue()));
        }
        for(String key : upItemMap.keySet()){
            upItemWeights.add(new Pair<>(key, upItemMap.get(key).doubleValue()));
        }
    }


//使用字符串拿到对应的小池子数据
    public PoolData getPoolData(String level){
        for(PoolData poolData : this.poolDataMap.keySet()){
            if(poolData.getLevel().equals(level)){
                return poolData;
            }
        }
        return null;
    }



    //抽取这个奖池
    public List<String> getRandomPool(Player player, Integer quantity){
        //卡片列表
        List<String> cardList = new ArrayList<>();
        //抽到过的小池子
        Map<String, Integer> poolList = new HashMap<>();

        for (int i = 1; i <= quantity; i++) {
            boolean foundMatch = false; // 添加标志变量
            //获取玩家数据 检查玩家这个池子的状态
            PlayerData playerData = PlayerDataManager.getPlayerData(player);
            //对应池子的数据
            PlayerPrizePoolData playerPrizePoolData = playerData.getPlayerPrizePoolData(prizePoolId);
            Map<String, Integer> playerPoolData = playerPrizePoolData.getPlayerPoolData();
            //检查小池子是否初始化完成
            for(PoolData poolData : poolDataMap.keySet()){
                String poolDataLevel = poolData.getLevel();
                //检查有没有记录 没有就初始化
                if(!playerPoolData.containsKey(poolDataLevel)){
                    playerPoolData.put(poolDataLevel, 0);
                }
            }
            //增加一次累积抽取次数
            playerPrizePoolData.addUseCount();
            //小池子也全部增加一次累积抽取次数
            playerPrizePoolData.addPlayerPoolData();
            //检查玩家小池子的状态
            for(PoolData poolData : poolDataMap.keySet()){
                //本次遍历的池子保底数
                Integer poolDataGuarantyDraw = poolData.getGuarantyDraw();
                String poolDataLevel = poolData.getLevel();
                if(poolDataGuarantyDraw == null || poolDataGuarantyDraw == 0){
                    continue;
                }
                //检查有没有记录 没有就初始化
                if(!playerPoolData.containsKey(poolDataLevel)){
                    player.sendMessage("初始化 " + poolDataLevel);
                    playerPoolData.put(poolDataLevel, 0);
                }
                //如果玩家的累积次数已经大于保底数了 那就必定出现这个池子
                if(playerPoolData.get(poolDataLevel) >= poolDataGuarantyDraw){
                    //必定出现的池子要清理累积次数
                    playerPoolData.put(poolDataLevel, 0);
                    //如果本次的池子是up池 那就增加玩家累积
                    if(poolData.getLevel().equals(upLevel)){
                        playerPrizePoolData.addGuarantyDraw();
                        //检查是否开启了up池
                        if(isUpPrizePool){
                            //如果开启了
                            //获取玩家当前的up保底数量
                            Integer playerGuarantyDraw = playerPrizePoolData.getGuarantyDraw();
                            //如果玩家的保底数到了这个池子设置的数量
                            if(playerGuarantyDraw >= guarantyDraw){
                                //那就必定返回一个up物品 并且清理保底
                                playerPrizePoolData.setGuarantyDraw(0);
                                //这里是清理小池子的累积抽取次数
                                playerPrizePoolData.getPlayerPoolData().put(upLevel, 0);
                                String a = new EnumeratedDistribution<>(upItemWeights).sample();;
//                                Main.getMainPlugin().getLogger().info("------------GenShinCardDraw 第" + i + "次 ------------");
//                                Main.getMainPlugin().getLogger().info("当前玩家 -> " + player.getName());
//                                Main.getMainPlugin().getLogger().info("当前池子 -> " + this.prizePoolId);
//                                Main.getMainPlugin().getLogger().info("是否启用up -> " + this.isUpPrizePool);
//                                if(this.isUpPrizePool){
//                                    Main.getMainPlugin().getLogger().info("触发大保底物品 -> " + a);
//                                    Main.getMainPlugin().getLogger().info("剩余大保底数 -> " + (guarantyDraw - playerPrizePoolData.getGuarantyDraw()));
//                                }
//                                Main.getMainPlugin().getLogger().info("累积抽取次数 -> " + playerPrizePoolData.getUseCount());
//                                Main.getMainPlugin().getLogger().info("小池子抽取次数 -> " + playerPrizePoolData.getPlayerPoolData());
//                                Main.getMainPlugin().getLogger().info("-------------------------------------------");
                                playerPrizePoolData.addHistoryRecord(a);
                                cardList.add(a);
                                poolList.put(upLevel, getPoolData(upLevel).getWeight());
                                foundMatch = true;
                                continue;
                            }
                        }
                    }
                    String a = poolData.getRandomCard();
                    playerPrizePoolData.addHistoryRecord(a);
                    cardList.add(a);
                    poolList.put(poolData.getLevel(), poolData.getWeight());
                    foundMatch = true;
//                    Main.getMainPlugin().getLogger().info("------------GenShinCardDraw 第" + i + "次 ------------");
//                    Main.getMainPlugin().getLogger().info("当前玩家 -> " + player.getName());
//                    Main.getMainPlugin().getLogger().info("当前池子 -> " + this.prizePoolId);
//                    Main.getMainPlugin().getLogger().info("是否启用up -> " + this.isUpPrizePool);
//                    Main.getMainPlugin().getLogger().info("必定出现的池子 -> " + poolData.getLevel());
//                    Main.getMainPlugin().getLogger().info("该池子保底数 -> " + poolDataGuarantyDraw);
//                    if(this.isUpPrizePool){
//                        Main.getMainPlugin().getLogger().info("剩余大保底数 -> " + (guarantyDraw - playerPrizePoolData.getGuarantyDraw()));
//                    }
//                    Main.getMainPlugin().getLogger().info("累积抽取次数 -> " + playerPrizePoolData.getUseCount());
//                    Main.getMainPlugin().getLogger().info("小池子抽取次数 -> " + playerPrizePoolData.getPlayerPoolData());
//                    Main.getMainPlugin().getLogger().info("----------------------------------------");
                    break;
                }
            }
            if(!foundMatch){
                PoolData a = new EnumeratedDistribution<>(toolWeights).sample();
                String b = a.getRandomCard();
                //如果没有触发up池保底 也没触发小池子的累积保底 那就随机一个池子
                poolList.put(a.getLevel(), a.getWeight());
                cardList.add(b);
                //如果随机的是5星
                if(a.getLevel().equals(upLevel)){
                    playerPrizePoolData.addGuarantyDraw();
                }
                //清理他抽到的次数
                playerPoolData.put(a.getLevel(),0);
//                Main.getMainPlugin().getLogger().info("------------GenShinCardDraw 第" + i + "次 ------------");
//                Main.getMainPlugin().getLogger().info("当前玩家 -> " + player.getName());
//                Main.getMainPlugin().getLogger().info("当前池子 -> " + this.prizePoolId);
//                Main.getMainPlugin().getLogger().info("是否启用up -> " + this.isUpPrizePool);
//                Main.getMainPlugin().getLogger().info("随机到的池子 -> " + a.getLevel());
//                Main.getMainPlugin().getLogger().info("该池子保底数 -> " + a.getRandomCard());
//                if(this.isUpPrizePool){
//                    Main.getMainPlugin().getLogger().info("剩余大保底数 -> " + (guarantyDraw - playerPrizePoolData.getGuarantyDraw()));
//                }
//                Main.getMainPlugin().getLogger().info("累积抽取次数 -> " + playerPrizePoolData.getUseCount());
//                Main.getMainPlugin().getLogger().info("小池子抽取次数 -> " + playerPrizePoolData.getPlayerPoolData());
//                Main.getMainPlugin().getLogger().info("----------------------------------------");
            }
        }
        String minKey = null;
        int minValue = Integer.MAX_VALUE;
        for (Map.Entry<String, Integer> entry : poolList.entrySet()) {
            if (entry.getValue() < minValue) {
                minValue = entry.getValue();
                minKey = entry.getKey();
            }
        }
        PlayerData playerData = PlayerDataManager.getPlayerData(player);
        //缓存抽到的东西
        playerData.setCacheCard(cardList);
        PlayerPrizePoolData playerPrizePoolData = playerData.getPlayerPrizePoolData(prizePoolId);
        //记录本次抽到的东西
        cardList.forEach(playerPrizePoolData::addHistoryRecord);
        //播放动画
        String guiName = this.getPoolData(minKey).getGui();
        Integer guiTime = this.getPoolData(minKey).getGuiTime();
        GuiManager.animationGui(player, guiName, guiTime);
//        Main.getMainPlugin().getLogger().info("------------GenShinCardDraw------------");
//        Main.getMainPlugin().getLogger().info("当前玩家 -> " + player.getName());
//        Main.getMainPlugin().getLogger().info("当前池子 -> " + this.prizePoolId);
//        Main.getMainPlugin().getLogger().info("是否启用up -> " + this.isUpPrizePool);
//        Main.getMainPlugin().getLogger().info("播放的动画是 -> " + guiName);
//        Main.getMainPlugin().getLogger().info("播放的动画时间 -> " + guiTime );
//        Main.getMainPlugin().getLogger().info("抽到的卡片列表 -> " + cardList);
//        Main.getMainPlugin().getLogger().info("----------------------------------------");
        return cardList;
    }


    public void setPrizePoolId(String prizePoolId) {
        this.prizePoolId = prizePoolId;
    }

    public String getPrizePoolId() {
        return this.prizePoolId;
    }

    public Map<PoolData, Integer> getPoolDataMap() {
        return this.poolDataMap;
    }

    public void setPoolDataMap(Map<PoolData, Integer> poolDataMap) {
        this.poolDataMap = poolDataMap;
    }

    public Integer getGuarantyDraw() {
        return this.guarantyDraw;
    }
    public void setGuarantyDraw(Integer guarantyDraw) {
        this.guarantyDraw = guarantyDraw;
    }


    public Boolean getUpPrizePool() {
        return this.isUpPrizePool;
    }
    public void setUpPrizePool(Boolean upPrizePool) {
        this.isUpPrizePool = upPrizePool;
    }


    public Map<String, Integer> getUpItemMap() {
        return this.upItemMap;
    }
    public void putUpItem(String cardId, Integer weight) {
        this.upItemMap.put(cardId, weight);
    }

    public String getUpLevel() {
        return this.upLevel;
    }
    public void setUpLevel(String upLevel) {
        this.upLevel = upLevel;
    }

    public Double getNeedVault() {
        return this.needVault;
    }

    public void setNeedVault(Double needVault) {
        this.needVault = needVault;
    }

    public void setVault(String vault) {
        this.vault = vault;
    }
    public String getVault() {
        return this.vault;
    }

    @Override
    public String toString() {
        return "PrizePoolData{" +
                "是否是up池=" + isUpPrizePool +
                ", up等级='" + upLevel + '\'' +
                ", up物品列表=" + upItemMap +
                ", up保底次数=" + guarantyDraw +
                ", 小池子=" + poolDataMap +
                ", 小池子总共权重=" + allPoolWeight +
                ", 经济类型=" + vault +
                ", 消耗经济=" + needVault +
                '}';
    }

}
