package cloud.timo.TimoCloud.bungeecord.utils;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.implementations.objects.ProxyObjectBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.objects.ServerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.bungeecord.api.PlayerObjectBungeeImplementation;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerUtil {

    public static PlayerObject playerToObject(ProxiedPlayer player) {
        return playerToObject(player, true);
    }

    public static PlayerObject playerToObject(ProxiedPlayer player, boolean online) {
        return new PlayerObjectBungeeImplementation(
                player.getName(),
                player.getUniqueId(),
                player.getServer() == null ? null : ((ServerObjectBasicImplementation) TimoCloudAPI.getUniversalAPI().getServer(player.getServer().getInfo().getName())).toLink(),
                ((ProxyObjectBasicImplementation) TimoCloudAPI.getBungeeAPI().getThisProxy()).toLink(),
                player.getAddress().getAddress(),
                online);
    }

}
