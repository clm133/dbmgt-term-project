
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
                    displayFriends();
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
                    addToGroup();
                    break;
                case 7:
                    sendMessageToUser();
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
        } catch (SQLException | ParseException e) {
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

    public static void displayFriends() {

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

        } catch (SQLException | NumberFormatException e) {
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

    public static void addToGroup() {

    }

    public static void sendMessageToUser() {

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

}
