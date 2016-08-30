package us.fihgu.minigamecore.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;

import org.bukkit.configuration.file.FileConfiguration;

import us.fihgu.minigamecore.Loader;
import us.fihgu.minigamecore.bungeecord.NetworkManager;
import us.fihgu.minigamecore.game.Minigame;
import us.fihgu.minigamecore.matchmaking.MinigamePlayer;

public class DatabaseManager
{
	private static Connection connection;
	
	public static final String DATABASE = "minigamecore";
	public static final String PLAYERS_TABLE = "players";
	public static final String LOBBIES_TABLE = "lobbies";
	
	
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
				statement.execute("DROP DATABASE " + DATABASE);
			}
			catch(SQLException e1)
			{
				//fail silently
			}
			
			statement.execute("CREATE DATABASE " + DATABASE);
			statement.execute("CREATE TABLE " + DATABASE + "." + PLAYERS_TABLE + "(uuid VARCHAR(36) PRIMARY KEY, username VARCHAR(50), lobby INT DEFAULT -2, money INT DEFAULT 0)");
			statement.execute("CREATE TABLE " + DATABASE + "." + LOBBIES_TABLE + "(id INT AUTO_INCREMENT PRIMARY KEY, minigame VARCHAR(64), playercount INT DEFAULT 0, server VARCHAR(64))");
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
			ResultSet resultSet = statement.executeQuery("SELECT " + colomnName + " FROM " + DATABASE + "." + PLAYERS_TABLE + " WHERE username=\"" + player.getUUID().toString() + "\"");
			
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
			statement.execute("UPDATE " + DATABASE + "." + PLAYERS_TABLE + " SET " + colomnName + "=" + rankPoint + " WHERE uuid=\"" + player.getUUID().toString() + "\"");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}		
	}
	
	/**
	 * 
	 * @param player
	 * @return -1 when player is in the hub, -2 when player lobby is not found.
	 */
	public static int getLobbyId(MinigamePlayer player)
	{
		try(Statement statement = getConnection().createStatement())
		{
			//SELECT lobby FROM minigamecore.players WHERE username="fihgu"
			ResultSet resultSet = statement.executeQuery("SELECT lobby FROM " + DATABASE + "." + PLAYERS_TABLE + " WHERE uuid=\"" + player.getUUID().toString() + "\"");
			
			if(resultSet.next())
			{
				return resultSet.getInt(1);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return -2;
	}
	
	public static void setLobby(MinigamePlayer player, int lobbyId)
	{
		initPlayerData(player);
		
		try(Statement statement = getConnection().createStatement())
		{
			//UPDATE minigamecore.players SET score=50 WHERE username="fihgu";
			statement.execute("UPDATE " + DATABASE + "." + PLAYERS_TABLE + " SET lobby=" + lobbyId + " WHERE uuid=\"" + player.getUUID().toString() + "\"");
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
			ResultSet resultSet = statement.executeQuery("SELECT money FROM " + DATABASE + "." + PLAYERS_TABLE + " WHERE uuid=\"" + player.getUUID().toString() + "\"");
			
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
			statement.execute("UPDATE " + DATABASE + "." + PLAYERS_TABLE + " SET money=" + money + " WHERE uuid=\"" + player.getUUID().toString() + "\"");
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
			statement.execute("ALTER TABLE " + DATABASE + "." + PLAYERS_TABLE + " ADD " + colomnName + " INT DEFAULT 0");
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
			ResultSet resultSet = statement.executeQuery("SELECT uuid FROM " + DATABASE + "." + PLAYERS_TABLE + " WHERE uuid=\"" + player.getUUID().toString() + "\"");
			if(!resultSet.next())
			{
				statement.execute("INSERT INTO " + DATABASE + "." + PLAYERS_TABLE + "(uuid, username) VALUES(\"" + player.getUUID().toString() + "\", \"" + player.getName() +"\")");
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * register a new lobby and return the generated lobby id.
	 */
	public static int registerNewLobby(Minigame minigame, String server)
	{
		try (Statement statement = getConnection().createStatement())
		{
			String minigameId = "undefined";
			if(minigame != null)
			{
				minigameId = minigame.getId();
			}
			statement.execute("INSERT INTO " + DATABASE + "." + LOBBIES_TABLE + "(minigame, server) VALUES(\"" + minigameId + "\", \"" + server +"\")");
			ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
			if(resultSet.next())
			{
				return resultSet.getInt(1);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	/**
	 * @param lobbyId
	 * @return null if lobby is not registered yet, or undefined when the lobby do not have a minigame set yet.
	 */
	public static String getMinigame(int lobbyId)
	{
		String minigame = null;
		
		try (Statement statement = getConnection().createStatement())
		{
			ResultSet resultSet = statement.executeQuery("SELECT minigame FROM " + DATABASE + "." + LOBBIES_TABLE + " WHERE id=" + lobbyId);
			if(resultSet.next())
			{
				return resultSet.getString(1);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return minigame;
	}
	
	public static int getPlayerCount(int lobbyId)
	{
		int playerCount = -1;
		
		try (Statement statement = getConnection().createStatement())
		{
			ResultSet resultSet = statement.executeQuery("SELECT playercount FROM " + DATABASE + "." + LOBBIES_TABLE + " WHERE id=" + lobbyId);
			if(resultSet.next())
			{
				return resultSet.getInt(1);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return playerCount;
	}
	
	public static void setPlayerCount(int lobbyId, int playerCount)
	{
		try(Statement statement = getConnection().createStatement())
		{
			statement.execute("UPDATE " + DATABASE + "." + LOBBIES_TABLE + " SET playercount=" + playerCount + " WHERE id=" + lobbyId);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static String getServer(int lobbyId)
	{
		String server = null;
		
		try (Statement statement = getConnection().createStatement())
		{
			ResultSet resultSet = statement.executeQuery("SELECT server FROM " + DATABASE + "." + LOBBIES_TABLE + " WHERE id=" + lobbyId);
			if(resultSet.next())
			{
				return resultSet.getString(1);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return server;
	}
	
	public static LinkedList<Integer> getLobbyList()
	{
		LinkedList<Integer> list = new LinkedList<Integer>();
		
		try (Statement statement = getConnection().createStatement())
		{
			ResultSet resultSet = statement.executeQuery("SELECT id FROM " + DATABASE + "." + LOBBIES_TABLE);
			while(resultSet.next())
			{
				list.add(resultSet.getInt(1));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * @return the name of lobbyServer that has least registered player playing in a lobby<br>
	 * players who isn't registered in the database does not count.
	 */
	public static String getEmptiestServer()
	{
		ArrayList<String> emptyServers = new ArrayList<String>();
		emptyServers.addAll(NetworkManager.getLobbyServers());
		String emptiest = null;
		try (Statement statement = getConnection().createStatement())
		{
			ResultSet resultSet = statement.executeQuery("SELECT server,SUM(playercount) FROM " + DATABASE + "." + LOBBIES_TABLE + "  GROUP BY server ORDER BY sum(playercount)");
			while(resultSet.next())
			{
				String serverName = resultSet.getString(1);
				if(emptiest == null)
				{
					emptiest = serverName;
				}
				emptyServers.remove(serverName);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		if(emptyServers.size() > 0)
		{
			return emptyServers.get(0);
		}
		else
		{
			return emptiest;
		}
	}
	
	public static void unregisterLobby(int lobbyId)
	{
		try (Statement statement = getConnection().createStatement())
		{
			statement.execute("DELETE FROM " + DATABASE + "." + LOBBIES_TABLE + " WHERE id=" + lobbyId);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
