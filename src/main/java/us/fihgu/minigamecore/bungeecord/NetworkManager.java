package us.fihgu.minigamecore.bungeecord;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import us.fihgu.minigamecore.Loader;

public class NetworkManager implements PluginMessageListener
{
	public static NetworkManager instance = null;
	
	public void initialize(JavaPlugin plugin)
	{
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
		
		instance = this;
	} 
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message)
	{
		//Do nothing for now
	}
	
	public void sendPlayerToServer(Player player, String serverName)
	{
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(serverName);
		this.sendBungeeCordMessage(player, out);
	}
	
	private void sendBungeeCordMessage(Player player, ByteArrayDataOutput out)
	{
		player.sendPluginMessage(Loader.instance, "BungeeCord", out.toByteArray());
	}
	
	public static String getHubServer()
	{
		return Loader.instance.getConfig().getString("hubServer");
	}
	
	/**
	 * @return the name of this server
	 */
	public static String getServerName()
	{
		return Loader.instance.getConfig().getString("serverName");
	}
	
	public static List<String> getLobbyServers()
	{
		return Loader.instance.getConfig().getStringList("lobbyServers");
	}
}
