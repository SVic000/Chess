package client;

import httpobjs.*;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.HttpResponseException;
import model.GameData;
import ui.DrawChessBoard;

import java.util.*;

public class ClientMain {
    private static final ServerFacade SERVER = new ServerFacade("http://localhost:8080");

    private static boolean isLoggedIn = false;
    private static List<GameData> lastListCall = new ArrayList<>();
    private static String authToken = "";

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
                if(line.equals("-10")) {
                    SERVER.clear();
                    System.out.println("hidden clear worked");
                }
                if(!result.equals("4")) {
                    System.out.println(result);
                    System.out.println();
                }
                if(isLoggedIn) {
                    signedInREPL(scanner);
                    result = "";
                    System.out.print(menu());
                }
            }catch (Throwable e){
                var msg = e.getMessage();
                System.out.println(msg);
                System.out.println(menu());
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
                        if(line.equals("2")) {
                            System.out.println();
                            for(GameData data : lastListCall) {
                                System.out.print("Game Name: ");
                                System.out.print(data.gameName());
                                System.out.print(" Game ID: ");
                                System.out.print(data.gameID());
                                System.out.print(" White player: ");
                                System.out.print(data.whiteUsername());
                                System.out.print(" Black player: ");
                                System.out.print(data.blackUsername());
                                System.out.println();
                            }
                        }
                        if(!line.equals("6")) {
                            System.out.print(menu());
                        }

                        if(line.equals("-10")) {
                            SERVER.clear();
                            System.out.println("Secret clear worked");
                        }
                    }
                }
//                    if(line.equals("3")) {
//                        continue;  add call to game REPL here (Phase 6)
//                    }
            } catch (Throwable e){
                var msg = e.getMessage();
                System.out.println(msg);
                System.out.println(menu());
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
        try {
            SERVER.logout(authToken);
        } catch (HttpResponseException e) {
            return "Server Error: Unable to log you out, try again.";
        }
        isLoggedIn = false;
        authToken = "";
        return "5";
    }

    private static String observerGame(Scanner scanner) {
        assertSignedIn();
        if(lastListCall == null) {
            return "You must call list games before you enter a game!";
        }

        System.out.print("Enter the game ID you'd like to observe: ");
        String[] input = scanner.nextLine().split(" ");
        try {
            int gameID = Integer.parseInt(input[0].trim());
            GameData game = lastListCall.stream()
                    .filter(g -> g.gameID() == gameID)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Game not found"));

            System.out.println("Observing " + game.gameName());
            new DrawChessBoard(game.game());
            System.out.println();
            System.out.println();
            return "Observing " + game.gameName();
        } catch (NumberFormatException ex){
            return "Game ID is not valid, try again.";
        }
    }

    private static String joinGame(Scanner scanner) {
        assertSignedIn();
        // will be joining another REPL here in phase 6
        System.out.print("Enter the game ID of the game you'd like to join: ");
        String input = scanner.nextLine().trim();
        int gameID;
        try {
            gameID = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return "That's not a valid game ID, try again.";
        }
        System.out.print("Enter the color you'd like to join as (WHITE/BLACK): ");
        String color = scanner.nextLine().trim().toUpperCase();

        JoinGameRequest req = new JoinGameRequest(color, gameID);

        JoinGameResult res = SERVER.joinGame(req, authToken);
        if(!Objects.equals(res.message(), "")) {
            if(res.message().contains("500")) {
                return "Server Error: Please try again.";
            }
            return res.message();
        }

        GameData game = lastListCall.stream()
                .filter(g -> g.gameID() == gameID)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Game not found"));

        new DrawChessBoard(game.game(), color);
        System.out.println();
        System.out.println();

        return "Successfully joined " + game.gameName() + " game!";
    }

    private static String listGames() {
        assertSignedIn();

        httpobjs.ListGameResult res = SERVER.listGames(authToken);
        lastListCall = (List<GameData>) res.games();
        return "Here is the list of current games: ";
    }

    private static String createGame(Scanner scanner) {
        assertSignedIn();
        System.out.print("Enter your desired game name: ");
        String name = scanner.nextLine().trim();
        if(name.isEmpty()) {
            return "Not a valid game name, try again";
        }
        CreateGameRequest req = new CreateGameRequest(name);
        CreateGameResult res = SERVER.createGame(req, authToken);

        if(!Objects.equals(res.message(), "")) {
            if(res.message().contains("500")) {
                return "Server Error. Please try again";
            }
            return res.message();
        }

        return "Created " + name + " game successfully.";
    }

    public static String logIn(Scanner scanner) {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine().trim();

        LoginRequest req = new LoginRequest(username, password);

        LoginResult res = SERVER.login(req);

        if(!Objects.equals(res.message(), "")) {
            if(res.message().contains("500")) {
                return "Server Error. Please try again.";
            }
            return res.message();
        }

        authToken = res.authToken();
        isLoggedIn = true;

        System.out.println();
        return "Successfully logged in! Hi " + username + "!";
    }

    public static String register(Scanner scanner) {
        System.out.print("Enter a username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine().trim();

        RegisterRequest req = new RegisterRequest(username,password,email);

        RegisterResult res = SERVER.register(req);
        if(!Objects.equals(res.message(), "")) {
            if(res.message().contains("500")) {
                return "Server Error. Please try again";
            }
            return res.message();
        }

        isLoggedIn = true;
        authToken = res.authToken();

        System.out.println();
        return "Registered new user " + username +". Welcome!";
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
