package locationAPI;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;

import org.json.*;

//My json is coming: https://rapidapi.com/letscrape-6bRBa3QguO5/api/driving-directions1/

/**
 * @author YairF
 */
public class DrivingDirections {

    private String distance;
    private String duration;
    private int hour;
    private int minute;
    private double miles;
    private double km;
    private int meters;

    public String GetDirections(String originLAT,
                                String originLNG,
                                String destinationLAT,
                                String destinationLNG) throws IOException, InterruptedException, ParseException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://driving-directions1.p.rapidapi.com/get-directions?origin=" +
                        originLAT + "%2C%20" + originLNG + "&destination=" + destinationLAT + "%2C%20" +
                        destinationLNG + "&avoid_routes=tolls%2Cferries&country=us&language=en"))
                .header("X-RapidAPI-Key", "5c9eaff043msh30c61f17d646d8fp1d5f4ajsn528698b21cc0")
                .header("X-RapidAPI-Host", "driving-directions1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());

        JSONObject jsonObj = new JSONObject(response.body());
        JSONObject item = jsonObj.getJSONObject("data").getJSONArray("best_routes").getJSONObject(0);

        //this.distance = item.optString("distance_label");
        this.duration = item.optString("duration_label");
        this.meters = item.getInt("distance_meters");

        return this.duration;
    }

    /**
     * The function: its job is to convert the received text into numbers.
     *
     * @param str - Gets a string containing the travel time.
     */
    public void conversion(String str) {
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
    }

    public void start(String originLAT,
                      String originLNG,
                      String destinationLAT,
                      String destinationLNG) throws IOException, InterruptedException, ParseException {

        conversion(GetDirections(originLAT, originLNG, destinationLAT, destinationLNG));
    }
}
