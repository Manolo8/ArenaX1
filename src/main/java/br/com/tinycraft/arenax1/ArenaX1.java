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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

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

    @Override
    public void onEnable()
    {
        getLogger().info("ArenaX1 By Manolo8 - cron1001@gmail.com");

        this.config = new ArenaConfig(this);
        this.data = new FlatFileData(getDataFolder());
        this.language = new Language(this, config);
        this.arenaManager = new ArenaManager(this, data.loadAllArena());
        this.arenaExecutor = new ArenaExecutor(this, arenaManager, config, language);
        this.inviteManager = new InviteManager(arenaExecutor, config, language);
        this.gui = new GUI(arenaManager, inviteManager, arenaExecutor);
        this.commandManager = new CommandManager(inviteManager, arenaManager, gui, language);

        getServer().getScheduler().runTaskTimer(this, arenaExecutor, 20, 20);
        getServer().getScheduler().runTaskTimer(this, inviteManager, 20, 20);

        getServer().getPluginManager().registerEvents(new PlayerListener(arenaExecutor, config, gui), this);
        getServer().getPluginManager().registerEvents(gui, this);

        getCommand("arenax1").setExecutor(commandManager);
        getCommand("arenax1adm").setExecutor(commandManager);
    }

    @Override
    public void onDisable()
    {
        getLogger().info("ArenaX1 By Manolo8 - cron1001@gmail.com");
        data.saveAllArena(arenaManager.getArenas());
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
