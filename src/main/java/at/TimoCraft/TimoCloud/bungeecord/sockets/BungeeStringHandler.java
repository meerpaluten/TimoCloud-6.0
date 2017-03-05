package at.TimoCraft.TimoCloud.bungeecord.sockets;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.objects.BaseObject;
import at.TimoCraft.TimoCloud.bungeecord.objects.ServerGroup;
import at.TimoCraft.TimoCloud.bungeecord.objects.TemporaryServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Created by Timo on 29.12.16.
 */
public class BungeeStringHandler extends SimpleChannelInboundHandler<String> {

    private Map<Channel, Integer> open;
    private Map<Channel, String> remaining;
    private Map<Channel, String> parsed;

    public BungeeStringHandler() {
        open = new HashMap<>();
        remaining = new HashMap<>();
        ;
        parsed = new HashMap<>();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        try {
            remaining.put(ctx.channel(), getRemaining(ctx.channel()) + message);
            read(ctx.channel());
        } catch (Exception e) {
            TimoCloud.severe("Error while parsing JSON message: " + message);
            e.printStackTrace();
        }
    }

    public void read(Channel channel) {
        for (String c : getRemaining(channel).split("")) {
            parsed.put(channel, getParsed(channel) + c);
            remaining.put(channel, getRemaining(channel).substring(1));
            if (c.equals("{")) {
                open.put(channel, getOpen(channel) + 1);
            }
            if (c.equals("}")) {
                open.put(channel, getOpen(channel) - 1);
                if (getOpen(channel) == 0) {
                    handleJSON((JSONObject) JSONValue.parse(getParsed(channel)), getParsed(channel), channel);
                    parsed.put(channel, "");
                }
            }
        }
    }

    public void handleJSON(JSONObject json, String message, Channel channel) {
        String serverName = (String) json.get("server");
        String type = (String) json.get("type");
        String data = (String) json.get("data");
        TemporaryServer server = null;
        TemporaryServer requestedServer = null;
        if (!type.toLowerCase().startsWith("base")) {
            server = TimoCloud.getInstance().getServerManager().getServerByName(serverName);
            if (server == null) {
                //TimoCloud.severe("OFFLINE server connected: " + serverName);
                channel.close();
                return;
            }
            server.setChannel(channel);
            TimoCloud.getInstance().getSocketServerHandler().getServerChannels().put(channel, server);
            requestedServer = TimoCloud.getInstance().getServerManager().getServerByName(data);
        }
        switch (type) {
            case "HANDSHAKE":
                server.register();
                server.setMap(data);
                server.setState("ONLINE");
                break;
            case "BASE_HANDSHAKE":
                InetAddress address = ((InetSocketAddress) channel.remoteAddress()).getAddress();
                if (!TimoCloud.getInstance().getFileManager().getConfig().getStringList("allowedIPs").contains(address.getHostAddress())) {
                    TimoCloud.severe("Unknown base connected from " + address.getHostAddress() + ". If you want to allow this connection, please add the IP address to 'allowedIPs' in your config.yml, else, please block the port " + TimoCloud.getInstance().getFileManager().getConfig().getInt("socket-port") + " in your firewall.");
                    channel.close();
                    return;
                }
                if (TimoCloud.getInstance().getServerManager().getBase(serverName) != null) {
                    return;
                }
                BaseObject base = new BaseObject(serverName, address, channel);
                TimoCloud.getInstance().getSocketServerHandler().getBaseChannels().put(channel, base);
                TimoCloud.getInstance().getServerManager().addBase(serverName, base);
                break;
            case "SETSTATE":
                server.setState(data);
                break;
            case "GETSTATE":
                String state = "OFFLINE";
                if (requestedServer != null) {
                    state = requestedServer.getState();
                }
                TimoCloud.getInstance().getSocketServerHandler().sendMessage(channel, data, "STATE", state);
                break;
            case "SETEXTRA":
                server.setExtra(data);
                break;
            case "GETEXTRA":
                String extra = "";
                if (requestedServer != null) {
                    extra = requestedServer.getExtra();
                }
                TimoCloud.getInstance().getSocketServerHandler().sendMessage(channel, data, "EXTRA", extra);
                break;
            case "SETMOTD":
                server.setMotd(data);
                break;
            case "GETMOTD":
                String motd = "";
                if (requestedServer != null) {
                    motd = requestedServer.getMotd();
                }
                TimoCloud.getInstance().getSocketServerHandler().sendMessage(channel, data, "MOTD", motd);
                break;
            case "GETMAP":
                String map = "";
                if (requestedServer != null) {
                    map = requestedServer.getMap();
                }
                TimoCloud.getInstance().getSocketServerHandler().sendMessage(channel, data, "MAP", map);
                break;
            case "SETPLAYERS":
                server.setPlayers(data);
                break;
            case "GETPLAYERS":
                TemporaryServer requestedServer3 = TimoCloud.getInstance().getServerManager().getServerByName(data);
                if (requestedServer3 == null) {
                    return;
                }
                TimoCloud.getInstance().getSocketServerHandler().sendMessage(channel, data, "PLAYERS", requestedServer3.getPlayers() == null ? "0/0" : requestedServer3.getPlayers());
                break;
            case "GETSERVERS":
                ServerGroup requestedGroup = TimoCloud.getInstance().getServerManager().getGroupByName(data);
                List<String> servers = new ArrayList<>();
                for (TemporaryServer t : requestedGroup.getTemporaryServers()) {
                    servers.add(t.getName());
                }
                TimoCloud.getInstance().getSocketServerHandler().sendMessage(channel, data, "SERVERS", servers);
                break;
            case "EXECUTECOMMAND":
                TimoCloud.getInstance().getProxy().getPluginManager().dispatchCommand(TimoCloud.getInstance().getProxy().getConsole(), data);
                break;
            default:
                TimoCloud.severe("Could not categorize json message: " + message);
        }
    }

    public int getOpen(Channel channel) {
        open.putIfAbsent(channel, 0);
        return open.get(channel);
    }

    public String getRemaining(Channel channel) {
        remaining.putIfAbsent(channel, "");
        return remaining.get(channel);
    }

    public String getParsed(Channel channel) {
        parsed.putIfAbsent(channel, "");
        return parsed.get(channel);
    }

}
