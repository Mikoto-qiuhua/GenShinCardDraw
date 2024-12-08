package org.qiuhua.genshincarddraw.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.qiuhua.genshincarddraw.Main;

import java.io.File;

public class Config {

    private static FileConfiguration config;

    private static String button1;
    private static String button10;
    private static String historyRecordButton;
    private static String checkoutGui1;
    private static String checkoutGui10;
    //重新加载
    public static void reload () {
        config = Tool.load(new File(Main.getMainPlugin().getDataFolder (),"config.yml"));
        button1 = config.getString("Button1");
        button10 = config.getString("Button10");
        historyRecordButton = config.getString("HistoryRecordButton");
        checkoutGui1 = config.getString("CheckoutGui1");
        checkoutGui10 = config.getString("CheckoutGui10");
    }

    public static FileConfiguration getConfig(){
        return config;
    }

    public static String getButton1(){
        return button1;
    }
    public static String getButton10(){
        return button10;
    }
    public static String getHistoryRecordButton(){
        return historyRecordButton;
    }

    public static String getCheckoutGui1() {
        return checkoutGui1;
    }

    public static String getCheckoutGui10() {
        return checkoutGui10;
    }
}
