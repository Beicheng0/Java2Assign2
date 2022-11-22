package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;


public class Client extends Application {
    static Socket client;
    Game game;
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage primaryStage) {
        try {
            client = new Socket(InetAddress.getLocalHost(), 10000);
            new Thread(() -> {
                try {
                    while (true) {
                        InputStream inputStream = client.getInputStream();
                        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                        String message = (String) objectInputStream.readObject();
                        String[] m = message.split(" ");
                        if (m[0].equals("start")) {
                            Platform.runLater(() -> {
                                game = new Game((!m[1].equals("f")), client);
                                game.initial();
                                game.start();
                            });
                            while (true) {

                                Object me;
                                try {
                                    InputStream input = client.getInputStream();
                                    ObjectInputStream objectInput = new ObjectInputStream(input);
                                    me = objectInput.readObject();
                                    if (me instanceof String[]) {
                                        String[] mes = (String[]) me;
                                        if (mes[0].equals("plant")) {
                                            String[] p = mes[1].split(" ");
                                            Platform.runLater(() -> {
                                                game.chessBoard[Integer.parseInt(p[0])][Integer.parseInt(p[1])] = game.player ? 2 : 1;
                                                game.TURN = !game.TURN;
                                                game.drawChess();
                                                game.isTie();
                                                game.isWon1(false);
                                                game.isWon2(false);
                                            });
                                        }
                                    } else if (me instanceof String) {
                                        String mes = (String) me;
                                        if (mes.equals("QUIT")) {
                                            System.out.println("Opponent is missing!");
                                            System.exit(0);
                                        }
                                    }
                                } catch (IOException | ClassNotFoundException e) {
                                    System.out.println("Sorry! The sever is die!");
                                    System.exit(0);
                                }
                            }
                        } else {
                            System.out.println("There's no match. Please wait a minute.");
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Sorry! The sever is die!");
                    System.exit(0);
                }
            }).start();

        } catch (Exception e) {
            System.out.println("Sorry! The sever is die!");
            System.exit(0);
        }
    }
}