package clientServer;

import SQL_connection.AuthenticationService;
import java.io.*;
import java.net.Socket;
import SQL_connection.Repository;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private InputStream input;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Repository repository;
    private int user_id;
    //private List<VolunteerHandler> volunteerHandlers = new ArrayList<>();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.repository = new Repository();
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    
    @Override
    public void run() {
        try {
            int userId;
            AuthenticationService service;

            input = clientSocket.getInputStream();
            in = new ObjectInputStream(input);
            OutputStream outputStream = clientSocket.getOutputStream();
            out = new ObjectOutputStream(outputStream);

            SendToServer inputLine = (SendToServer) in.readObject();

            if (inputLine.getTypeClient().equals("CLIENT")) {
                //TODO: ללכת לפונקציה שבודקת את הנתונים למתנדב הנכון
                System.out.println("Received message from client: " + inputLine.getKindOfHelp() + " => " + inputLine.getPhone());
            } else if (inputLine.getTypeClient().equals("VOLUNTEER")) {
                try {
                    do {
                        //TODO: לקבל את הנתונים שהוא שולח כדי לעדכן את המסד הנתונים

                        if (inputLine.getLoginOrRegister().equals("LOGIN")) {
                            userId = repository.CheckUserByEmail(inputLine.getEmail());     //אם צריך להעביר לפני הבדיקה של לוגין
                            setUser_id(userId);
                            
                            if (getUser_id()!= 0) {
                                /**
                                 * The user enters his email, and I'm going to
                                 * check with the DB if such an email exists, if
                                 * so - bring me the userID and bring me the
                                 * password and the salt so that I can check the
                                 * customer's password if it is correct.
                                 *
                                 */
                                if (inputLine.getRequestType().equals("checkEmail")) {
                                    service = repository.GetPasswordSalt(inputLine.getEmail());

                                    outService(service);
                                    //clientSocket.shutdownOutput();
                                }

                                if (inputLine.getRequestType().equals("insertToLoginAttempts")) {
                                    //להכניס לטבלת הניסיון של אותו אימייל לכניסה למערכת
                                    repository.insertLoginAttempt(getUser_id());
                                }
                            } else {
                                service = new AuthenticationService();
                                service.setPasswordExists(false); //הוי אומר המשתמש אינו רשום למערכת ולכן אינו יכול להיכנס ועליו להירשם
                                outService(service);
                            }
                        } else {
                            if (inputLine.getLoginOrRegister().equals("SIGNUP")) {
                                /**
                                 * The user enters his email, and I'm going to
                                 * check with the DB if such an email exists, if
                                 * so - bring me the userID and bring me the
                                 * password and the salt so that I can check the
                                 * customer's password if it is correct.
                                 *
                                 */
                                if (inputLine.getRequestType().equals("checkEmail")) {
                                    service = repository.GetPasswordSalt(inputLine.getEmail());

                                    if (!service.isPasswordExists()) {
                                        //להכניס את הנתונים לטבלאות לפי הפרוצדורה של יוזר
                                        userId = repository.insertUser(inputLine);
                                        setUser_id(userId);
                                        DoesotExist(); //הוי אומר לא נמצא יוזר כזה ואפשר להכניס אותו למערכת
                                    } else {
                                        out.writeObject("Exist");
                                        out.flush();
                                    }
                                }
                            }
                        }
                    } while ((inputLine = (SendToServer) in.readObject()) != null);
                } catch (EOFException e) {
                }
            }

            System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void outService(AuthenticationService service) throws IOException {
        out.writeObject(service);
        out.flush();
    }

    public void DoesotExist() throws IOException {
        out.writeObject("Does not exist");
        out.flush();
    }

    public void stop() throws IOException {
        in.close();
        clientSocket.close();
    }
}
