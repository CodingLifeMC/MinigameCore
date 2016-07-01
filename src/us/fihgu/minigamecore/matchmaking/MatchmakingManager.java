package us.fihgu.minigamecore.matchmaking;

import java.util.HashMap;

import us.fihgu.minigamecore.game.Minigame;
import us.fihgu.minigamecore.game.MinigameManager;
import us.fihgu.minigamecore.mysql.DatabaseManager;

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
	}
	
	/**
	 * creates a new lobby with information related to this lobbyId
	 * @param lobbyId
	 * @return null if lobbyId is not registered inside the database.
	 */
	public static Lobby createLocalLobby(int lobbyId)
	{
		Lobby lobby = null;
		String minigameId = DatabaseManager.getMinigame(lobbyId);
		
		if(minigameId != null)
		{
			Minigame minigame = MinigameManager.getMinigame(minigameId);
			if(minigame != null)
			{
				lobby = minigame.createLobby();
			}
			else
			{
				lobby = new Lobby();
			}
			
			lobby.id = lobbyId;
			lobby.init();
		}
		
		return lobby;
	}
}