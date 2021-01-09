package task3008.client;

public class ClientGuiController extends Client {

   private  ClientGuiModel model = new ClientGuiModel();
   private ClientGuiView view = new ClientGuiView(this);

    public class GuiSocketThread extends SocketThread{
        @Override
        protected void processIncomingMessage(String message) {
            model.setNewMessage(message);
            view.refreshMessages();
        }

        @Override
        protected void informAboutAddingNewUser(String userName) {
            model.addUser(userName);
            view.refreshUsers();
        }

        @Override
        protected void informAboutDeletingNewUser(String userName) {
            model.deleteUser(userName);
            view.refreshUsers();
        }

        @Override
        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            super.notifyConnectionStatusChanged(clientConnected);
            view.notifyConnectionStatusChanged(clientConnected);
        }}


        protected SocketThread getSocketThread(){
            return new GuiSocketThread();
        }


        @Override
        public void run() {
          getSocketThread().run();
        }

        public String getServerAddress(){
           return view.getServerAddress();
        }

        public int getServerPort(){
            return view.getServerPort();
        }

        public String getUserName(){
            return view.getUserName();
        }


    public ClientGuiModel getModel(){
        return this.model;
    }

    public static void main(String[] args) {
         new ClientGuiController().run();

    }

}
