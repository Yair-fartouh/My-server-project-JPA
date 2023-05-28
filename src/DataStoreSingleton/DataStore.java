package DataStoreSingleton;

import DTO.VolunteerDTO;

public class DataStore {
    private static DataStore instance;
    private VolunteerDTO volunteerDTO;

    private DataStore() {
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public void setVolunteerDTO(VolunteerDTO volunteerDTO) {
        this.volunteerDTO = volunteerDTO;
    }

    public VolunteerDTO getVolunteerDTO() {
        return volunteerDTO;
    }
}