package client;

import chess.ChessGame;
import httpobjs.CreateGameRequest;
import httpobjs.JoinGameRequest;
import httpobjs.LoginRequest;
import httpobjs.RegisterRequest;
import io.javalin.http.ForbiddenResponse;
import model.GameData;
import ui.DrawChessBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ClientMain {
    private static boolean isLoggedIn = false;
    private static List<GameData> lastListCall = new ArrayList<>();
    private static String loggedInUser = null;
    private static String authToken = null;

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
                    signedInREPL(scanner);
                    result = "";
                    System.out.print(menu());
                }
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println("Goodbye!");
    }

    public static void signedInREPL(Scanner scanner) {
        System.out.print(menu());

        var result = "";
        while(!result.equals("5")) {
            String line = scanner.nextLine();

            try {
                result = eval(line, scanner);
                if(!result.equals("5")) {
                    System.out.println(result);
                    System.out.println();
                    if(!line.equals("3")) {
                        if(!line.equals("6")) {
                            System.out.print(menu());
                        }
                    }
                }
                if(line.equals("3")) {
                    continue; // add call to game REPL here (Phase 6)
                }
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println("Successfully logged out.");
        System.out.println();
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
        assertSignedIn();
        // server call to logout

        isLoggedIn = false;
        authToken = null;
        loggedInUser = null;
        return "5";
    }

    private static String observerGame(Scanner scanner) {
        assertSignedIn();
        System.out.print("Enter the game id you'd like to observe: ");
        String[] input = scanner.nextLine().split(" ");
        try {
            int gameID = Integer.parseInt(Arrays.toString(input));
            if(lastListCall == null) {
                listGames();
            }
            GameData game = lastListCall.get(gameID);
            System.out.println("Observing " + game.gameName());
            new DrawChessBoard(game.game());
            return "Observing " + game.gameName();
        } catch (NumberFormatException ex){
            return "Game ID is not valid, try again.";
        }
    }

    private static String joinGame(Scanner scanner) {
        assertSignedIn();
        // will be joining another REPL1 here in phase 6
        System.out.print("Enter the game ID of the game you'd like to join: ");
        String[] input = scanner.nextLine().split(" ");
        int gameID = -1;
        try {
            gameID = Integer.parseInt(Arrays.toString(input));
        } catch (NumberFormatException e) {
            return "That's not a valid game ID, try again.";
        }
        System.out.print("Enter the color you'd like to join as (WHITE/BLACK): ");
        String color = Arrays.toString(scanner.nextLine().toUpperCase().split(" "));

        JoinGameRequest req = new JoinGameRequest(color, gameID);
        // SERVER FACADE CALL HERE

        GameData game = lastListCall.get(gameID); // if error, don't do this line
        new DrawChessBoard(game.game(), color); // or this line

        return "Successfully joined " + game.gameName() + " game!";
    }

    private static String listGames() {
        assertSignedIn();

        // SERVER FACADE CALL HERE
        // WILL CHANGE VALUE OF LASTLISTCALL VAR

        return "Not implemented until Facade is built";
    }

    private static String createGame(Scanner scanner) {
        assertSignedIn();
        System.out.print("Enter your desired game name: ");
        String[] name = scanner.nextLine().split(" ");
        if(name.length == 0) {
            return "Not a valid game name, try again";
        }
        CreateGameRequest req = new CreateGameRequest(Arrays.toString(name));
        // MAKE SERVER CALL HERE
        // IF ERROR, THEN YEAH

        return Arrays.toString(name) + " game successfully created with a game ID of: "; // add response here
    }

    public static String logIn(Scanner scanner) {
        System.out.print("Enter your username: ");
        String[] username = scanner.nextLine().split(" ");
        System.out.print("Enter your password: ");
        String[] password = scanner.nextLine().split(" ");

        LoginRequest req = new LoginRequest(Arrays.toString(username), Arrays.toString(password));
        // SERVER FACADE LOGIN REQ GOES HERE
        // Get the token string back...?

        isLoggedIn = true; // if there's an error, don't change this var
        loggedInUser = Arrays.toString(username); // also change this since they didn't log in

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
