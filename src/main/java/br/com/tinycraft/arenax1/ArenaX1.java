package br.com.tinycraft.arenax1;

import br.com.tinycraft.arenax1.arena.ArenaManager;
import br.com.tinycraft.arenax1.commands.CommandManager;
import br.com.tinycraft.arenax1.data.Data;
import br.com.tinycraft.arenax1.data.FlatFileData;
import br.com.tinycraft.arenax1.language.Language;
import br.com.tinycraft.arenax1.executor.ArenaExecutor;
import br.com.tinycraft.arenax1.gui.GUI;
import br.com.tinycraft.arenax1.invite.InviteManager;
import br.com.tinycraft.arenax1.listener.PlayerListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

/**
 *
 * @author AB
 */
public class ArenaX1 extends JavaPlugin
{

    private ArenaManager arenaManager;
    private ArenaExecutor arenaExecutor;
    private InviteManager inviteManager;
    private GUI gui;
    private Data data;
    private ArenaConfig config;
    private Language language;
    private CommandManager commandManager;
    private Metrics metrics;

    @Override
    public void onEnable()
    {
        getLogger().info("ArenaX1 By Manolo8 - cron1001@gmail.com");

        this.config = new ArenaConfig(this);
        this.data = new FlatFileData(getDataFolder());
        this.language = new Language(this, config);
        this.arenaManager = new ArenaManager(this, data.loadAllArena(), data.loadLobby());
        this.arenaExecutor = new ArenaExecutor(this, arenaManager, config, language);
        this.inviteManager = new InviteManager(arenaExecutor, config, language);
        this.gui = new GUI(arenaManager, inviteManager, arenaExecutor);
        this.commandManager = new CommandManager(inviteManager, arenaManager, gui, language);

        getServer().getScheduler().runTaskTimer(this, arenaExecutor, 20, 20);
        getServer().getScheduler().runTaskTimer(this, inviteManager, 20, 20);

        getServer().getPluginManager().registerEvents(new PlayerListener(arenaExecutor, config, gui, language), this);
        getServer().getPluginManager().registerEvents(gui, this);

        getCommand("arenax1").setExecutor(commandManager);
        getCommand("arenax1adm").setExecutor(commandManager);

        try
        {
            metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e)
        {
            metrics = null;
        }
    }

    @Override
    public void onDisable()
    {
        getLogger().info("ArenaX1 By Manolo8 - cron1001@gmail.com");
        data.saveAllArena(arenaManager.getArenas());
        data.saveLobby(arenaManager.getArenaLobby());
        try
        {
            data.saveToBase();
        } catch (Exception ex)
        {
            Logger.getLogger(ArenaX1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArenaManager getArenaManager()
    {
        return this.arenaManager;
    }

    public InviteManager inviteManager()
    {
        return this.inviteManager;
    }
}
