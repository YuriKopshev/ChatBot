package task3008.client;



import task3008.Connection;
import task3008.ConsoleHelper;
import task3008.Message;
import task3008.MessageType;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public  class SocketThread extends Thread{
        protected void processIncomingMessage(String message){
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName){
            ConsoleHelper.writeMessage("Участник с именем "+ userName + " присоединился к чату");
        }

        protected void informAboutDeletingNewUser(String userName){
            ConsoleHelper.writeMessage("Участник с именем "+ userName + " покинул чат");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected){
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this){
                Client.this.notify();
            }
        }

       protected void clientHandshake() throws IOException, ClassNotFoundException{
            while (!clientConnected){
                Message message=connection.receive();
                if (message.getType() == MessageType.NAME_REQUEST){
                    String name = getUserName();
                    connection.send(new Message(MessageType.USER_NAME, name));
                }
               else if (message.getType() == MessageType.NAME_ACCEPTED){
                    notifyConnectionStatusChanged(true);
                   return;
                }

                else {
                  throw new IOException("Unexpected MessageType");
                }
            }
       }

       protected void clientMainLoop() throws IOException, ClassNotFoundException{
            while (true){
                Message message=connection.receive();
                if (message.getType()==MessageType.TEXT){
                    processIncomingMessage(message.getData());
                }
               else if (message.getType()==MessageType.USER_ADDED){
                    informAboutAddingNewUser(message.getData());
                }
             else if (message.getType()==MessageType.USER_REMOVED){
                    informAboutDeletingNewUser(message.getData());
                }
                else {
                    throw new IOException("Unexpected MessageType");
                }
            }
       }

        @Override
        public void run() {
            try {
                Socket clientSocket = new Socket(getServerAddress(),getServerPort());
                connection = new Connection(clientSocket);
                clientHandshake();
                clientMainLoop();
            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
                System.out.println("Ошибка соединения");
            }
        }
    }

    public void run() {
        SocketThread thread= getSocketThread();
        thread.setDaemon(true);
        thread.start();
        try {
            synchronized (this){

                wait();}
        }
        catch (InterruptedException e) {
            ConsoleHelper.writeMessage("Client error");
            return;
        }
        if (clientConnected){
            ConsoleHelper.writeMessage("Соединение установлено.Для выхода наберите команду 'exit'.");
        }
        else {
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
        }
        while (clientConnected){
            String text = ConsoleHelper.readString();
            if (text.equalsIgnoreCase("exit"))
                break;
            if (shouldSendTextFromConsole())
                sendTextMessage(text);

        }

    }


    protected Connection connection;
    private volatile boolean clientConnected = false;

    protected String getServerAddress() {
        String address = ConsoleHelper.readString();
        return address;
    }

    protected int getServerPort() {
        int port = ConsoleHelper.readInt();
        return port;
    }

    protected String getUserName() {
        String name = ConsoleHelper.readString();
        return name;
    }

    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Произошла ошибка соединения");
            clientConnected = false;
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        Client client1 = new Client();
        client.run();
        client1.run();

    }
}
