import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ComThread extends Thread{
    ComThread(ChestCollection curSet, Socket socket) {
        this.curSet = curSet;
        this.socket = socket;
    }

    private ChestCollection curSet;
    private Socket socket;
    private JsonParcerofComands parser = new JsonParcerofComands();

    @Override
    public void run() {

        try (InputStream in = socket.getInputStream();
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            if(!socket.isClosed()) {

                System.out.println("Сервер работает...");
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try{
                    byte[] buf = new byte[32 * 1024];
                    int readBytes = in.read(buf);
                    String line = new String(buf, 0, readBytes);
                    Scanner cmdScanner = new Scanner(line);
                    cmdScanner.useDelimiter(" ");
                    String curCmd = cmdScanner.next();
                    String stringJson = cmdScanner.next();

                    try {
                        parser.command(curCmd, stringJson, curSet);
                        curSet.writeElements();

                        out.writeObject(curSet.returnObjects());
                        out.flush();
                    } catch (NoSuchElementException e) {
                        if (curCmd.equals("exit")) {
                            curSet.save();
                            System.out.println("Приложение остановлено...");

                        }
                    }

                } catch (EOFException | StringIndexOutOfBoundsException | SocketException e) {
                    e.printStackTrace();
                    System.out.println("EOFException");
                }
            }
        } catch (IOException e) {
            System.out.println("Клиент" + socket.getInetAddress() + "отключился");
        }
    }
}
