package application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Game {
    private Pane pane = new Pane();
    private Stage primaryStage;
    private static final int PLAY_1 = 1;
    private static final int PLAY_2 = 2;
    private static final int EMPTY = 0;
    private static final int BOUND = 90;
    private static final int OFFSET = 15;
    boolean TURN = false;
    final int[][] chessBoard = new int[3][3];
    final boolean[][] flag = new boolean[3][3];
    boolean player;
    Socket socket;

    public Game(boolean p, Socket s) {
        player = p;
        socket = s;
    }

    public void initial() {
        Thread a = new Thread(() -> Platform.runLater(() -> {
            primaryStage = new Stage();
            primaryStage.setTitle("Tic Tac Toe");
            primaryStage.getIcons().add(new Image("file:C:\\Users\\LENOVO\\Desktop\\icon.jpg"));
            pane = new Pane();
            pane.setPrefWidth(300);
            pane.setPrefHeight(300);
            Line s1 = new Line();
            s1.setStartX(105);
            s1.setEndX(105);
            s1.setStartY(0);
            s1.setEndY(300);
            s1.setStroke(Color.GRAY);
            Line s2 = new Line();
            s2.setStartX(195);
            s2.setEndX(195);
            s2.setStartY(0);
            s2.setEndY(300);
            s2.setStroke(Color.GRAY);
            Line h1 = new Line();
            h1.setStartX(0);
            h1.setEndX(300);
            h1.setStartY(105);
            h1.setEndY(105);
            h1.setStroke(Color.GRAY);
            Line h2 = new Line();
            h2.setStartX(0);
            h2.setEndX(300);
            h2.setStartY(195);
            h2.setEndY(195);
            h2.setStroke(Color.GRAY);
            pane.getChildren().addAll(h1, h2, s2, s1);
            Scene scene = new Scene(pane);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            primaryStage.setOnCloseRequest(event -> {
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    String out = "QUIT";
                    outputStream.writeObject(out);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.exit(0);
                }
            });
            Thread.currentThread().stop();
        }));
        a.start();
    }

    public void start() {
        new Thread(() -> Platform.runLater(() -> {
            pane.setOnMouseClicked(event -> {
                int x = (int) (event.getX() / BOUND);
                int y = (int) (event.getY() / BOUND);
                if (refreshBoard(x, y)) {
                    String point = x + " " + y;
                    OutputStream outputStream;
                    try {
                        outputStream = socket.getOutputStream();
                        ObjectOutputStream outputStream1 = new ObjectOutputStream(outputStream);
                        String[] messages = new String[2];
                        messages[0] = "plant";
                        messages[1] = point;
                        outputStream1.writeObject(messages);
                        System.out.println(point);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    TURN = !TURN;
                    isWon1(true);
                    isWon2(true);
                    isTie();
                }
            });
        })).start();
    }

    public void drawChess() {
        for (int i = 0; i < chessBoard.length; i++) {
            for (int j = 0; j < chessBoard[0].length; j++) {
                if (flag[i][j]) {
                    continue;
                }
                switch (chessBoard[i][j]) {
                    case PLAY_1:
                        drawCircle(i, j);
                        break;
                    case PLAY_2:
                        drawLine(i, j);
                        break;
                    case EMPTY:
                        break;
                    default:
                        System.err.println("Invalid value!");
                }
            }
        }
    }

    private void drawCircle(int i, int j) {
        Circle circle = new Circle();
        pane.getChildren().add(circle);
        circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
        circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
        circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
        circle.setStroke(Color.RED);
        circle.setFill(Color.TRANSPARENT);
        flag[i][j] = true;
    }

    private void drawLine(int i, int j) {
        Line line_a = new Line();
        Line line_b = new Line();
        pane.getChildren().add(line_a);
        pane.getChildren().add(line_b);
        line_a.setStartX(i * BOUND + OFFSET * 1.5);
        line_a.setStartY(j * BOUND + OFFSET * 1.5);
        line_a.setEndX((i + 1) * BOUND + OFFSET * 0.5);
        line_a.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_a.setStroke(Color.BLUE);
        line_b.setStartX((i + 1) * BOUND + OFFSET * 0.5);
        line_b.setStartY(j * BOUND + OFFSET * 1.5);
        line_b.setEndX(i * BOUND + OFFSET * 1.5);
        line_b.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_b.setStroke(Color.BLUE);
        flag[i][j] = true;
    }

    private boolean refreshBoard(int x, int y) {
        if (x >= 0 && x <= 2 && y >= 0 && y <= 2) {
            if (chessBoard[x][y] == EMPTY && player == TURN) {
                chessBoard[x][y] = TURN ? PLAY_1 : PLAY_2;
                drawChess();
                return true;
            }
        }
        return false;
    }

    public boolean isWon1(boolean win) {
        for (int i = 0; i < 3; i++) {
            if (chessBoard[i][0] == PLAY_1 && chessBoard[i][1] == PLAY_1 && chessBoard[i][2] == PLAY_1) {
                System.out.println(win ? "You win" : "You lose");
                TURN = !player;
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (chessBoard[0][i] == PLAY_1 && chessBoard[1][i] == PLAY_1 && chessBoard[2][i] == PLAY_1) {
                System.out.println(win ? "You win" : "You lose");
                TURN = !player;
                return true;
            }
        }
        if (chessBoard[0][0] == PLAY_1 && chessBoard[1][1] == PLAY_1 && chessBoard[2][2] == PLAY_1) {
            TURN = !player;
            System.out.println(win ? "You win" : "You lose");
            return true;
        }
        if (chessBoard[2][0] == PLAY_1 && chessBoard[1][1] == PLAY_1 && chessBoard[0][2] == PLAY_1) {
            System.out.println(win ? "You win" : "You lose");
            TURN = !player;
            return true;
        }
        return false;
    }

    public boolean isWon2(boolean win) {
        for (int i = 0; i < 3; i++) {
            if (chessBoard[i][0] == PLAY_2 && chessBoard[i][1] == PLAY_2 && chessBoard[i][2] == PLAY_2) {
                TURN = !player;
                System.out.println(win ? "You win" : "You lose");
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (chessBoard[0][i] == PLAY_2 && chessBoard[1][i] == PLAY_2 && chessBoard[2][i] == PLAY_2) {
                TURN = !player;
                System.out.println(win ? "You win" : "You lose");
                return true;
            }
        }
        if (chessBoard[0][0] == PLAY_2 && chessBoard[1][1] == PLAY_2 && chessBoard[2][2] == PLAY_2) {
            TURN = !player;
            System.out.println(win ? "You win" : "You lose");
            return true;
        }
        if (chessBoard[2][0] == PLAY_2 && chessBoard[1][1] == PLAY_2 && chessBoard[0][2] == PLAY_2) {
            System.out.println(win ? "You win" : "You lose");
            TURN = !player;
            return true;
        }
        return false;
    }

    public boolean isTie() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (chessBoard[i][j] == 0) {
                    return false;
                }
            }
        }
        if (!isWon2(true) && !isWon1(true)) {
            System.out.println("It is a tie");
            TURN = !player;
            return true;
        } else {
            return false;
        }
    }

}
