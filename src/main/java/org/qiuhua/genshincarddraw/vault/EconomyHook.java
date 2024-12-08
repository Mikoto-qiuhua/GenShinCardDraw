package org.qiuhua.genshincarddraw.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.qiuhua.genshincarddraw.config.Config;

public class EconomyHook {
    private static Economy econ = null;

    public static void setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
    }

    //扣除指定经济 会返回是否扣除成功
    public static Boolean deductMoney(Player player, Double money){
        //获取玩家余额
        double balance = econ.getBalance(player);
        double m = Math.round(money * 100.0) / 100.0;
        //判断余额是否足够
        if(balance >= money){
            EconomyResponse response = econ.withdrawPlayer(player, m); // 扣除玩家的经济
            // 检查是否成功扣除
            if (response.transactionSuccess()) {
                return true;
            } else {
                // 打印错误信息
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[ERROR] [GenShinCardDraw] 扣除经济失败: " + response.errorMessage);
                return false;
            }
        }
        return false;
    }


    public static Economy getEconomy() {
        return econ;
    }

}
