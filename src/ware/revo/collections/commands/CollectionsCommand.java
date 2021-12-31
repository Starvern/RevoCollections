package ware.revo.collections.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ware.revo.collections.RevoCollections;

public class CollectionsCommand implements CommandExecutor
{
    private final RevoCollections plugin;

    public CollectionsCommand(RevoCollections pl)
    {
        this.plugin = pl;
        this.plugin.getCommand("collections").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if ( !(sender instanceof Player) )
        {
            sender.sendMessage(plugin.formatColors(plugin.config.getString("messages.error_notplayer")));
        }

        if ( commandLabel.equals("collections") || commandLabel.equals("collection")  )
        {
            Player player = (Player) sender;
            player.openInventory(plugin.menuManager.MainInventory(player));
        }

        return true;
    }
}
