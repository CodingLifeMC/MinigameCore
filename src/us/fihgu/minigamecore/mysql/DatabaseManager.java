package us.fihgu.minigamecore.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.configuration.file.FileConfiguration;

import us.fihgu.minigamecore.Loader;
import us.fihgu.minigamecore.game.Minigame;
import us.fihgu.minigamecore.matchmaking.MinigamePlayer;

public class DatabaseManager
{
	private static Connection connection;
	public static final String DATABASE_NAME = "minigamecore";
	
	private static void connect() throws SQLException
	{
		FileConfiguration config = Loader.instance.getConfig();
		connection = DriverManager.getConnection("jdbc:mysql://" + config.getString("mysql.host") + ":" + config.getString("mysql.port"), config.getString("mysql.user"), config.getString("mysql.password"));
	}
	
	/**
	 * will try to connect to the database first if connection is not valid.
	 */
	public static Connection getConnection() throws SQLException
	{
		if (connection == null || connection.isClosed())
		{
			connect();
			//connection = DriverManager.getConnection("jdbc:mysql://localhost:3306","root","");
		}

		return connection;
	}
	
	/**
	 * setup the default database.<br>
	 * will delete any existing data<br>
	 */
	public static void setup()
	{
		//delete existing database
		System.out.println("Setting up database.");
		try(Statement statement = getConnection().createStatement())
		{
			try
			{
				statement.execute("DROP DATABASE " + DATABASE_NAME);
			}
			catch(SQLException e1)
			{
				//fail silently
			}
			
			statement.execute("CREATE DATABASE " + DATABASE_NAME);
			statement.execute("CREATE TABLE " + DATABASE_NAME + ".players(uuid VARCHAR(36) PRIMARY KEY, username VARCHAR(50), lobby VARCHAR(50) DEFAULT \"offline\", money INT DEFAULT 0)");
		}
		catch (SQLException e)
		{
			if(e.getErrorCode() != 1008)
			{
				e.printStackTrace();
				return;
			}
		}
	}
	
	public static int getRankPoint(MinigamePlayer player, Minigame minigame)
	{
		String colomnName = "rp_" + minigame.getId();
		try(Statement statement = getConnection().createStatement())
		{
			//SELECT score FROM minigamecore.players WHERE username="fihgu";
			ResultSet resultSet = statement.executeQuery("SELECT " + colomnName + " FROM " + DATABASE_NAME + ".players WHERE username=\"" + player.getUUID().toString() + "\"");
			
			if(resultSet.next())
			{
				return resultSet.getInt(1);
			}
			else
			{
				//player is not found
				return 0;
			}
		}
		catch (SQLException e)
		{
			if(e.getErrorCode() == 1054)
			{
				//no such column, alter table
				addRankPointColumn(minigame);
				return 0;
			}
			else
			{
				e.printStackTrace();
				return 0;
			}
		}
	}
	
	public static void setRankPoint(MinigamePlayer player, Minigame minigame, int rankPoint)
	{
		initPlayerData(player);
		getRankPoint(player, minigame);
		
		//if player doesn't exist
		
		String colomnName = "rp_" + minigame.getId();
		try(Statement statement = getConnection().createStatement())
		{
			//UPDATE minigamecore.players SET rp_minigame=rank WHERE username="fihgu";
			statement.execute("UPDATE " + DATABASE_NAME + ".players SET " + colomnName + "=" + rankPoint + " WHERE uuid=\"" + player.getUUID().toString() + "\"");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}		
	}
	
	public static String getLobby(MinigamePlayer player)
	{
		try(Statement statement = getConnection().createStatement())
		{
			//SELECT lobby FROM minigamecore.players WHERE username="fihgu"
			ResultSet resultSet = statement.executeQuery("SELECT lobby FROM " + DATABASE_NAME + ".players WHERE uuid=\"" + player.getUUID().toString() + "\"");
			
			if(resultSet.next())
			{
				return resultSet.getString(1);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static void setLobby(MinigamePlayer player, String lobby)
	{
		initPlayerData(player);
		
		try(Statement statement = getConnection().createStatement())
		{
			//UPDATE minigamecore.players SET score=50 WHERE username="fihgu";
			statement.execute("UPDATE " + DATABASE_NAME + ".players SET lobby=\"" + lobby + "\" WHERE uuid=\"" + player.getUUID().toString() + "\"");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}	
	}
	
	public static int getMoney(MinigamePlayer player)
	{
		try(Statement statement = getConnection().createStatement())
		{
			//SELECT money FROM minigamecore.players WHERE username="fihgu"
			ResultSet resultSet = statement.executeQuery("SELECT money FROM " + DATABASE_NAME + ".players WHERE uuid=\"" + player.getUUID().toString() + "\"");
			
			if(resultSet.next())
			{
				return resultSet.getInt(1);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void setMoney(MinigamePlayer player, int money)
	{
		initPlayerData(player);
		
		try(Statement statement = getConnection().createStatement())
		{
			//UPDATE minigamecore.players SET money=money WHERE username="fihgu";
			statement.execute("UPDATE " + DATABASE_NAME + ".players SET money=" + money + " WHERE uuid=\"" + player.getUUID().toString() + "\"");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}	
	}
	
	/**
	 * does not check if column already exist.
	 */
	public static void addRankPointColumn(Minigame minigame)
	{
		String colomnName = "rp_" + minigame.getId();
		try (Statement statement = getConnection().createStatement())
		{
			statement.execute("ALTER TABLE " + DATABASE_NAME + ".players ADD " + colomnName + " INT DEFAULT 0");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * checks if player data exists in database, creates one if not.
	 */
	public static void initPlayerData(MinigamePlayer player)
	{
		try(Statement statement = getConnection().createStatement())
		{
			ResultSet resultSet = statement.executeQuery("SELECT uuid FROM " + DATABASE_NAME + ".players WHERE uuid=\"" + player.getUUID().toString() + "\"");
			if(!resultSet.next())
			{
				statement.execute("INSERT INTO " + DATABASE_NAME + ".players(uuid, username) VALUE(\"" + player.getUUID().toString() + "\", \"" + player.getName() +"\")");
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
