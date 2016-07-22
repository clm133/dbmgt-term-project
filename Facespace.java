import java.sql.*;
import java.text.ParseException;
import java.util.Scanner;
import java.util.Date;

public class Facespace {

    private static Connection connection;
    private String query;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public static void main(String[] args) throws SQLException {

        String username, password;

        username = "wrk10";
        password = "3718569";

        try {
            //register driver
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

            //set database location
            String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";

            System.out.println("Connecting...");
            //create connection
            connection = DriverManager.getConnection(url, username, password);            
            System.out.println("Connection Successful!");
            
            mainMenu();

        } catch (Exception e) {
            System.out.println("Error connecting to database! " + e.toString());
            System.exit(0);
        }

        try {
            System.out.println("Disconnecting...");
            connection.close();
        } catch (Exception e) {
            System.out.println("Error disconnecting from database!  " + e.toString());
        }

    }

    public static void mainMenu() {

        Scanner input = new Scanner(System.in);

        int quit = 0;
        while (quit == 0) {
            System.out.print("\n");
            System.out.println("1. createUser");
            System.out.println("2. initiateFriendship");
            System.out.println("3. establishFriendship");
            System.out.println("4. displayFriends");
            System.out.println("5. createGroup");
            System.out.println("6. addToGroup");
            System.out.println("7. sendMessageToUser");
            System.out.println("8. sendMessageToGroup");
            System.out.println("9. displayMessages");
            System.out.println("10. displayNewMessages");
            System.out.println("11. searchForUser");
            System.out.println("12. threeDegrees");
            System.out.println("13. topMessagers");
            System.out.println("14. dropUser");
            System.out.println("0. QUIT");
            System.out.println("\nWhat is your choice?: ");

            int choice = input.nextInt();
            Facespace fs = new Facespace();

            while (choice < 0 || choice > 14) {
                System.out.println("Please enter a valid choice!");
                choice = input.nextInt();
            }

            input.nextLine();
            switch (choice) {
                case 1:
					System.out.println("Enter the users full name (First MI Last): ");
					String user = input.nextLine();
					System.out.println("Enter the users email: ");
					String email = input.nextLine();
					System.out.println("Enter the users date of birth (YYYY-MM-DD): ");
					String dob = input.nextLine();
					fs.createUser(user, email, dob);
					break;
                case 2:
					System.out.println("Enter the first users userID: ");
					int user1 = input.nextInt();
					System.out.println("Enter the second users userID: ");
					int user2 = input.nextInt();
					fs.initiateFriendship(user1, user2);
					break;
                case 3:
					System.out.println("Enter the first friends userID: ");
					int friend1 = input.nextInt();
					System.out.println("Enter the second friends userID: ");
					int friend2 = input.nextInt();
					fs.establishFriendship(friend1, friend2);
					break;
                case 4:
					System.out.println("Enter the user name (First MI Last): ");
					String userfriend = input.nextLine();
					fs.displayFriends(userfriend);
					break;
                case 5:
					System.out.println("Enter the group name: ");
					String group = input.nextLine();
					System.out.println("Enter the group description: ");
					String description = input.nextLine();
					System.out.println("Enter the group membership limit: ");
					String limit = input.nextLine();
					fs.createGroup(group, description, limit);
					break;
                case 6:
					System.out.println("Enter the group name: ");
					group = input.nextLine();
					System.out.println("Enter the userID to be added to the group: ");
					user1 = input.nextInt();
					fs.addToGroup(group, user1);
					break;
                case 7:
					System.out.println("Enter message subject: ");
					String subject = input.nextLine();
					System.out.println("Enter message body: ");
					String body = input.nextLine();
					System.out.println("Enter recipient userID: ");
					int recipient = input.nextInt();
					System.out.println("Enter sender userID: ");
					int sender = input.nextInt();
					fs.sendMessageToUser(subject, body, recipient, sender);
					break;
                case 8:
                sendMessageToGroup();
                break;
                case 9:
                displayMessages();
                break;
                case 10:
                displayNewMessages();
                break;
                case 11:
                searchForUser();
                break;
                case 12:
                threeDegrees();
                break;
                case 13:
                topMessagers();
                break;
                case 14:
                dropUser();
                break;
                case 0:
                quit = 1;
                break;
                default:
                break;
            }

        }
    }

