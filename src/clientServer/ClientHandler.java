package clientServer;

import DTO.VolunteerDTO;
import DataStoreSingleton.DataStore;
import ExcelReader.Location;
import ExcelReader.Time;
import SQL_connection.AuthenticationService;
import java.io.*;
import java.net.Socket;
import SQL_connection.Repository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {

    private static final double EARTH_RADIUS = 6371; // in km
    private static final Map<String, Socket> VOLUNTEER_SOCKETS = new HashMap<>();
    private final Socket CLIENT_SOCKET;

    private InputStream input;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Repository repository;
    private int user_id;
    private VolunteerData volunteerData;
    private Location location;

    private String duration;
    private int hour;
    private int minute;
    private double km;
    private int meters;
    private String link;

    public ClientHandler(Socket clientSocket, VolunteerData volunteerData) {
        this.CLIENT_SOCKET = clientSocket;
        this.repository = new Repository();
        this.volunteerData = volunteerData;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        repository.setUser_id(user_id);
        this.user_id = user_id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void run() {
        try {
            int userId;
            AuthenticationService service;

            input = CLIENT_SOCKET.getInputStream();
            in = new ObjectInputStream(input);
            OutputStream outputStream = CLIENT_SOCKET.getOutputStream();
            out = new ObjectOutputStream(outputStream);

            SendToServer inputLine = (SendToServer) in.readObject();

            if (inputLine.getTypeClient().equals("CLIENT")) {
                //TODO: ללכת לפונקציה שבודקת את הנתונים למתנדב הנכון
                Customer customer = new Customer(inputLine);
                volunteerData.addCustomer(customer);
                System.out.println("Received message from client: " + inputLine.getKindOfHelp() + " => " + inputLine.getPhone());
                // Search for available volunteers
                HashMap<Integer, Volunteer> availableVolunteers = (HashMap<Integer, Volunteer>) volunteerData.getVolunteer();
                Volunteer volunteer = findClosestVolunteer(
                        availableVolunteers,
                        inputLine.getLocation(),
                        customer.getSendToServer().getKindOfHelp());

                if (volunteer != null) {
                    //TODO: לשלוח לאותו מתנדב לכתובת אי פי אדרס הודעה.
                    String message
                            = "<html>"
                            + "<body>"
                            + "<p>The most suitable volunteer is found.<br>"
                            + "This is the customer's cell phone number" + customer.getSendToServer().getPhone() + "<br>"
                            + "You have a journey of " + volunteer.getDrivingTime().getKm() + " km, "
                            + "which is " + volunteer.getDrivingTime().getHour() + ":" + volunteer.getDrivingTime().getMinute() + " minutes <br>"
                            + "would you be able to take the reading at this  <a href=" + this.link + ">location</a>"
                            + "</p>"
                            + "</body>"
                            + "</html>";
                    volunteerData.removeCustomer(customer.getSendToServer().getPhone());
                    volunteerData.removeVolunteer(volunteer.getVdto().getUser_id());
                    sendMessageToVolunteer(volunteer, message);

                }
            } else if (inputLine.getTypeClient().equals("VOLUNTEER")) {
                try {
                    do {
                        //TODO: לקבל את הנתונים שהוא שולח כדי לעדכן את המסד הנתונים

                        if (inputLine.getLoginOrRegister().equals("LOGIN")) {
                            userId = repository.CheckUserByEmail(inputLine.getEmail());     //אם צריך להעביר לפני הבדיקה של לוגין
                            setUser_id(userId);
                            //TODO: לא לשכוח למחוק את ההדפסה !!!!!!!!!!!!!!!!!
                            //VolunteerDTO o = repository.getAllData();
                            //System.out.println(o.getVolunteer_id() + " " + o.getTraining_id());
                            if (getUser_id() != 0) {
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

                                    outAuthenticationService(service);
                                    //clientSocket.shutdownOutput();
                                }

                                if (inputLine.getRequestType().equals("insertToLoginAttempts")) {
                                    //להכניס לטבלת הניסיון של אותו אימייל לכניסה למערכת
                                    repository.insertLoginAttempt();
                                }
                            } else {
                                service = new AuthenticationService();
                                service.setPasswordExists(false); //הוי אומר המשתמש אינו רשום למערכת ולכן אינו יכול להיכנס ועליו להירשם
                                outAuthenticationService(service);
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
                            } else {
                                if (inputLine.getLoginOrRegister().equals("HOME")) {

                                    VolunteerDTO volunteerDTO = repository.getAllData();
                                    DataStore.getInstance().setVolunteerDTO(volunteerDTO);
                                    Volunteer volunteer = new Volunteer(volunteerDTO, inputLine.getLocation(), this.CLIENT_SOCKET.getInetAddress().toString(), this.CLIENT_SOCKET.getPort());
                                    volunteerData.addVolunteer(volunteer);
                                    addVolunteerSocket(volunteer, CLIENT_SOCKET);
                                    //TODO: UpsertLocation מסד נתונים פרוצדורה
                                    if (inputLine.getRequestType().equals("Information")) {

                                        //System.out.println(DataStore.getInstance().getVolunteerDTO().getEmail());
                                        outvolunteerDTOService(volunteerDTO);

                                    } else {
                                        if (inputLine.getRequestType().equals("UpdateLocation")) {
                                            setLocation(inputLine.getLocation());
                                            //TODO: לעדכן במסד הנתונים
                                        }
                                    }
                                }
                            }
                        }
                    } while ((inputLine = (SendToServer) in.readObject()) != null);
                } catch (EOFException e) {
                }
            }

            System.out.println("Client disconnected: " + CLIENT_SOCKET.getInetAddress().getHostAddress());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addVolunteerSocket(Volunteer volunteer, Socket socket) {
        // Add the Socket object to the HashMap using the volunteer's IP address and port number as the key
        VOLUNTEER_SOCKETS.put(volunteer.getIpAddress() + ":" + volunteer.getPort(), socket);
        System.out.println(this.VOLUNTEER_SOCKETS.values());
    }

    private Volunteer findClosestVolunteer(HashMap<Integer, Volunteer> volunteers, Location location, String problemType) {
        // Sort the volunteers based on their driving distance to the client's location
        List<Volunteer> sortedVolunteers = volunteers.values().stream()
                .sorted(Comparator.comparingDouble(volunteer -> calculateDrivingDistance(volunteer.getLocation(), location)))
                .collect(Collectors.toList());

        // Filter the list of closest volunteers to only include those who are capable of handling the problem
        List<Volunteer> closestVolunteers = sortedVolunteers.stream()
                .filter(volunteer -> volunteer.canHandleProblem(problemType))
                .limit(5)
                .collect(Collectors.toList());

        // Find the most suitable volunteer based on their availability, rating, and proximity to the client's location
        Volunteer mostSuitableVolunteer = null;
        double bestScore = Double.MAX_VALUE;
        for (Volunteer volunteer : closestVolunteers) {
            double travelDistance = calculateDrivingDistance(volunteer.getLocation(), location);
            double availabilityScore = volunteer.isAvailable() ? 1.0 : 0.0;
            double proximityScore = 1.0 / (travelDistance + 1.0); // Add 1 to avoid division by zero
            double suitabilityScore = availabilityScore * proximityScore;
            if (suitabilityScore < bestScore) {
                mostSuitableVolunteer = volunteer;
                bestScore = suitabilityScore;
            }
        }

        // Call the startAPI function to get the driving time to the client's location
        if (mostSuitableVolunteer != null) {
            Time drivingTime = null;
            try {
                drivingTime = startAPI(
                        Double.toString(mostSuitableVolunteer.getLocation().getLatitude()),
                        Double.toString(mostSuitableVolunteer.getLocation().getLongitude()),
                        Double.toString(location.getLatitude()),
                        Double.toString(location.getLongitude())
                );
            } catch (IOException | InterruptedException | ParseException e) {
                // Handle any errors that occur when calling the API
                e.printStackTrace();
            }

            // Return the most suitable volunteer along with their driving time to the client's location
            mostSuitableVolunteer.setDrivingTime(drivingTime);
            return mostSuitableVolunteer;
        } else {
            // Return null if no suitable volunteers are found
            return null;
        }
    }

    private double calculateDrivingDistance(Location location1, Location location2) {
        // TODO: Implement a function that calculates the driving distance between two locations using a mapping API
        // Here's an example of how you could calculate the driving distance using the Haversine formula:
        double lat1 = Math.toRadians(location1.getLatitude());
        double lon1 = Math.toRadians(location1.getLongitude());
        double lat2 = Math.toRadians(location2.getLatitude());
        double lon2 = Math.toRadians(location2.getLongitude());
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;
        return distance;
    }

    /**
     * TOKEN 1 - 1a30703c36msh7d4953d5a6e903ap13e939jsnc18ed6efcffc ==> GitHub
     * -> hadarbarebi@proton.me TOKEN 2 -
     * 5c9eaff043msh30c61f17d646d8fp1d5f4ajsn528698b21cc0 ==>
     * yairlimudim@gmail.com TOKEN 3 -
     * 134d7063a7msh481f855665f5a16p151085jsn908a16eec997 ==> yairf933@gmail.com
     *
     *
     */
    public String GetDirections(
            String originLAT,
            String originLNG,
            String destinationLAT,
            String destinationLNG) throws IOException, InterruptedException, ParseException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://driving-directions1.p.rapidapi.com/get-directions?origin="
                        + originLAT + "%2C%20" + originLNG + "&destination=" + destinationLAT + "%2C%20"
                        + destinationLNG + "&avoid_routes=tolls%2Cferries&country=us&language=en"))
                .header("X-RapidAPI-Key", "134d7063a7msh481f855665f5a16p151085jsn908a16eec997")
                .header("X-RapidAPI-Host", "driving-directions1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        //TODO: delete output
        System.out.println(response.body());

        JSONObject jsonObj = new JSONObject(response.body());
        JSONObject item = jsonObj.getJSONObject("data").getJSONArray("best_routes").getJSONObject(0);
        String link = jsonObj.getJSONObject("data").optString("directions_link");

        //this.distance = item.optString("distance_label");
        this.link = link;
        this.duration = item.optString("duration_label");
        this.meters = item.getInt("distance_meters");

        return this.duration;
    }

    /**
     * The function: its job is to convert the received text into numbers.
     *
     * @param str - Gets a string containing the travel time.
     * @return
     */
    public Time conversion(String str) {
        int indexHour;
        int indexMinute;

        indexHour = str.indexOf("hr");
        indexMinute = str.indexOf("min");

        if (indexHour != -1) {
            this.hour = Integer.parseInt(str.substring(0, indexHour - 1));
            this.minute = Integer.parseInt(str.substring(indexHour + 3, indexMinute - 1));
        } else {
            this.minute = Integer.parseInt(str.substring(0, indexMinute - 1));
        }
        this.km = this.meters / 1000.000;

        System.out.println("Hour: " + this.hour);
        System.out.println("Minute: " + this.minute);
        System.out.println("Meters: " + this.meters);
        System.out.println("km: " + this.km);

        return new Time(this.hour, this.minute, this.km);
    }

    public Time startAPI(String originLAT,
            String originLNG,
            String destinationLAT,
            String destinationLNG) throws IOException, InterruptedException, ParseException {

        return conversion(GetDirections(originLAT, originLNG, destinationLAT, destinationLNG));
    }

    public void sendMessageToVolunteer(Volunteer volunteer, String message) {
        try {
            // Look up the Socket object for the volunteer in the HashMap
            Socket socket = VOLUNTEER_SOCKETS.get(volunteer.getIpAddress() + ":" + volunteer.getPort());

            // Convert the HTML string into a byte array
            String htmlString = message;
            byte[] htmlBytes = htmlString.getBytes();

            // Send the byte array to the volunteer through the socket connection
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(htmlBytes);

        } catch (IOException e) {
            // Handle any errors that occur when sending the message
            e.printStackTrace();
        }
    }

    public void outAuthenticationService(AuthenticationService service) throws IOException {
        out.writeObject(service);
        out.flush();
    }

    public void outvolunteerDTOService(VolunteerDTO service) throws IOException {
        out.writeObject(service);
        out.flush();
    }

    public void DoesotExist() throws IOException {
        out.writeObject("Does not exist");
        out.flush();
    }

    public void stop() throws IOException {
        in.close();
        CLIENT_SOCKET.close();
    }
}
