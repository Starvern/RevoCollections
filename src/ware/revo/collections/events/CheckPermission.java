package ware.revo.collections.events;

import ware.revo.collections.RevoCollections;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class CheckPermission extends BukkitRunnable
{
    private final RevoCollections plugin;

    public CheckPermission(RevoCollections pl)
    {
        this.plugin = pl;
    }

    @Override
    public void run()
    {
        for (Player player : plugin.getServer().getOnlinePlayers() )
        {
            for ( String collectionName : plugin.config.getConfigurationSection("collections").getKeys(false) )
            {
                int permissionCount = 0;
                boolean collectionCompleted = false;

                Set<String> requiredPermissions = plugin.config.getConfigurationSection(
                        "collections." + collectionName + ".required_permissions").getKeys(false);

                for ( String item : requiredPermissions )
                {
                    String permission = plugin.config.getString(
                            "collections." + collectionName + ".required_permissions." + item + ".permission");
                    if ( player.hasPermission(permission) )
                    {
                        permissionCount ++;
                    }
                    if ( permissionCount >= requiredPermissions.size() )
                    {
                        collectionCompleted = true;
                    }
                }

                if ( collectionCompleted && !player.hasPermission("revocollections." + collectionName) )
                {
                    LuckPerms luckPerms = LuckPermsProvider.get();
                    UserManager manager = luckPerms.getUserManager();
                    User user = manager.getUser(player.getUniqueId());

                    user.data().add(Node.builder("revocollections." + collectionName).build());
                    luckPerms.getUserManager().saveUser(user);

                    plugin.giveRewards(player, collectionName);
                }
            }
        }
    }
}
