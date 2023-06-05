package clientServer;

import ExcelReader.Location;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolunteerData {

    private Map<String, Customer> customers;
    private Map<Integer, Volunteer> volunteers;

    public VolunteerData() {
        customers = new HashMap<>();
        volunteers = new HashMap<>();
    }

    public void addCustomer(Customer customer) {
        customers.put(customer.getSendToServer().getPhone(), customer);
    }

    public void removeCustomer(String phone) {
        customers.remove(phone);
    }

    public Customer getCustomer(String phone) {
        return customers.get(phone);
    }

    public void addVolunteer(Volunteer volunteer) {
        volunteers.put(volunteer.getVdto().getUser_id(), volunteer);
    }

    public void removeVolunteer(int user_id) {
        volunteers.remove(user_id);
    }

    public HashMap<Integer, Volunteer> getVolunteer() {
        return (HashMap<Integer, Volunteer>) volunteers;
    }

    public void updateVolunteerLocation(int id, Location location) {
        Volunteer volunteer = volunteers.get(id);
        if (volunteer != null) {
            volunteer.setLocation(location);
        }
    }

    // add more methods as needed
}
