package us.fihgu.minigamecore.matchmaking;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import us.fihgu.minigamecore.bungeecord.NetworkManager;
import us.fihgu.minigamecore.game.Minigame;
import us.fihgu.minigamecore.game.MinigameManager;
import us.fihgu.minigamecore.mysql.DatabaseManager;
import us.fihgu.toolbox.world.MapManager;

public class MatchmakingManager
{
	private static HashMap<Integer,Lobby> localLobbies = new HashMap<>();
	
	public static int registerLobby(Minigame minigame, String server)
	{
		int id = DatabaseManager.registerNewLobby(minigame, server);
		return id;
	}
	
	public static Lobby getLocalLobby(int lobbyId)
	{
		return localLobbies.get(lobbyId);
	}
	
	public static void removeLocalLobby(Lobby lobby)
	{
		localLobbies.remove(lobby.id);
		DatabaseManager.unregisterLobby(lobby.id);
		
		if(lobby.world != null)
		{
			MapManager.deleteWorld(lobby.world.getName(), Bukkit.getWorlds().get(0).getSpawnLocation());
		}
	}
	
	/**
	 * @param lobbyId -1 means hub.
	 */
	public static void joinLobby(int lobbyId, Player player)
	{
		MinigamePlayer minigamePlayer = new MinigamePlayer(player);
		
		//exist old lobby if player is in it.
		int oldLobbyId = DatabaseManager.getLobbyId(minigamePlayer);
		if(oldLobbyId != lobbyId)
		{
			Lobby oldLocalLobby = MatchmakingManager.getLocalLobby(oldLobbyId);
			if(oldLocalLobby != null)
			{
				MinigamePlayer oldPlayer = oldLocalLobby.getPlayers().get(player.getUniqueId());
				if(oldPlayer != null)
				{
					oldLocalLobby.unregisterPlayer(oldPlayer);
				}
			}
		}
		
		DatabaseManager.setLobby(minigamePlayer, lobbyId);
		
		String playerServer = null;
		if(lobbyId < 0)
		{
			//player is in hub.
			playerServer = NetworkManager.getHubServer();
		}
		else
		{
			//player is in a lobby
			playerServer = DatabaseManager.getServer(lobbyId);
		}
		
		if(playerServer == null)
		{
			playerServer = NetworkManager.getHubServer();
		}
		
		if(!NetworkManager.getServerName().equals(playerServer))
		{
			//System.out.println("Player at wrong server, sending player to the correct server!");
			//player is in the wrong server
			NetworkManager.instance.sendPlayerToServer(player, playerServer);
		}
		else
		{
			//player is in the correct server
			if(lobbyId > 0)
			{
				MatchmakingManager.joinLocalLobby(lobbyId, player);
			}
		}
	}

	/**
	 * creates a new lobby with information related to this lobbyId
	 * @param lobbyId
	 * @return null if lobbyId is not registered inside the database.
	 */
	private static Lobby createLocalLobby(int lobbyId)
	{
		Lobby lobby = null;
		String minigameId = DatabaseManager.getMinigame(lobbyId);
		
		if(minigameId != null)
		{
			Minigame minigame = MinigameManager.getMinigame(minigameId);
			if(minigame != null)
			{
				lobby = minigame.createLobby(lobbyId);
			}
			else
			{
				lobby = new Lobby(lobbyId);
			}
			
			localLobbies.put(lobbyId, lobby);
		}
		
		return lobby;
	}
	
	/**
	 * send a player into a local lobby
	 */
	private static void joinLocalLobby(int lobbyId, Player player)
	{
		String serverName = DatabaseManager.getServer(lobbyId);
		String localServer = NetworkManager.getServerName();
		
		if(!localServer.equals(serverName))
		{
			System.err.println("Local lobby (id=" + lobbyId + ") was not registered, can not create local lobby.");
			DatabaseManager.setLobby(new MinigamePlayer(player), -1);
			return;
		}
		
		
		//player is suppose to be in a local lobby
		Lobby lobby = MatchmakingManager.getLocalLobby(lobbyId);
		
		if(lobby == null)
		{
			lobby = MatchmakingManager.createLocalLobby(lobbyId);
		}
		
		lobby.addPlayer(player);
	}
}