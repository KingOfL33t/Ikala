
package com.ikalagaming.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Properties;

import com.ikalagaming.util.StringOperations;

/**
 * Allows the program to connect with an SQL database.
 * 
 * @author Ches Burks
 * 
 */
public class DatabaseInterface {

	String url = "localhost";
	int port = 3306;
	String username = "Your_Username_Here";
	String password = " Your_Password_Here ";
	String database = "main";
	Connection connection;

	/**
	 * Constructs a new DatabaseInterface with the given information.
	 * 
	 * @param url the url to connect to (such as "localhost")
	 * @param port the port number of the server (such as "3306")
	 * @param username the username for this connection (such as "admin")
	 * @param password the password for this connection (such as "password123")
	 * @param database the name of the database to connect to
	 */
	public DatabaseInterface(String url, int port, String username,
			String password, String database) {
		this.url = url;
		this.port = port;
		this.username = username;
		this.password = password;
		this.database = database;
	}

	/**
	 * Strings that are not permitted inside of a query (as they are operators
	 * in SQL).
	 */
	private static final String[] invalidStrings = {"||", "-", "*", "/", "<>",
			"<", ">", " ", ",", "=", "<=", ">=", "~=", "!=", "^=", "(", ")"};

	/**
	 * Performs a safe database query using a {@link PreparedStatement}. The
	 * query should be a valid SQL query with question marks ("?") where the
	 * arguments should be inserted. String arguments should be supplied for
	 * each question mark. Returns a {@link ResultSet} that is returned by the
	 * query. Results is not guaranteed to be non-null. The results set should
	 * be closed as soon as possible to minimize the resources loaded.
	 * 
	 * @param query the query to send to the database
	 * @param arguments the arguments for the query
	 * @return the set that is returned
	 * @throws InvalidQuery if the query was badly formatted
	 * @throws SQLException if a database access error occurs; this method is
	 *             called on a closed PreparedStatement or the SQL statement
	 *             does not return a ResultSet object
	 * @throws SQLTimeoutException if a database access error occurs; this
	 *             method is called on a closed PreparedStatement or the SQL
	 *             statement does not return a ResultSet object
	 */
	public ResultSet sendSafeQuery(String query, Object... arguments)
			throws InvalidQuery, SQLException, SQLTimeoutException {
		PreparedStatement statement = null;
		ResultSet results = null;
		try {
			statement = connection.prepareStatement(query);

			int occurances = StringOperations.countOccurances(query, "?");

			if (arguments.length < occurances) {
				if (statement != null) {
					statement.close();
				}
				throw new InvalidQuery("Not enough arguments");
			}

			int i;
			for (i = 0; i < arguments.length; ++i) {
				if (!setObject(statement, arguments[i], i)) {
					throw new SQLException("Invalid argument type");
				}
			}
			results = statement.executeQuery();
		}
		catch (SQLException exception) {
			if (statement != null) {
				statement.close();
			}
			throw exception;
		}
		catch (InvalidQuery e) {
			if (statement != null) {
				statement.close();
			}
			throw e;
		}
		return results;
	}

	/**
	 * Sets the given parameter of the PreparedStatement to the given object. If
	 * the object's type can be determined, that objects setter is used.
	 * Otherwise, the object is set using the
	 * {@link PreparedStatement#setObject(int, Object)} method. The known types
	 * are:
	 * <ul>
	 * <li>BigDecimal</li>
	 * <li>boolean</li>
	 * <li>byte</li>
	 * <li>byte[]</li>
	 * <li>double</li>
	 * <li>float</li>
	 * <li>int</li>
	 * <li>long</li>
	 * <li>short</li>
	 * <li>String</li>
	 * </ul>
	 * 
	 * @param statement the statement to change
	 * @param toSet the object to set
	 * @param index the index of the parameter to set
	 * @return false if an exception was thrown setting the parameter
	 */
	private boolean setObject(PreparedStatement statement, Object toSet,
			int index) {
		try {
			if (toSet instanceof BigDecimal) {
				statement.setBigDecimal(index, (BigDecimal) toSet);
			}
			else if (toSet instanceof Boolean) {
				statement.setBoolean(index, (boolean) toSet);
			}
			else if (toSet instanceof Byte) {
				statement.setByte(index, (byte) toSet);
			}
			else if (toSet instanceof Byte[]) {
				statement.setBytes(index, (byte[]) toSet);
			}
			else if (toSet instanceof Double) {
				statement.setDouble(index, (double) toSet);
			}
			else if (toSet instanceof Float) {
				statement.setFloat(index, (float) toSet);
			}
			else if (toSet instanceof Integer) {
				statement.setInt(index, (int) toSet);
			}
			else if (toSet instanceof Long) {
				statement.setLong(index, (long) toSet);
			}
			else if (toSet instanceof Short) {
				statement.setShort(index, (short) toSet);
			}
			else if (toSet instanceof String) {
				statement.setString(index, (String) toSet);
			}
			else {
				statement.setObject(index, toSet);
			}
		}
		catch (SQLException e) {
			return false;
		}
		return true;
	}

	/**
	 * Connect to the database using the set properties.
	 * 
	 * @return true if the connection succeeded
	 */
	public boolean establishConnection() {
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.username);
		connectionProps.put("password", this.password);
		try {
			connection =
					DriverManager
							.getConnection("jdbc:" + "mysql" + "://" + this.url
									+ ":" + this.port + "/", connectionProps);
		}
		catch (SQLException e) {
			// TODO log error
			e.printStackTrace();
			return false;
		}
		System.out.println("Connected to database");
		return true;
	}

	/**
	 * Closes the connection.
	 */
	public void closeConnection() {
		try {
			connection.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the input is valid for sending to an SQL database.
	 * 
	 * @param input the string to test
	 * @return true if it is valid, otherwise false
	 */
	public static boolean isValidInput(String input) {
		if (input.length() <= 0) {
			// has to be a valid length
			return false;
		}
		for (String item : invalidStrings) {
			if (input.contains(item)) {
				return false;
			}
		}
		return true;
	}

}
