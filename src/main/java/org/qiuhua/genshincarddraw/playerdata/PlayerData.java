package org.qiuhua.genshincarddraw.playerdata;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.qiuhua.genshincarddraw.card.CardDataManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {

    //玩家当前打开的抽卡页面
    private String prizePoolId = null;

    public void setPrizePoolId(String prizePoolId) {
        this.prizePoolId = prizePoolId;
    }

    public String getPrizePoolId() {
        return this.prizePoolId;
    }

    //定时关闭gui的线程
    private BukkitTask task = null;

    //玩家当前播放的动画guiname
    private String guiName = null;

    public BukkitTask getTask() {
        return this.task;
    }
    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public String getGuiName() {
        return this.guiName;
    }
    public void setGuiName(String guiName) {
        this.guiName = guiName;
    }

    //缓存的物品 玩家抽卡后立即生成
    private final List<String> cacheCard = new ArrayList<>();

    //玩家奖池数据
    private final ConcurrentHashMap<String, PlayerPrizePoolData> playerPrizePoolDataMap = new ConcurrentHashMap<>();

    public List<String> getCacheCard(){
        return this.cacheCard;
    }
    public void setCacheCard(List<String> list){
        this.cacheCard.addAll(list);
    }

    public void delCacheCard(){
        this.cacheCard.clear();
    }

    public PlayerPrizePoolData getPlayerPrizePoolData(String key) {
        if(!this.playerPrizePoolDataMap.containsKey(key)){
            PlayerPrizePoolData playerPrizePoolData = new PlayerPrizePoolData(key);
            this.playerPrizePoolDataMap.put(key, playerPrizePoolData);
        }
        return this.playerPrizePoolDataMap.get(key);
    }
    public void putPlayerPrizePoolData(String key, PlayerPrizePoolData data){
        this.playerPrizePoolDataMap.put(key,data);
    }


    public String cacheCardToJson() {
        Gson gson = new Gson();
        return gson.toJson(this.cacheCard);
    }
    public void jsonToCacheCard(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> cardList = gson.fromJson(json, type);
        this.cacheCard.addAll(cardList);
    }




    public void cacheCardResult(Player player){
        if(this.cacheCard.isEmpty()){
            return;
        }
        for(String cardId : this.cacheCard){
            CardDataManager.runCard(cardId, player);
        }
        this.cacheCard.clear();
    }


}

