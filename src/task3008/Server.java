package task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static class Handler extends Thread{
        Socket socket;
        public Handler(Socket socket){
            this.socket=socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            Message message;
            while (true){
            connection.send(new Message(MessageType.NAME_REQUEST," Введите имя:"));
           message = connection.receive();
          String username = message.getData();
          if (message.getType()==MessageType.USER_NAME && !message.getData().isEmpty() && !username.equals("") && !connectionMap.containsKey(username)){
              connectionMap.put(username,connection);
              break;
          }
          }
          connection.send(new Message(MessageType.NAME_ACCEPTED, " Вы добавлены в чат!"));
            ConsoleHelper.writeMessage(message.getData() + " принято");
         return message.getData();

        }

        private void notifyUsers(Connection connection, String userName) throws IOException{
            connectionMap.forEach((name,connect) ->{
                 if (!name.equals(userName)){
                     try {
                         connection.send(new Message(MessageType.USER_ADDED,name));
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
                });
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException{
            while (true){
                Message message=connection.receive();
                if (message.getType()==MessageType.TEXT){
                    Server.sendBroadcastMessage(new Message(MessageType.TEXT,userName + ": " + message.getData()));
                }
                if (message.getType()!=MessageType.TEXT){
                    ConsoleHelper.writeMessage("Неверный формат сообщения!");

                }

            }
        }

        @Override
        public void run() {
            System.out.println(socket.getRemoteSocketAddress()+ "");
            try {

                Connection connection = new Connection(socket);
              String userName = serverHandshake(connection);
              sendBroadcastMessage(new Message(MessageType.USER_ADDED,userName));
              notifyUsers(connection,userName);
              serverMainLoop(connection,userName);
                connectionMap.forEach((name,connection1)->{
                            if (name.equals(userName)){
                                connectionMap.remove(name);
                                sendBroadcastMessage(new Message(MessageType.USER_REMOVED,userName));
                            }
                        }
                );
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("ошибка при обмене данными с удаленным адресом"+ e);
            }
            ConsoleHelper.writeMessage("Cоединение с удаленным адресом закрыто!");

        }
    }
    private static Map<String,Connection> connectionMap = new ConcurrentHashMap<>();


    public static void main(String[] args) {
            try(ServerSocket serverSocket = new ServerSocket(ConsoleHelper.readInt())) {
                ConsoleHelper.writeMessage("Server started..");
                while (true){
                 new Handler(serverSocket.accept()).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        public static void sendBroadcastMessage(Message message){
           for (Entry entry: connectionMap.entrySet()){
             Connection connection= (Connection) entry.getValue();
               try {
                   connection.send(message);
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        }


}
