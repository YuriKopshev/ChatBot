package task3008.client;



import task3008.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        int x = (int) (Math.random()*100);
        return "date_bot_"+ x;
    }

    public class BotSocketThread extends SocketThread{
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
           sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            String delimiter = ": ";
            String[]splitMessage = message.split(delimiter);
            if (splitMessage.length!=2) return;
            String messageWithoutUser = splitMessage[1];
            String formatInfo = null;
            switch (messageWithoutUser){
                case "дата":
                    formatInfo="d.MM.YYYY";
                    break;
                case "день":
                    formatInfo="d";
                    break;
                case "месяц":
                    formatInfo="MMMM";
                    break;
                case "год":
                    formatInfo="YYYY";
                    break;
                case "время":
                    formatInfo="H:mm:ss";
                    break;
                case "час":
                    formatInfo="H";
                    break;
                case "минуты":
                    formatInfo="m";
                    break;
                case "секунды":
                    formatInfo="s";
                    break;
            }
            if (formatInfo!=null){
                String answer = new SimpleDateFormat(formatInfo).format(Calendar.getInstance().getTime());
                sendTextMessage("Информация для "+ splitMessage[0]+ ": "+ answer);
            }



        }
    }

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }
}
