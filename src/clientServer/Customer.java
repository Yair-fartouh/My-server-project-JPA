package clientServer;

public class Customer {

    private SendToServer sendToServer;

    public Customer(SendToServer sendToServer) {
        this.sendToServer = sendToServer;
    }

    public SendToServer getSendToServer() {
        return sendToServer;
    }
}
