package org.qiuhua.genshincarddraw.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.qiuhua.genshincarddraw.Main;
import org.qiuhua.genshincarddraw.config.Config;
import org.qiuhua.genshincarddraw.playerdata.PlayerData;
import org.qiuhua.genshincarddraw.playerdata.PlayerDataManager;
import org.qiuhua.genshincarddraw.playerdata.PlayerPrizePoolData;
import org.qiuhua.genshincarddraw.prizepool.PrizePoolManager;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class SqlControl {

    private static HikariDataSource dataSource;
    private static Connection connection;




    //创建表 给每个奖池都创建一个单独的
    public static void createTable() {
        try {
            connect();
            Statement statement = connection.createStatement();
            //数据类型 uuid  玩家小池子的数据  池子的up保底数 抽取的次数 历史记录
            String sql = "CREATE TABLE IF NOT EXISTS cacheCard (" +
                    "uuid VARCHAR(255) PRIMARY KEY," +
                    "cacheCard TEXT" +
                    ")";
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for(String prizePool : PrizePoolManager.getPrizePoolMap().keySet()){
            try {
                connect();
                Statement statement = connection.createStatement();
                //数据类型 uuid  玩家小池子的数据  池子的up保底数 抽取的次数 历史记录
                String sql = "CREATE TABLE IF NOT EXISTS " + prizePool + " (" +
                        "uuid VARCHAR(255) PRIMARY KEY," +
                        "playerPoolData TEXT," +
                        "guarantyDraw INT," +
                        "useCount INT," +
                        "historyRecord TEXT" +
                        ")";
                statement.executeUpdate(sql);
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //存储一个指定玩家的数据
    public static void insert(UUID uuid) {
        PlayerData playerData = PlayerDataManager.getPlayerData(uuid);
        for(String prizePool : PrizePoolManager.getPrizePoolMap().keySet()){
            PlayerPrizePoolData playerPrizePoolData = playerData.getPlayerPrizePoolData(prizePool);
            try {
                cleanData(uuid.toString(), prizePool); // 先清理具有相同 UUID 的数据
                connect();
                PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO " + prizePool + " (uuid, playerPoolData, guarantyDraw, useCount, historyRecord) VALUES (?, ?, ?, ?, ?)"
                );
                insertStatement.setString(1, uuid.toString());
                insertStatement.setString(2, playerPrizePoolData.playerPoolDataToJson());
                insertStatement.setInt(3, playerPrizePoolData.getGuarantyDraw());
                insertStatement.setInt(4, playerPrizePoolData.getUseCount());
                insertStatement.setString(5, playerPrizePoolData.historyRecordToJson());
                insertStatement.executeUpdate();
                insertStatement.close();
//            Main.getMainPlugin().getLogger().info("存储玩家 -> " + uuid + " 数据成功");
            } catch (SQLException e) {
                e.printStackTrace();
                Main.getMainPlugin().getLogger().warning("存储玩家 -> " + uuid + " 数据失败");
            }
        }
        //存储card缓存
        try {
            cleanData(uuid.toString(), "cacheCard"); // 先清理具有相同 UUID 的数据
            connect();
            PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO cacheCard (uuid, cacheCard) VALUES (?, ?)"
            );
            insertStatement.setString(1, uuid.toString());
            insertStatement.setString(2, playerData.cacheCardToJson());
            insertStatement.executeUpdate();
            insertStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Main.getMainPlugin().getLogger().warning("存储玩家 -> " + uuid + " 数据失败");
        }
    }

    //加载一个玩家的全部数据
    public static void loadData(UUID uuid) {
        PlayerData playerData = PlayerDataManager.getPlayerData(uuid);
        try {
            connect();
            for (String prizePool : PrizePoolManager.getPrizePoolMap().keySet()) {
                PlayerPrizePoolData playerPrizePoolData = playerData.getPlayerPrizePoolData(prizePool);
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT playerPoolData, guarantyDraw, useCount, historyRecord FROM " + prizePool + " WHERE uuid = ?"
                );
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String playerPoolDataJson = resultSet.getString("playerPoolData");
                    int guarantyDraw = resultSet.getInt("guarantyDraw");
                    int useCount = resultSet.getInt("useCount");
                    String historyRecordJson = resultSet.getString("historyRecord");
                    playerPrizePoolData.jsonToPlayerPoolData(playerPoolDataJson);
                    playerPrizePoolData.setGuarantyDraw(guarantyDraw);
                    playerPrizePoolData.setUseCount(useCount);
                    playerPrizePoolData.jsonToHistoryRecord(historyRecordJson);

                }
                resultSet.close();
                statement.close();
            }
            //加载玩家缓存
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT cacheCard FROM cacheCard WHERE uuid = ?"
            );
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String cacheCardJson = resultSet.getString("cacheCard");
                playerData.jsonToCacheCard(cacheCardJson);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Main.getMainPlugin().getLogger().warning("获取玩家 -> " + uuid + " 数据失败");
        }
    }





    //清理指定玩家的数据
    public static void cleanData(String uuid, String prizePool) {
        try {
            connect();
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM " + prizePool + " WHERE uuid = ?"
            );
            statement.setString(1, uuid);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //加载
    public static void loadSQL(){
        String databaseType = Config.getConfig().getString("Database.type");
        String dataBaseName = "";
        String username = "";
        String password = "";
        int port = 3306;
        String ip = "";
        //加载sqlite
        if(databaseType.equalsIgnoreCase("mysql")) {
            dataBaseName = Config.getConfig().getString("Database.dataBaseName");
            username = Config.getConfig().getString("Database.username");
            password = Config.getConfig().getString("Database.password");
            port = Config.getConfig().getInt("Database.port");
            ip = Config.getConfig().getString("Database.ip");
        }
        connection = getConnection(databaseType, ip, port, dataBaseName,username, password);
        if(connection != null){
            Main.getMainPlugin().getLogger().info("数据库连接成功....");
        }
    }

    //连接数据库 防止连接断开
    public static void connect() {
        try {
            if(dataSource == null || dataSource.isClosed()){
                loadSQL();
            }
            if(connection == null || connection.isClosed()){
                connection = dataSource.getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //通用方法
    public static Connection getConnection(String databaseType, String host, int port, String databaseName, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setMaxLifetime(1800000);
        config.setMinimumIdle(10);
        config.setMaximumPoolSize(10);
        config.setValidationTimeout(5000);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        if (databaseType.equalsIgnoreCase("mysql")) {
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?autoReconnect=true");
            config.setUsername(username);
            config.setPassword(password);
        } else if (databaseType.equalsIgnoreCase("sqlite")) {
            //创建数据库文件路径
            String dbFilePath = Main.getMainPlugin().getDataFolder().getAbsolutePath() + File.separator + "database.db";
            config.setJdbcUrl("jdbc:sqlite:" + dbFilePath);
            config.setDriverClassName("org.sqlite.JDBC");
        } else {
            throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }

        // 设置其他共享的数据源属性
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);

        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    // 执行 SELECT 1 查询并返回结果
    public static void keepConnectionAlive() {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connect();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT 1");
        } catch (SQLException e) {
            // 处理异常
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
