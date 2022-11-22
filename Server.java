package application;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
  static Socket wait = null;
  static List<Socket[]> players = new ArrayList<>();

  public static void main(String[] args) {
    ServerSocket server;
    try {
      server = new ServerSocket(10000);
      while (true) {
        Socket socket = server.accept();
        System.out.println("login");
        new ServerThread(socket);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static class ServerThread extends Thread {
    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    ObjectOutputStream outputStream1;

    public ServerThread(Socket socket) {
      this.socket = socket;
      start();
    }

    public void run() {
      try {
        if (wait == null) {
          wait = socket;
          outputStream = socket.getOutputStream();
          outputStream1 = new ObjectOutputStream(outputStream);
          outputStream1.writeObject("There's no match. Please wait a minute.");
        } else {
          outputStream = socket.getOutputStream();
          outputStream1 = new ObjectOutputStream(outputStream);
          outputStream1.writeObject("start f");
          outputStream = wait.getOutputStream();
          outputStream1 = new ObjectOutputStream(outputStream);
          outputStream1.writeObject("start t");
          players.add(new Socket[]{wait, socket});
          wait = null;
        }
        while (true) {
          try {
            Object message;
            inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            message = objectInputStream.readObject();
            if (message instanceof String[]) {
              String[] m = (String[]) message;
                if (m[0].equals("plant")) {
                  for (Socket[] p : players) {
                    if (p[0].equals(socket)) {
                      outputStream = p[1].getOutputStream();
                      outputStream1 = new ObjectOutputStream(outputStream);
                      outputStream1.writeObject(m);
                      break;
                    }
                    if (p[1].equals(socket)) {
                      outputStream = p[0].getOutputStream();
                      outputStream1 = new ObjectOutputStream(outputStream);
                      outputStream1.writeObject(m);
                      break;
                    }
                  }
                }
            }
            else if(message instanceof String){
              String m = (String) message;
              for (Socket[] p : players) {
                if (p[0].equals(socket)) {
                  outputStream = p[1].getOutputStream();
                  outputStream1 = new ObjectOutputStream(outputStream);
                  outputStream1.writeObject(m);
                  break;
                }
                if (p[1].equals(socket)) {
                  outputStream = p[0].getOutputStream();
                  outputStream1 = new ObjectOutputStream(outputStream);
                  outputStream1.writeObject(m);
                  break;
                }
              }

            }
          } catch (Exception ignored) {

          }
        }

      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        close();
      }
    }

    private void close() {
      try {
        outputStream.close();
        inputStream.close();
        socket.close();
      } catch (Exception ignored) {

      }
    }
  }
}
