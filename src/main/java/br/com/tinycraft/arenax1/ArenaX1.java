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
import br.com.tinycraft.arenax1.utils.identification.Identification;
import br.com.tinycraft.arenax1.utils.identification.NameIdentification;
import br.com.tinycraft.arenax1.utils.identification.UUIDIdentification;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

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
        checkForUpdates();
        this.config = new ArenaConfig(this);

        ArenaDao arenaDao = new ArenaDaoFlatFile(getDataFolder());
        DataBaseBuild dataBaseBuild = new DataBaseBuild();
        UserDao userDao;

        try {
            dataBaseBuild.buildByConfig(this, config);

            userDao = new UserDaoSQL(dataBaseBuild);
        } catch (DataBaseException e) {
            getLogger().log(Level.SEVERE,
                    "\n\nCould not load UserDao. Something is wrong! The plugin will be disabled \nError: " + e.getMessage() + "\n\n");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Identification identification;

        if(hasMethod(Player.class, "getUniqueId")) {
            identification = new UUIDIdentification();
        } else {
            getLogger().warning("Usando identificador por NOME! ##PERIGOSO##");
            identification = new NameIdentification();
        }

        this.arenaService = new ArenaService(arenaDao);
        this.userService = new UserService(userDao);
        this.language = new Language(this, config);
        this.userController = new UserController(userService, identification, getLogger());
        this.arenaController = new ArenaController(arenaService, getLogger());
        this.arenaExecutor = new ArenaExecutor(arenaController, userController, config, language);
        this.inviteController = new InviteController(arenaExecutor, config, language);
        this.commandController = new CommandController(inviteController, arenaController, arenaExecutor, userController, language);

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

        try {
            arenaController.saveAllArena();
            arenaService.saveToBase();
            userController.updateUsers();
        } catch (Exception ignored) {
        }
    }

    private void checkForUpdates() {
        getLogger().info("Checking for updates...");
        try {
            String address = "http://hecato.com/test2.php?plugin=" +
                    this.getDescription().getName() + "&version="
                    + this.getDescription().getVersion();

            URL url = new URL(address);
            URLConnection conn = null;
            conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            StringBuilder builder = new StringBuilder();

            String ln;
            while ((ln = reader.readLine()) != null) {
                builder.append(ln);
            }

            String[] values = builder.toString().split(";");

            double version = Double.parseDouble(values[0]);
            double currentVersion = Double.parseDouble(this.getDescription().getVersion());

            if (currentVersion >= version) {
                getLogger().info("The plugin is updated");
            } else {
                getLogger().warning("-------------------------------------------------------");
                getLogger().warning("Have a new version of the plugin, download in:");
                getLogger().warning(values[1]);
                getLogger().warning("-------------------------------------------------------");
            }

        } catch (Exception e) {
            getLogger().info("Can't check for updates...");
        }
    }

    public boolean hasMethod(Class clazz, String method) {
        boolean hasMethod = false;
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            if (m.getName().equals(method)) {
                return true;
            }
        }
        return false;
    }
}
