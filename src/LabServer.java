import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class LabServer {

    private static final int PORT = 8128;

    public static void main(String[] args) {

        ChestCollection curSet = new ChestCollection();
        Socket socket;

        Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Сохранение...");
            try {
                curSet.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        try {
            curSet.setPath(System.getenv("Lab6"));
        } catch (FileNotFoundException | NullPointerException e) {
            try {
                System.out.println("Переменная окружения Lab6 либо не существует, либо указывает на не тот файл.");
                System.out.println("Будет использоваться файл lab6.csv.");
                curSet.setPath("/home/s245031/lab6/lab6.csv");
            } catch (FileNotFoundException e1) {
                System.out.println("Создайте переменную окружения Lab6 и укажите путь к файлу, в котором хотите хранить коллекцию.");
            }
        } finally {

            curSet.readElements(curSet.input);
            curSet.writeElements();
            ServerSocket serverSocket;

            try {
                serverSocket = new ServerSocket(PORT);
                while (true) {
                    System.out.println("Ожидание подключения...");
                    socket = serverSocket.accept();
                    System.out.println("Подключился клиент: " + socket.getInetAddress());
                    new Thread(new ComThread(curSet, socket)).start();
                }

                } catch(IOException e){
                    e.printStackTrace();
                }
        }
    }
}