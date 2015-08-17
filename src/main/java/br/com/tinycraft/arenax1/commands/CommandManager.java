package br.com.tinycraft.arenax1.commands;

import br.com.tinycraft.arenax1.arena.ArenaManager;
import br.com.tinycraft.arenax1.commands.annotation.CommandArena;
import br.com.tinycraft.arenax1.gui.GUI;
import br.com.tinycraft.arenax1.invite.InviteManager;
import br.com.tinycraft.arenax1.language.Language;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Willian
 */
public class CommandManager implements CommandExecutor
{

    private final InviteManager inviteManager;
    private final ArenaManager arenaManager;
    private final GUI gui;
    private final Language language;
    private final CommandX1 commandX1;
    private final List<Method> methods;

    public CommandManager(InviteManager inviteManager, ArenaManager arenaManager, GUI gui, Language language)
    {
        this.inviteManager = inviteManager;
        this.arenaManager = arenaManager;
        this.gui = gui;
        this.language = language;
        this.methods = new ArrayList();
        this.commandX1 = new CommandX1(inviteManager, language, gui, arenaManager);

        for (Method method : commandX1.getClass().getDeclaredMethods())
        {
            if (method.isAnnotationPresent(CommandArena.class))
            {
                methods.add(method);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args)
    {
        if (!(cs instanceof Player))
        {
            cs.sendMessage(language.getMessage("ErrorCommandPlayerOnly", new String[0]));
            return true;
        }
        
        Player player = (Player) cs;
        
        if (args.length == 0)
        {
            return false;
        }

        String command = cmnd.getName().toLowerCase();

        for (Method method : methods)
        {
            CommandArena annotation = method.getAnnotation(CommandArena.class);

            if (!annotation.superCommand().equals(command))
            {
                continue;
            }
            if (!annotation.command().equalsIgnoreCase(args[0]))
            {
                continue;
            }
            if (!player.hasPermission(annotation.permission()))
            {
                player.sendMessage(annotation.permissionMessage());
                return true;
            }
            if (args.length != annotation.args())
            {
                player.sendMessage("§aUsage: " + annotation.usage());
            }
            try
            {
                method.invoke(commandX1, player, args);
                return true;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }
}
