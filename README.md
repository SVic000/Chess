# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
## Client and Server Diagram Share Link

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZMAcygQArtgDEAFgDMADgBMAThAxbyMwAswHQQrQwAlFDMkVTAoOSQINExEVFIAWgA+ckoaKAAuGABtAAUAeTIAFQBdGAB6KwMoAB00AG8AInrKNGAAWxQ23LaYNoAaYdx1AHdoDgGh0eGUHuAkBDnhgF9MYRyYDNZ2Lkp89s6obr71hbaJ1WmoWcHhsballbWntq22Tm5YfZ2onyUAiUTAlAAFOFItFKOEAI4haIASm22VEe0ysnkShU6nyZhQYAAqg0IWcLihUdjFMo1KpMUYdLkAGJITgwUmUGkwHQATxgFN6Yh0IOAAGtOQ0YJMkGA-IKGpSYMAEGKOAKUAAPMEaGm4+l7AHolT5LlQGlokQqI2ZHbHGAKNUoYAa8oS9AAUS1KmwBASVt2qX2yXQYHy9gADI5mu0+upgISBsNPVBLHlFV1hSrna6BfJxegvph0BxMPq6epbVlrSh8mgrAgEIGMfsK3jVLkQGLwebyQ0adTtAaq-tjLkFBwOFLudoWza28PK53uy7wQorPKIcBN35B+Wlx3GePJ9ON-LLYCF-sfkcM9CwXC1I2sLe-tX7RnTkrhUN8vMXh3eVyggQs0D-YZNkDShq1DDB8lcSNI1jDof0uGB-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
