package us.fihgu.minigamecore;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import us.fihgu.minigamecore.bungeecord.NetworkManager;
import us.fihgu.minigamecore.command.Join;
import us.fihgu.minigamecore.mysql.DatabaseManager;

public class Loader extends JavaPlugin
{
	public static Loader instance;
	
	@Override 
	public void onEnable()
	{
		instance = this;
		this.saveDefaultConfig();
		
		//register commands
		this.getCommand("join").setExecutor(new Join());
		
		//initialize bungeecord plugin channel
		new NetworkManager().initialize(this);
		
		//check if database exists
		try(Statement statement = DatabaseManager.getConnection().createStatement();)
		{
			statement.execute("USE " + DatabaseManager.DATABASE);
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
