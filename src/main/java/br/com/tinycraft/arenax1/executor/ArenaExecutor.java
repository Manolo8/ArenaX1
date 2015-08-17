package br.com.tinycraft.arenax1.executor;

import br.com.tinycraft.arenax1.ArenaX1;
import br.com.tinycraft.arenax1.arena.Arena;
import br.com.tinycraft.arenax1.arena.ArenaManager;
import br.com.tinycraft.arenax1.ArenaConfig;
import br.com.tinycraft.arenax1.language.Language;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author AB
 */
public class ArenaExecutor extends BukkitRunnable
{

    private final ArenaX1 plugin;
    private final ArenaManager arenaManager;
    private final Language language;
    private final List<Arena> runningArenas;
    private final int defaultRemainingTime;
    private final int startWaitTime;
    private final int endingTime;

    public ArenaExecutor(ArenaX1 plugin, ArenaManager arenaManager, ArenaConfig config, Language language)
    {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.language = language;
        this.defaultRemainingTime = config.getDefaultRemainingTime();
        this.startWaitTime = config.getStartWaitTime();
        this.endingTime = config.getEndindTime();

        this.runningArenas = new ArrayList();
    }

    public void closeX1(Arena arena)
    {
        Location spawn = Bukkit.getWorld(arena.getWorld()).getSpawnLocation();

        Iterator<Player> i = arena.getPlayers().iterator();

        while (i.hasNext())
        {
            Player player = i.next();
            player.teleport(arenaManager.getArenaLobby() == null ? spawn : arenaManager.getArenaLobby());
            i.remove();
        }

        arena.setOcurring(false);
        arena.setWaitinigToStart(false);
    }

    public boolean createX1(Player player, Player player2)
    {
        Arena duel = arenaManager.getAvailableArena();

        if (duel == null)
        {
            return false;
        }
        duel.setOcurring(true);
        duel.setWaitinigToStart(true);
        duel.setRemainingTime(defaultRemainingTime);

        duel.addPlayer(player);
        duel.addPlayer(player2);

        player.teleport(duel.getPos1());
        player2.teleport(duel.getPos2());

        runningArenas.add(duel);
        return true;
    }

    public void sendMessage(Arena arena, String message)
    {
        Iterator<Player> i = arena.getPlayers().iterator();

        while (i.hasNext())
        {
            i.next().sendMessage(message);
        }
    }

    public Arena getPlayerArena(Player player, boolean remove)
    {
        Iterator<Arena> i = runningArenas.iterator();

        while (i.hasNext())
        {
            Arena arena = i.next();

            if (arena.isInArena(player, remove))
            {
                return arena;
            }
        }
        return null;
    }

    public void playerDeath(Player player)
    {
        Arena arena = getPlayerArena(player, true);

        if (arena != null)
        {
            if (arena.getPlayers().isEmpty())
            {
                player.teleport(Bukkit.getWorld(arena.getWorld()).getSpawnLocation());
            }

            arena.setRemainingTime(endingTime);
            arena.setLoser(player);

            if (arena.getWinner() != null)
            {
                arena.getWinner().sendMessage(language.getMessage("WinnerMessage", new String[]
                {
                    endingTime + ""
                }));
            }
        }
    }

    public boolean playerCommand(Player player)
    {
        return this.getPlayerArena(player, false) != null;
    }

    @Override
    public void run()
    {
        Iterator<Arena> i = this.runningArenas.iterator();

        while (i.hasNext())
        {
            Arena arena = i.next();

            if (arena.isWaitinigToStart())
            {
                int waitTime = defaultRemainingTime - arena.getRemainingTime();
                if (waitTime >= startWaitTime)
                {
                    sendMessage(arena, language.getMessage("DuelStartMessage", new String[]
                    {

                    }));
                    arena.setWaitinigToStart(false);
                } else
                {
                    sendMessage(arena, language.getMessage("WaitMessage", new String[]
                    {
                        (this.startWaitTime - waitTime) + ""
                    }));
                }
            }

            if (arena.isEnded())
            {
                if (arena.getWinner() == null)
                {
                    closeX1(arena);
                    i.remove();
                } else
                {
                    Bukkit.broadcastMessage(language.getMessage("BroadCastMessage", new String[]
                    {
                        arena.getWinner().getName(), arena.getLoser().getName()
                    }));

                    closeX1(arena);
                    i.remove();
                }
            }
        }
    }
}
