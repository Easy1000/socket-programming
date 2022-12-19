import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

  private Socket socket;
  private BufferedReader bufferedReader;
  private BufferedWriter bufferedWriter;
  private String username;

  public Client(Socket socket, String username) {
    try {
      this.socket = socket;
      this.bufferedWriter =
        new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      this.bufferedReader =
        new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.username = username;
    } catch (IOException e) {
      closeEverything(socket, bufferedReader, bufferedWriter);
    }
  }

  public void sendMessage() {
    try {
      bufferedWriter.write(username);
      bufferedWriter.newLine();
      bufferedWriter.flush();

      Scanner scanner = new Scanner(System.in);
      while (socket.isConnected()) {
        String messageToSend = scanner.nextLine();
        // if (messageToSend == "/exit") {
        //   closeEverything(socket, bufferedReader, bufferedWriter);
        // }
        bufferedWriter.write(username + ": " + messageToSend);
        bufferedWriter.newLine();
        bufferedWriter.flush();
        // scanner.close();
      }
    } catch (IOException e) {
      closeEverything(socket, bufferedReader, bufferedWriter);
    }
  }

  public void listenForMessage() {
    new Thread(
      new Runnable() {
        // @override
        public void run() {
          String msgFromGroupChat;

          while (socket.isConnected()) {
            try {
              msgFromGroupChat = bufferedReader.readLine();
              System.out.println(msgFromGroupChat);
            } catch (IOException e) {
              closeEverything(socket, bufferedReader, bufferedWriter);
            }
          }
        }
      }
    )
      .start();
  }

  public void closeEverything(
    Socket socket,
    BufferedReader bufferedReader,
    BufferedWriter bufferedWriter
  ) {
    try {
      if (bufferedReader != null) {
        bufferedReader.close();
      }
      if (bufferedWriter != null) {
        bufferedWriter.close();
      }
      if (socket != null) {
        socket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    try {
      Scanner scanner = new Scanner(System.in);
      System.out.println("Enter your username for the group chat: ");
      String username = scanner.nextLine();
      // Make sure to change the ip address and port below to match the server's ip
      // and port
      // The server's port is located in Server.java
      // If you're just testing it and run it on your own computer, the default is
      // localhost
      Socket socket = new Socket("192.168.1.21", 3000);
      Client client = new Client(socket, username);
      client.listenForMessage();
      client.sendMessage();
      // scanner.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
