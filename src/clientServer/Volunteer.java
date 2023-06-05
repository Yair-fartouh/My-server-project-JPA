package clientServer;

import DTO.VolunteerDTO;
import ExcelReader.Location;
import ExcelReader.Time;

public class Volunteer {

    private VolunteerDTO vdto;
    private Location location;
    private Time drivingTime;
    private String ipAddress;
    private int port;

    public Volunteer(VolunteerDTO vdto, Location location, String ipAddress, int port) {
        this.vdto = vdto;
        this.location = location;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public VolunteerDTO getVdto() {
        return vdto;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setVdto(VolunteerDTO vdto) {
        this.vdto = vdto;
    }

    boolean isAvailable() {
        //TODO: לקבל את הזמינות מהמסד נתונים
        return true;
    }

    public Time getDrivingTime() {
        return drivingTime;
    }

    public void setDrivingTime(Time drivingTime) {
        this.drivingTime = drivingTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean canHandleProblem(String problemType) {
        // Check if the volunteer is capable of handling the specified problem type
        for (String capability : getVdto().getVolunteerResourcesDTO().getNameOfTraining()) {
            if (capability.equals(problemType)) {
                return true;
            }
        }
        return false;
    }

}
