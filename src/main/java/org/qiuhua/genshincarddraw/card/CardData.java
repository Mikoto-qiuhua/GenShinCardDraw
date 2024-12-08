package org.qiuhua.genshincarddraw.card;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.qiuhua.QiuhuaItemSpace.Main;
import org.qiuhua.QiuhuaItemSpace.api.AddItemApi;
import pers.neige.neigeitems.manager.ItemManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//物品类型
public class CardData {

    //物品类型
    private String type = null;

    //展示物品
    private ItemStack displayItem = null;

    //执行的命令
    private List<String> cmdList = new ArrayList<>();

    //物品库的物品name
    private String name = null;


    public CardData(String type, ItemStack displayItem){
        this.type = type;
        this.displayItem = displayItem;
    }


    public CardData(String type, ItemStack displayItem, List<String> cmdList){
        this.type = type;
        this.displayItem = displayItem;
        this.cmdList = cmdList;
    }

    public CardData(String type, String name){
        this.type = type;
        this.name = name;
    }

    public CardData(String type, String name, ItemStack displayItem){
        this.type = type;
        this.name = name;
        this.displayItem = displayItem;
    }



    public String getType(){
        return this.type;
    }
    public String getName(){
        return  this.name;
    }
    public List<String> getCmdList(){
        return this.cmdList;
    }
    public ItemStack getDisplayItem(){
        return this.displayItem;
    }

    //执行卡片
    public void run(Player player){
        switch (this.type){
            case "item":
                if(AddItemApi.tryAllSpaceAddItem(player, this.displayItem) == null){
                    if (player.getInventory().firstEmpty() != -1) {
                        // 玩家背包有空位，直接给予物品
                        player.getInventory().addItem(displayItem);
                    } else {
                        // 玩家背包已满，将物品掉落在地上
                        player.getWorld().dropItem(player.getLocation(), displayItem);
                    }
                }
                break;
            case "NeigeItems":
                ItemStack itemStack = ItemManager.INSTANCE.getItemStack(name, player);
                if(AddItemApi.tryAllSpaceAddItem(player, itemStack) == null){
                    if (player.getInventory().firstEmpty() != -1) {
                        // 玩家背包有空位，直接给予物品
                        player.getInventory().addItem(itemStack);
                    } else {
                        // 玩家背包已满，将物品掉落在地上
                        player.getWorld().dropItem(player.getLocation(), itemStack);
                    }
                }
            case "cmd":
                Bukkit.getScheduler().runTask(Main.getMainPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        cmdList.forEach(cmd -> {
                            CardDataManager.onCommand(player, cmd);
                        });
                    }
                });
                break;
        }
    }





}
