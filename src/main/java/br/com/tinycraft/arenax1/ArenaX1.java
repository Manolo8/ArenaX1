package br.com.tinycraft.arenax1;

import br.com.tinycraft.arenax1.build.DataBaseBuild;
import br.com.tinycraft.arenax1.commands.CommandController;
import br.com.tinycraft.arenax1.controller.ArenaController;
import br.com.tinycraft.arenax1.controller.InviteController;
import br.com.tinycraft.arenax1.controller.UserController;
import br.com.tinycraft.arenax1.exception.DataBaseException;
import br.com.tinycraft.arenax1.executor.ArenaExecutor;
import br.com.tinycraft.arenax1.listener.PlayerListener;
import br.com.tinycraft.arenax1.repository.ArenaDao;
import br.com.tinycraft.arenax1.repository.UserDao;
import br.com.tinycraft.arenax1.repository.impl.ArenaDaoFlatFile;
import br.com.tinycraft.arenax1.repository.impl.UserDaoSQL;
import br.com.tinycraft.arenax1.service.ArenaService;
import br.com.tinycraft.arenax1.service.UserService;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author AB
 */
public class ArenaX1 extends JavaPlugin {

    private ArenaService arenaService;
    private UserService userService;

    private ArenaController arenaController;
    private InviteController inviteController;
    private UserController userController;
    private CommandController commandController;

    private ArenaExecutor arenaExecutor;
    private ArenaConfig config;
    private Language language;

    @Override
    public void onEnable() {
        getLogger().info("ArenaX1 By Manolo8 - cron1001@gmail.com");
        this.config = new ArenaConfig(this);

        ArenaDao arenaDao = new ArenaDaoFlatFile(getDataFolder());
        DataBaseBuild dataBaseBuild = new DataBaseBuild();
        UserDao userDao;

        try {
            dataBaseBuild.buildByConfig(this, config);

            userDao = new UserDaoSQL(dataBaseBuild);
        } catch (DataBaseException e) {
            getLogger().log(Level.SEVERE,
                    "Could not load UserDao. Something is wrong! The plugin will not start \n" + e.getMessage());
            return;
        }

        this.arenaService = new ArenaService(arenaDao);
        this.userService = new UserService(userDao);

        this.language = new Language(this, config);

        this.userController = new UserController(userService, getLogger());
        this.arenaController = new ArenaController(arenaService, getLogger());
        this.inviteController = new InviteController(arenaExecutor, config, language);
        this.commandController = new CommandController(inviteController, arenaController, arenaExecutor, userController, language);
        this.arenaExecutor = new ArenaExecutor(arenaController, config, language);

        getServer().getScheduler().runTaskTimer(this, arenaExecutor, 20, 20);
        getServer().getScheduler().runTaskTimer(this, inviteController, 20, 20);
        getServer().getScheduler().runTaskTimer(this, inviteController, 20 * 30, 20 * 30);
        getServer().getScheduler().runTaskTimer(this, userController, 20 * 30, 20 * 30);

        getServer().getPluginManager().registerEvents(new PlayerListener(arenaExecutor, userController, language), this);

        getCommand("arenax1").setExecutor(commandController);
        getCommand("arenax1adm").setExecutor(commandController);

    }

    @Override
    public void onDisable() {
        getLogger().info("ArenaX1 By Manolo8 - cron1001@gmail.com");

        arenaController.saveAllArena();

        try {
            arenaService.saveToBase();
            userController.updateUsers();
        } catch (Exception ex) {
            Logger.getLogger(ArenaX1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
