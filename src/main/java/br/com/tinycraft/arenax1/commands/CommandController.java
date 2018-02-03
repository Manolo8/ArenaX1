package br.com.tinycraft.arenax1.commands;

import br.com.tinycraft.arenax1.Language;
import br.com.tinycraft.arenax1.commands.annotation.CommandArena;
import br.com.tinycraft.arenax1.controller.ArenaController;
import br.com.tinycraft.arenax1.controller.InviteController;
import br.com.tinycraft.arenax1.controller.UserController;
import br.com.tinycraft.arenax1.executor.ArenaExecutor;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Willian
 */
public class CommandController implements CommandExecutor {

    private final Language language;
    private final CommandX1 commandX1;
    private final List<Method> methods;

    public CommandController(InviteController inviteController,
                             ArenaController arenaController,
                             ArenaExecutor arenaExecutor,
                             UserController userController,
                             Language language) {
        this.language = language;
        this.methods = new ArrayList<>();
        this.commandX1 = new CommandX1(inviteController, language, arenaController, userController, arenaExecutor);

        for (Method method : commandX1.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(CommandArena.class)) {
                methods.add(method);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(language.getMessage("ErrorCommandPlayerOnly"));
            return true;
        }

        Player player = (Player) cs;

        if (args.length == 0) {
            return false;
        }

        String command = cmnd.getName().toLowerCase();

        for (Method method : methods) {
            CommandArena annotation = method.getAnnotation(CommandArena.class);

            if (!annotation.superCommand().equals(command)) {
                continue;
            }
            if (!annotation.command().equalsIgnoreCase(args[0])) {
                continue;
            }
            if (!player.hasPermission(annotation.permission())) {
                player.sendMessage(annotation.permissionMessage());
                return true;
            }
            if (!ArrayUtils.contains(annotation.args(), args.length)) {
                player.sendMessage("§cUsage: " + annotation.usage());
                return true;
            }
            try {
                Object object = method.invoke(commandX1, player, args);
                if (object instanceof Boolean) return (Boolean) object;
                return true;
            } catch (Exception e) {
                player.sendMessage("§cHouve um erro interno executando o comando.");
                e.printStackTrace();
                return true;
            }
        }
        return false;
    }
}