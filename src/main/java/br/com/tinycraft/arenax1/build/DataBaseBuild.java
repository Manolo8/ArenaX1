package br.com.tinycraft.arenax1.build;

import br.com.tinycraft.arenax1.ArenaConfig;
import br.com.tinycraft.arenax1.exception.DataBaseException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;

public class DataBaseBuild {

    private DataBase dataBase;

    public Connection getConnection() throws DataBaseException {
        return dataBase.getConnection();
    }

    public void buildByConfig(Plugin plugin, ArenaConfig config) {
        String type = config.getString("SQLITE", "Database.TYPE");

        switch (type) {
            case "SQLITE":
                Sqlite sqlite = new Sqlite();
                sqlite.setDataFolder(plugin.getDataFolder());
                this.dataBase = sqlite;
                break;
            case "MYSQL":
                Mysql mysql = new Mysql();
                mysql.setHost(config.getString("Database.MYSQL.HOST"));
                mysql.setUsername(config.getString("Database.MYSQL.USERNAME"));
                mysql.setPassword(config.getString("Database.MYSQL.PASSWORD"));
                mysql.setDataBase(config.getString("Database.MYSQL.DB"));
                this.dataBase = mysql;
                break;
        }
    }
}
