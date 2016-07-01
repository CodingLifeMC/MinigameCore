package us.fihgu.minigamecore.matchmaking;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import us.fihgu.minigamecore.Loader;
import us.fihgu.minigamecore.bungeecord.NetworkManager;
import us.fihgu.minigamecore.game.Minigame;
import us.fihgu.minigamecore.game.MinigameManager;
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
				String playerServer = null;
				int playerLobby = DatabaseManager.getLobbyId(new MinigamePlayer(player));
				
				if(playerLobby < 0)
				{
					//player is in hub.
					playerServer = NetworkManager.getHubServer();
				}
				else
				{
					//player is in a lobby
					playerServer = DatabaseManager.getServer(playerLobby);
				}
				
				if(!NetworkManager.getServerName().equals(playerServer))
				{
					//player is in the wrong server
					NetworkManager.instance.sendPlayerToServer(player, playerServer);
				}
				else
				{
					//player is in the correct server
					if(playerLobby > 0)
					{
						//player is suppose to be in a local lobby
						Lobby lobby = MatchmakingManager.getLocalLobby(playerLobby);
						if(lobby == null)
						{
							//create a new lobby if this lobby does not exist
							
							//create lobby though minigame if minigame is defined.
							Minigame minigame = MinigameManager.getMinigame(DatabaseManager.getMinigame(playerLobby));
							if(minigame != null)
							{
								lobby = minigame.createLobby();
							}
							else
							{
								lobby = MatchmakingManager.createLocalLobby(playerLobby);
							}
							
							lobby.addPlayer(player);
						}
					}
				}
			}
		};
		task.runTaskLater(Loader.instance, 1);
	}
	
	
}