    public void createUser(String name, String email, String dob) {

        try {
            String[] names = name.split(" ");

            statement = connection.createStatement();
            query = "SELECT MAX(userID) FROM Users";
            resultSet = statement.executeQuery(query);
            resultSet.next();
            long maxUsers = resultSet.getLong(1);

            java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date bday = new java.sql.Date(df.parse(dob).getTime());
            Date now = new Date();
            String date = df.format(now);
            java.sql.Date login = new java.sql.Date(df.parse(date).getTime());

            String insert = "INSERT INTO Users(userID, fname, mname, lname, email, DOB, loggedIn) VALUES(?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insert);

            preparedStatement.setLong(1, (maxUsers + 1));
            preparedStatement.setString(2, names[0]);
            preparedStatement.setString(3, names[1]);
            preparedStatement.setString(4, names[2]);
            preparedStatement.setString(5, email);
            preparedStatement.setDate(6, bday);
            preparedStatement.setDate(7, login);
            preparedStatement.executeUpdate();
            System.out.println("User successfully created!");
        } catch (Exception e) {
            System.out.println("Error adding user to database: "
                + e.toString());
        } finally {
            try {
                statement.close();
                preparedStatement.close();
            } catch (Exception e) {
                System.out.println("Cannot close statement: " + e.toString());
            }
        }
    }

    public void initiateFriendship(int user1, int user2) {

        try {
            String insertQuery = "INSERT INTO Friendships(friend1, friend2, status, established ) VALUES(?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertQuery);

            preparedStatement.setInt(1, user1);
            preparedStatement.setInt(2, user2);
            preparedStatement.setInt(3, 0);
            preparedStatement.setNull(4, java.sql.Types.DATE);
            preparedStatement.executeUpdate();
            System.out.println("Pending friendship successfully created!");

        } catch (Exception e) {
            System.out.println("Error adding group to database: "
                + e.toString());
        } finally {
            try {
                preparedStatement.close();
            } catch (Exception e) {
                System.out.println("Cannot close statement: " + e.toString());
            }
        }

    }

    public void establishFriendship(int friend1, int friend2) {

        try {
            String update = "UPDATE Friendships SET status = 1, established = systimestamp WHERE ((friend1 = " + friend1 + " AND friend2 = " + friend2 + ") OR (friend2 = " + friend1 + " AND friend1 = " + friend2 + "))";
            preparedStatement = connection.prepareStatement(update);
            preparedStatement.executeUpdate();
            System.out.println("Bilateral friendship created!");

        } catch (Exception e) {
            System.out.println("Error adding group to database: "
                + e.toString());
        } finally {
            try {
                preparedStatement.close();
            } catch (Exception e) {
                System.out.println("Cannot close statement: " + e.toString());
            }
        }

    }

    public void displayFriends(String user) {
        try {
            String[] names = user.split(" ");
            statement = connection.createStatement();
            String select = "SELECT userID FROM Users WHERE fname = '" + names[0] + "' AND mname = '" + names[1] + "' AND lname = '" + names[2] + "'";
            resultSet = statement.executeQuery(select);
            resultSet.next();
            if (resultSet.isAfterLast()) {
                System.out.println("User does not exist!");
            } else {

                int userID = resultSet.getInt(1);

                System.out.print("\n");
                System.out.println("FRIENDSHIPS:");
                System.out.print("---------------------------\n");

                String column;
                select = "SELECT fname, mname, lname FROM Users WHERE userID IN (SELECT friend1 FROM Friendships WHERE friend2 = " + userID + ")";
                resultSet = statement.executeQuery(select);

                while (resultSet.next()) {
                    column = resultSet.getString("fname");
                    System.out.print(column + " ");
                    column = resultSet.getString("mname");
                    System.out.print(column + " ");
                    column = resultSet.getString("lname");
                    System.out.print(column);
                }

                select = "SELECT fname, mname, lname FROM Users WHERE userID IN (SELECT friend2 FROM Friendships WHERE friend1 = " + userID + ")";
                resultSet = statement.executeQuery(select);

                while (resultSet.next()) {
                    column = resultSet.getString("fname");
                    System.out.print(column + " ");
                    column = resultSet.getString("mname");
                    System.out.print(column + " ");
                    column = resultSet.getString("lname");
                    System.out.print(column);
                    System.out.print("\n");
                }

            }

        } catch (Exception e) {
            System.out.println("Error displaying friends: "
                + e.toString());
        } finally {
            try {
                statement.close();
            } catch (Exception e) {
                System.out.println("Cannot close statement: " + e.toString());
            }
        }

    }

