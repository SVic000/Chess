package client;

import httpobjs.LoginRequest;
import httpobjs.RegisterRequest;
import io.javalin.http.ForbiddenResponse;
import model.GameData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ClientMain {
    private static boolean isLoggedIn = false;
    private static List<GameData> lastListCall = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Welcome to Chess ♕! Sign in to start.");
        signedOutREPL();
    }

    public static void signedOutREPL() {
       System.out.print(menu());

       Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("4")) {
            String line = scanner.nextLine();

            try {
                result = eval(line, scanner);
                if(!result.equals("4")) {
                    System.out.println(result);
                    System.out.println();
                    if(line.equals("2")) {
                        System.out.println(menu());
                    }
                }

                if(isLoggedIn) {
                    signedInREPL();
                }
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println("Goodbye!");
    }

    public static void signedInREPL() {
        System.out.print(menu());

        // FINISH THIS IMPLEMENTATION AS WELL
    }


    public static String menu() {
        if(!isLoggedIn) {
            return """
                    1. Login
                    2. Register
                    3. Help
                    4. Quit
                    
                    """;
        }
        return """
                1. Create Game
                2. List Games
                3. Play Game
                4. Observe Game
                5. Logout
                6. Help
                
                """;
    }

    private static String eval(String line, Scanner scanner) {
       try {
           String[] token = line.toLowerCase().split(" ");
           String cmd = (token.length > 0) ? token[0] : "3";
           if(!isLoggedIn) {
               return switch (cmd) {
                   case "1" -> logIn(scanner);
                   case "2" -> register(scanner);
                   case "4" -> "4";
                   default -> help();
               };
           } else {
               return switch (cmd) {
                   case "1" -> createGame(scanner);
                   case "2" -> listGames();
                   case "3" -> joinGame(scanner);
                   case "4" -> observerGame(scanner);
                   case "5" -> logOut();
                   default -> help();
               };
           }
       } catch (Exception ex) {
           return ex.getMessage();
       }
    }

    private static String logOut() {
        return "Not implemented";
    }

    private static String observerGame(Scanner scanner) {
        assertSignedIn();
        return "Not implemented";
    }

    private static String joinGame(Scanner scanner) {
        assertSignedIn();
        return "Not implemented";
    }

    private static String listGames() {
        assertSignedIn();
        return "Not implemented";
    }

    private static String createGame(Scanner scanner) {
        assertSignedIn();
        return "Not implemented";
    }

    public static String logIn(Scanner scanner) {
        System.out.print("Enter your username: ");
        String[] username = scanner.nextLine().split(" ");
        System.out.print("Enter your password: ");
        String[] password = scanner.nextLine().split(" ");

        LoginRequest req = new LoginRequest(Arrays.toString(username), Arrays.toString(password));
        // SERVER FACADE LOGIN REQ GOES HERE

        isLoggedIn = true; // if there's an error, don't change this var

        System.out.println();
        return "Successfully logged in! Hi " + Arrays.toString(username);
    }

    public static String register(Scanner scanner) {
        System.out.print("Enter a username: ");
        String[] username = scanner.nextLine().split(" ");
        System.out.print("Enter your email: ");
        String[] email = scanner.nextLine().split(" ");
        System.out.print("Enter your password: ");
        String[] password = scanner.nextLine().split(" ");

        RegisterRequest req = new RegisterRequest(
                Arrays.toString(username),
                Arrays.toString(password),
                Arrays.toString(email));
        // SERVER FACADE REGISTER CALL GOES HERE

        // if there's an error, make sure to change this return
        System.out.println();
        return "Registered new user " + Arrays.toString(username) +", please make sure to login.";
    }

    public static String help() {
        if(!isLoggedIn) {
            return """
                    Make sure to only type the number of the command you're interested in.
                    
                    1. Login - Login to play chess
                    2. Register - to create an account
                    3. Help - to get help with possible commands
                    4. Quit - to exit the program
                    
                    """;
        }
        return """
                Make sure to only type the number of the command you're interested in.
                
                1. Create Game - Create a new game to play
                2. List Games - See a List of all games in play
                3. Play Game - Join a specified game
                4. Observe Game - See a current game that's being played
                5. Logout - Logout out of your account
                6. Help - to get help with possible commands
             
                """;
    }

    private static void assertSignedIn() throws ForbiddenResponse {
        if (!isLoggedIn) {
            throw new ForbiddenResponse("Error: You are not logged in");
        }
    }
}
