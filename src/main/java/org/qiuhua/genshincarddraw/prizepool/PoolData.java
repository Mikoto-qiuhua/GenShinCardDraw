package org.qiuhua.genshincarddraw.prizepool;


import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.util.Pair;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//奖池内的独立分级小池子
public class PoolData {
    //星级
    private String level = null;

    //权重值
    private Integer weight = null;

    //物品保底次数
    private Integer guarantyDraw = null;
    //动画时长
    private Integer guiTime = null;

    //抽卡过程打开的界面
    private String gui = null;

    //物品列表
    private Map<String, Integer> itemMap = new HashMap<>();

    //权重总合
    private Integer allItemWeight = 0;


    private final List<Pair<String, Double>> toolWeights = new ArrayList<>();

    public PoolData(String level, Integer weight, Integer guiTime, String gui, Map<String, Integer> itemMap, Integer guarantyDraw){
        this.gui = gui;
        this.itemMap = itemMap;
        this.guiTime = guiTime;
        this.level = level;
        this.weight = weight;
        this.guarantyDraw = guarantyDraw;
        for (int value : itemMap.values()) {
            allItemWeight += value;
        }
        for(String key : itemMap.keySet()){
            toolWeights.add(new Pair<>(key, itemMap.get(key).doubleValue()));
        }
    }


    //获取一个卡片
    public String getRandomCard(){
        return new EnumeratedDistribution<>(toolWeights).sample();
    }




    public Integer getGuarantyDraw() {
        return this.guarantyDraw;
    }
    public void setGuarantyDraw(Integer guarantyDraw) {
        this.guarantyDraw = guarantyDraw;
    }

    public Map<String, Integer> getItemMap() {
        return this.itemMap;
    }
    public void putItemMap(String cardId, Integer weight) {
        this.itemMap.put(cardId, weight);
    }


    public Integer getGuiTime() {
        return this.guiTime;
    }
    public void setGuiTime(Integer guiTime) {
        this.guiTime = guiTime;
    }

    public Integer getWeight() {
        return this.weight;
    }
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getGui() {
        return this.gui;
    }
    public void setGui(String gui) {
        this.gui = gui;
    }

    public String getLevel() {
        return this.level;
    }
    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "PoolData{" +
                "池子等级='" + level + '\'' +
                ", 权重=" + weight +
                ", 保底次数=" + guarantyDraw +
                ", 界面播放时间=" + guiTime +
                ", 界面名称='" + gui + '\'' +
                ", 物品列表=" + itemMap +
                ", 物品总共权重=" + allItemWeight +
                '}';
    }

}
