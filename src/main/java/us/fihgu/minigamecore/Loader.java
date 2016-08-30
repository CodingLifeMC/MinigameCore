package us.fihgu.minigamecore;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import us.fihgu.minigamecore.bungeecord.NetworkManager;
import us.fihgu.minigamecore.command.Menu;
import us.fihgu.minigamecore.matchmaking.LoginHandler;
import us.fihgu.minigamecore.menu.JoinLobbyMenu;
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
		this.getCommand("menu").setExecutor(new Menu());
		
		//register events
		Bukkit.getPluginManager().registerEvents(new LoginHandler(), this);
		
		//initialize bungeecord plugin channel
		new NetworkManager().initialize(this);
		
		//check if database exists
		try(Statement statement = DatabaseManager.getConnection().createStatement())
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

		BukkitRunnable refreshMenuTask = new BukkitRunnable()
		{
			@Override
			public void run()
			{
				JoinLobbyMenu.instance.update();
			}
		};
		
		refreshMenuTask.runTaskTimer(this, 20, 60);
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
