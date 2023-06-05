package DTO;

import java.io.Serializable;
import java.sql.Date;

public class VolunteerDTO implements Serializable {

    private int user_id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private Date birthDate;
    private VolunteerResourcesDTO volunteerResourcesDTO;

    public VolunteerDTO(int user_id, String firstName, String lastName,
            String email, String phone, String address, Date birthDate,
            VolunteerResourcesDTO volunteerResourcesDTO) {

        this.user_id = user_id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.birthDate = birthDate;
        this.volunteerResourcesDTO = volunteerResourcesDTO;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public VolunteerResourcesDTO getVolunteerResourcesDTO() {
        return volunteerResourcesDTO;
    }

    public void setVolunteerResourcesDTO(VolunteerResourcesDTO volunteerResourcesDTO) {
        this.volunteerResourcesDTO = volunteerResourcesDTO;
    }

}
