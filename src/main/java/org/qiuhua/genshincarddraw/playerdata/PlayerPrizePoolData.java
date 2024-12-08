package org.qiuhua.genshincarddraw.playerdata;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.qiuhua.genshincarddraw.Main;
import org.qiuhua.genshincarddraw.config.Config;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerPrizePoolData {



    //池子名字
    private String prizePoolId = null;

    //已经抽到了up池的次数
    private Integer guarantyDraw = 0;

    //这个池子抽取的次数
    private Integer useCount = 0;

    //小池子的抽取次数
    private ConcurrentHashMap<String, Integer> playerPoolData = new ConcurrentHashMap<>();

    //历史记录
    private final List<String> historyRecord = new ArrayList<>();

    public void add(String historyRecord) {
        this.historyRecord.add(historyRecord);
        if (this.historyRecord.size() > Config.getConfig().getInt("RecordMax")) {
            this.historyRecord.remove(0); // 移除最早的元素以限制大小
        }
    }

    public String historyRecordToJson(){
        Gson gson = new Gson();
        return gson.toJson(historyRecord);
    }

    public List<String> jsonToHistoryRecord(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> a = gson.fromJson(json, listType);
        historyRecord.addAll(a);
        return a;
    }



    public void addHistoryRecord(String cardId) {
        // 获取当前时间
        String currentTime = getCurrentTime();
        String historyRecordFormat = Config.getConfig().getString("HistoryRecordFormat");
        String str = historyRecordFormat.replaceAll("<card>", cardId).replaceAll("<time>", currentTime);
        historyRecord.add(str);
        if (historyRecord.size() > Config.getConfig().getInt("RecordMax")) {
            historyRecord.remove(0); // 移除最早的元素以限制大小
        }
    }

    public List<String> getHistoryRecord(){
        return this.historyRecord;
    }

    private String getCurrentTime() {
        // 获取当前时间的逻辑
        // 例如，使用 SimpleDateFormat 格式化当前时间为字符串
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public String playerPoolDataToJson() {
        Gson gson = new Gson();
        return gson.toJson(playerPoolData);
    }
    public void jsonToPlayerPoolData(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<ConcurrentHashMap<String, Integer>>() {}.getType();
        playerPoolData = gson.fromJson(json, type);
    }


    public Integer getGuarantyDraw() {
        return this.guarantyDraw;
    }
    public void addGuarantyDraw(){
        this.guarantyDraw ++;
    }

    public void setGuarantyDraw(Integer guarantyDraw) {
        this.guarantyDraw = guarantyDraw;
    }

    public String getPrizePoolId() {
        return this.prizePoolId;
    }
    public void setPrizePoolId(String prizePoolId) {
        this.prizePoolId = prizePoolId;
    }

    public Integer getUseCount() {
        return this.useCount;
    }
    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public void addUseCount(){
        this.useCount ++;
    }

    public Map<String, Integer> getPlayerPoolData() {
        return playerPoolData;
    }

    public PlayerPrizePoolData(String prizePoolId){
        this.prizePoolId = prizePoolId;
    }


    public void addPlayerPoolData(){
        for (String key : playerPoolData.keySet()) {
            Integer value = playerPoolData.get(key);
            if (value != null) {
                int incrementedValue = value + 1;
                playerPoolData.put(key, incrementedValue);
            }
        }
    }
}
