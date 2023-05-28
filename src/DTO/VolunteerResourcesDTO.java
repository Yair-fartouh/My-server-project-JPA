package DTO;

import java.io.Serializable;
import java.util.List;

public class VolunteerResourcesDTO implements Serializable {

    private boolean availability;
    private List<String> nameOfTraining;

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public List<String> getNameOfTraining() {
        return nameOfTraining;
    }

    public void setNameOfTraining(List<String> nameOfTraining) {
        this.nameOfTraining = nameOfTraining;
    }

}
