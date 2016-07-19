
import java.sql.*;
import java.util.Scanner;

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

            int choice = input.nextInt();

            while (choice < 0 || choice > 14) {
                System.out.println("Please enter a valid choice!");
                choice = input.nextInt();
            }

            switch (choice) {
                case 1:
                    createUser();
                    break;
                case 2:
                    initiateFriendship();
                    break;
                case 3:
                    establishFriendship();
                    break;
                case 4:
                    displayFriends();
                    break;
                case 5:
                    createGroup();
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

    public static void createUser() {

    }

    public static void initiateFriendship() {

    }

    public static void establishFriendship() {

    }

    public static void displayFriends() {

    }

    public static void createGroup() {

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
