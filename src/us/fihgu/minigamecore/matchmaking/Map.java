package us.fihgu.minigamecore.matchmaking;

public class Map
{
	/**
	 * the name of the map, can be unrelated to its path.
	 */
	protected String name;
	
	/**
	 * full path of the map, including the folder name.
	 */
	protected String path;
	
	public Map(String name, String path)
	{
		this.name = name;
		this.path = path;
	}
	
	boolean saveMap = false;
}
