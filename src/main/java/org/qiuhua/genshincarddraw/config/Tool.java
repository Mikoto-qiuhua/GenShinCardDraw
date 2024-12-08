package org.qiuhua.genshincarddraw.config;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.qiuhua.genshincarddraw.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tool {


    //创建配置文件
    public static void saveAllConfig(){
        //创建一个插件文件夹路径为基础的 并追加下一层。所以此时的文件应该是Config.yml
        //exists 代表是否存在
        if (!(new File(Main.getMainPlugin().getDataFolder() ,"config.yml").exists())){
            Main.getMainPlugin().saveResource("config.yml", false);
        }
        if (!(new File (Main.getMainPlugin().getDataFolder() ,"Card").exists())){
            Main.getMainPlugin().saveResource("Card/物品1.yml", false);
        }
        if (!(new File (Main.getMainPlugin().getDataFolder() ,"PrizePool").exists())){
            Main.getMainPlugin().saveResource("PrizePool/祈愿常驻池.yml", false);
        }
    }


    public static YamlConfiguration load (File file) {
        return YamlConfiguration.loadConfiguration(file);
    }











    private static Boolean isPlaceholderAPI = Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("PlaceholderAPI")).isEnabled();

    // 使用 PAPI 替换占位符
    public static String getPapiString(Player player, String string){
        if(isPlaceholderAPI){
            return PlaceholderAPI.setPlaceholders(player, string);
        }
        return string;
    }

    public static List<String> getPapiList(Player player, List<String> list){
        if(isPlaceholderAPI){
            List<String>  newList = new ArrayList<>();
            list.forEach((e) -> {
                newList.add(PlaceholderAPI.setPlaceholders(player, e));
            });
            return newList;
        }
        return list;
    }

}
