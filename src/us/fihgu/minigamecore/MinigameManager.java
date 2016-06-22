package us.fihgu.minigamecore;

import java.util.HashMap;

import us.fihgu.minigamecore.game.Minigame;

public class MinigameManager
{
	private static HashMap<String,Minigame> minigames = new HashMap<>();
	
	public static void registerMinigame(Minigame minigame)
	{
		minigames.put(minigame.getId(), minigame);
	}
}
