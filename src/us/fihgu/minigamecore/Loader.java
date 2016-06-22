package us.fihgu.minigamecore;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import us.fihgu.minigamecore.mysql.DatabaseManager;

public class Loader extends JavaPlugin
{
	public static Loader instance;
	
	@Override 
	public void onEnable()
	{
		instance = this;
		this.saveDefaultConfig();
		
		try(Statement statement = DatabaseManager.getConnection().createStatement();)
		{
			statement.execute("USE " + DatabaseManager.DATABASE_NAME);
		}
		catch (SQLException e)
		{
			if(e.getErrorCode() == 1049)
			{
				//unknown database
				DatabaseManager.setup();
			}
			else
			{
				e.printStackTrace();
				Bukkit.getPluginManager().disablePlugin(this);
			}
		}
	}
	
	@Override
	public void onDisable()
	{
		try
		{
			DatabaseManager.getConnection().close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