    public void createGroup(String name, String description, String limit) {

        try {
            statement = connection.createStatement();
            query = "SELECT MAX(groupID) FROM Groups";
            resultSet = statement.executeQuery(query);
            resultSet.next();
            long maxGroups = resultSet.getLong(1);

            String insert = "INSERT INTO Groups(groupID, name, memLimit, description) VALUES(?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insert);

            preparedStatement.setLong(1, (maxGroups + 1));
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, Integer.parseInt(limit));
            preparedStatement.setString(4, description);
            preparedStatement.executeUpdate();
            System.out.println("Group successfully created!");

        } catch (Exception e) {
            System.out.println("Error adding group to database: "
                + e.toString());
        } finally {
            try {
                statement.close();
                preparedStatement.close();
            } catch (Exception e) {
                System.out.println("Cannot close statement: " + e.toString());
            }
        }

    }

    public void addToGroup(String group, int user) {
        try {
            query = "SELECT COUNT(DISTINCT *) FROM BELONGS_TO WHERE GROUPID = (SELECT GROUPID FROM GROUPS WHERE NAME = ? )";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, group);
            resultSet = preparedStatement.executeQuery();
            Integer memCount = Integer.MAX_VALUE;
            while(resultSet.next()) {
                memCount = resultSet.getInt(1);
            }

            query = "SELECT MEMLIMIT FROM GROUPS WHERE NAME = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, group);
            resultSet = preparedStatement.executeQuery();
            Integer memMax = 0;
            while(resultSet.next()) {
                memMax = resultSet.getInt(1);
            }

            if(memCount >= memMax) {
                System.out.println("That group is already full so the user was not added");
                return;
            }

            query = "INSERT INTO BELONGS_TO SELECT ?, GROUPID FROM GROUPS WHERE NAME = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, user);
            preparedStatement.setString(2, group);
            boolean success = preparedStatement.execute();
            connection.commit();
            System.out.println(success? "The user was added to the group" : "There was an error when adding the user to the group");

        } catch(Exception e) {
            System.out.println("Error adding to group: " + e.toString());
        } finally {
            closeResources();
        }
    }

    public void sendMessageToUser(String subj, String body, int recipient, int sender) {
		try {
			
			//For generating new messageID
			statement = connection.createStatement();
            query = "SELECT MAX(msgID) FROM Messages";
            resultSet = statement.executeQuery(query);
            resultSet.next();
            long totalMessages = resultSet.getLong(1);
			
			//For generating date
			java.sql.Date dateSent = new java.sql.Date(new java.util.Date().getTime());
			
            String insert = "INSERT INTO Messages(msgID, sender, subject, content, dateSent) VALUES(?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insert);

            preparedStatement.setLong(1, (totalMessages + 1));
            preparedStatement.setLong(2, sender);
            preparedStatement.setString(3, subj);
			preparedStatement.setString(4, body);
			preparedStatement.setDate(5, dateSent);
            preparedStatement.executeUpdate();
			preparedStatement.close();
			
			insert = "INSERT INTO Recipients(msgID, recipient) VALUES(?, ?)";
			preparedStatement = connection.prepareStatement(insert);
			preparedStatement.setLong(1, (totalMessages + 1));
            preparedStatement.setLong(2, recipient);
            System.out.println("Message sent successfully!");
			preparedStatement.close();
        } 
		catch (Exception e) {
            System.out.println("Error inserting message into database: "
                + e.toString());
        } finally {
            try {
                statement.close();
                preparedStatement.close();
            } catch (Exception e) {
                System.out.println("Cannot close statement: " + e.toString());
            }
        }
    }

    public static void sendMessageToGroup() {

    }

    public static void displayMessages() {

    }

    public static void displayNewMessages() {

    }

    public static void searchForUser() {

    }

    public static void threeDegrees() {

    }

    public static void topMessagers() {

    }

    public static void dropUser() {

    }

    private void closeResources() {
        try {
            if(statement != null) {
                statement.close();
            }
            if(preparedStatement != null) {
                preparedStatement.close();
            }
            if(resultSet != null) {
                resultSet.close();
            }
        } catch(Exception e) {
            System.out.println("Error when closing resources: " + e.toString());
        }
    }

}