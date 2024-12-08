package org.qiuhua.genshincarddraw.card;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.qiuhua.genshincarddraw.Main;
import org.qiuhua.genshincarddraw.config.Tool;
import pers.neige.neigeitems.NeigeItems;
import pers.neige.neigeitems.manager.ItemManager;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CardDataManager {
    //全部card对象
    private static final ConcurrentHashMap<String, CardData> itemMap = new ConcurrentHashMap<>();

    private static final File folder = new File(Main.getMainPlugin().getDataFolder(), "Card");

    //加载卡片
    public static void load(){
        // 如果指定文件夹不存在，或者不是一个文件夹，则退出
        if (!folder.exists() || !folder.isDirectory()) {
            Main.getMainPlugin().getLogger().warning("未读取到Card文件夹");
            return;
        }
        //清空map
        itemMap.clear();
        // 遍历指定文件夹内的所有文件
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            // 如果不是一个YAML文件，则跳过
            if (!file.getName().endsWith(".yml")) {
                continue;
            }
            //加载配置
            FileConfiguration section = Tool.load(new File(folder,file.getName()));
            //获取全部key
            Set<String> scheme = section.getKeys(false);
            //如果不是空的
            if(!scheme.isEmpty()){
                Main.getMainPlugin().getLogger().info("加载配置文件 => " + file.getName());
                //转成独立节点
                for(String node : scheme){
                    ConfigurationSection cardSection = (ConfigurationSection) section.get(node);
                    if(itemMap.containsKey(node)){
                        Main.getMainPlugin().getLogger().warning("出现重复卡片 => " + node);
                        Main.getMainPlugin().getLogger().warning("此方案不会加载,请注意检查");
                    }else{
                        //获取卡片类型
                        String type = cardSection.getString("type");
                        CardData cardData = null;
                        switch(type){
                            case "item":
                                cardData = cardTypeItem(cardSection);
                                break;
                            case "cmd":
                                cardData = cardTypeCmd(cardSection);
                                break;
                            case "NeigeItems":
                                cardData = cardTypeNeigeItems(cardSection);
                                break;
                        }
                        if(cardData == null){
                            Main.getMainPlugin().getLogger().info("无效卡片 => " + node);
                            continue;
                        }
                        itemMap.put(node, cardData);
                        Main.getMainPlugin().getLogger().info("读取卡片 => " + node);
                    }
                }
            }
        }
    }

    //解析物品配置
    public static CardData cardTypeItem(ConfigurationSection cardSection){
        //获取卡片类型
        String type = cardSection.getString("type");
        //生成物品
        Material itemType = Material.getMaterial(Objects.requireNonNull(cardSection.getString("item.type")).toUpperCase());
        if(itemType == null){
            return null;
        }
        String itemName = cardSection.getString("item.name");
        List<String> itemLore = cardSection.getStringList("item.lore");
        int itemModel = cardSection.getInt("item..customModelData");
        //创建一个新物品堆
        ItemStack item = new ItemStack(itemType);
        //获取物品属性
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(itemName);
            itemMeta.setLore(itemLore);
            itemMeta.setCustomModelData(itemModel);
        }
        //物品属性设置回去
        item.setItemMeta(itemMeta);
        return new CardData(type, item);
    }

    public static CardData cardTypeCmd(ConfigurationSection cardSection){
        //获取卡片类型
        String type = cardSection.getString("type");
        //生成物品
        Material itemType = Material.getMaterial(Objects.requireNonNull(cardSection.getString("item.type")).toUpperCase());
        if(itemType == null){
            return null;
        }
        String itemName = cardSection.getString("item.name");
        List<String> itemLore = cardSection.getStringList("item.lore");
        int itemModel = cardSection.getInt("item..customModelData");
        //创建一个新物品堆
        ItemStack item = new ItemStack(itemType);
        //获取物品属性
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(itemName);
            itemMeta.setLore(itemLore);
            itemMeta.setCustomModelData(itemModel);
        }
        //物品属性设置回去
        item.setItemMeta(itemMeta);
        //获取cmd列表
        List<String> cmdList = cardSection.getStringList("cmd");
        return new CardData(type, item, cmdList);
    }

    public static CardData cardTypeNeigeItems(ConfigurationSection cardSection){
        //获取卡片类型
        String type = cardSection.getString("type");
        //获取物品id
        String id = cardSection.getString("id");
        //获取他的展示物品
        ItemStack itemStack = ItemManager.INSTANCE.getItemStack(id);
        return new CardData(type, id, itemStack);
    }

    //用卡片生成一个物品
    public static ItemStack spawnItem(String cardId){
        //检查有没有 没有就返回空气
        if(!itemMap.containsKey(cardId)){
            return new ItemStack(Material.AIR);
        }
        CardData cardData = itemMap.get(cardId);
        return cardData.getDisplayItem();
    }


    public static void runCard(String cardId, Player player){
        if(!itemMap.containsKey(cardId)){
            return;
        }
        itemMap.get(cardId).run(player);
    }



    //執行指令
    public static void onCommand(Player player, String cmd) {
        try {
            String[] commands = cmd.split("]:");
            // 检查指令参数长度
            if (commands.length >= 2) {
                commands[1] = Tool.getPapiString(player, cmd);
                String command = commands[1].trim();
                switch (commands[0]) {
                    case "[console":
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        break;
                    case "[op":
                        boolean originalOpStatus = player.isOp();
                        player.setOp(true);
                        player.performCommand(command);
                        player.setOp(originalOpStatus);
                        break;
                    case "[tell":
                        player.sendMessage(command);
                        break;
                    case "[player":
                        player.performCommand(command);
                        break;
                    case "[chat":
                        player.chat(command);
                        break;
                    default:
                        break;
                }
            } else {
                // 指令格式不正確的情況
                Main.getMainPlugin().getLogger().warning("---------可用的命令类型---------");
                Main.getMainPlugin().getLogger().warning("[console] - 控制台执行");
                Main.getMainPlugin().getLogger().warning("[op] - 玩家op身份执行");
                Main.getMainPlugin().getLogger().warning("[player] - 玩家身份执行");
                Main.getMainPlugin().getLogger().warning("[tell] - 向玩家发送消息");
                Main.getMainPlugin().getLogger().warning("[chat] - 玩家聊天栏执行");
            }
        } catch (Exception exception) {
            // 記錄異常日誌
            Main.getMainPlugin().getLogger().warning("处理命令时发生异常" + exception.getMessage());
        }
    }




}
