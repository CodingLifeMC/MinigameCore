package us.fihgu.minigamecore.matchmaking;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import us.fihgu.minigamecore.Loader;
import us.fihgu.minigamecore.mysql.DatabaseManager;

/**
 * move player to their correct server and lobby upon login.
 */
public class LoginHandler implements Listener
{
	@EventHandler
	public void onLogin(PlayerLoginEvent event)
	{
		Player player = event.getPlayer();
		
		BukkitRunnable task = new BukkitRunnable()
		{
			@Override
			public void run()
			{
				int playerLobby = DatabaseManager.getLobbyId(new MinigamePlayer(player));
				
				MatchmakingManager.joinLobby(playerLobby, player);
			}
		};
		task.runTaskLater(Loader.instance, 20);
	}
	
	
}
