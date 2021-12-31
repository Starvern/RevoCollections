package ware.revo.collections;

import net.Zrips.CMILib.Colors.CMIChatColor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import ware.revo.collections.commands.CollectionsCommand;
import ware.revo.collections.events.CheckPermission;
import ware.revo.collections.events.OnInventoryClick;
import ware.revo.collections.hooks.HeadDatabaseHook;
import ware.revo.collections.inventory.CollectionsMenu;

public class RevoCollections extends JavaPlugin
{
    public final FileConfiguration config = getConfig();
    public CollectionsMenu menuManager = new CollectionsMenu(this);

    @Override
    public void onEnable()
    {
        this.getLogger().info(formatColors("&aActivating &9RevoCollections [v1.0.0]"));

        saveDefaultConfig();

        subscribeCommands();
        subscribeEvents();
    }

    public void subscribeCommands()
    {
        new CollectionsCommand(this);
        new CollectionsMenu(this);
    }

    public void subscribeEvents()
    {
        getServer().getPluginManager().registerEvents(new HeadDatabaseHook(this), this);
        getServer().getPluginManager().registerEvents(new OnInventoryClick(this), this);
        new CheckPermission(this).runTaskTimer(this, 200, 200);
    }

    public String formatColors(String s)
    {
        return CMIChatColor.colorize(ChatColor.translateAlternateColorCodes('&', s));
    }

    public void giveRewards(Player player, String collection)
    {
        for ( String command : this.config.getStringList("collections." + collection + ".commands") )
        {
            if ( command.startsWith("[effect]") )
            {
                command = "effect give " + player.getName() + " " + command.replace("[effect] ", "")
                        .replace("[effect]", "");

                Bukkit.dispatchCommand(getServer().getConsoleSender(), command);
            }

            if ( command.startsWith("[message]") )
            {
                player.sendMessage(formatColors(command.replace("[message] ", "")
                        .replace("[message]", "")));
            }

            if ( command.startsWith("[broadcast]") )
            {
                Bukkit.broadcastMessage(formatColors(command.replace("[broadcast] ", "")
                        .replace("[broadcast]", "")));
            }

            if ( command.startsWith("[player]") )
            {
                player.performCommand(command.replace("[player] ", "")
                        .replace("[player]", ""));
            }

            if ( command.startsWith("[console]") )
            {
                Bukkit.dispatchCommand(getServer().getConsoleSender(), command.replace("[console] ", "")
                        .replace("[console]", ""));
            }

        }

    }

}
