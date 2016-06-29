package us.fihgu.minigamecore.game;

import java.util.HashMap;

public class MinigameManager
{
	private static HashMap<String,Minigame> minigames = new HashMap<>();
	
	public static void registerMinigame(Minigame minigame)
	{
		minigames.put(minigame.getId(), minigame);
	}
	
	public static Minigame getMinigame(String minigameId)
	{
		return minigames.get(minigameId);
	}
}
