package SQL_connection;

import DAO.VolunteerDAO;
import DTO.VolunteerDTO;
import DTO.VolunteerResourcesDTO;
import clientServer.SendToServer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.TypedQuery;

public class Repository implements VolunteerDAO {

    private EntityManagerFactory emf;
    private EntityManager em;
    private int user_id;
    private String url;
    private Connection connection;

    public Repository() {
        this.emf = Persistence.createEntityManagerFactory("My_server_project_JPAPU");
        this.em = emf.createEntityManager();
        //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        //this.url = "jdbc:sqlserver://localhost:1433;databaseName=MyProjectJava;trustServerCertificate=true;user=my project;password=1234";
        //this.connection = DriverManager.getConnection(this.url);
        begin();
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Connection getConnection() {
        return connection;
    }

    public int CheckUserByEmail(String email) {
        int userId;
        StoredProcedureQuery query = em.createStoredProcedureQuery("CheckUserByEmail_login");
        query.registerStoredProcedureParameter("Email", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("Result", Integer.class, ParameterMode.OUT);

        query.setParameter("Email", email);
        query.execute();
        userId = (Integer) query.getOutputParameterValue("Result");
        setUser_id(userId);
        return userId;
    }

    public AuthenticationService GetPasswordSalt(String email) {
        AuthenticationService passwordFromDB = new AuthenticationService();
        StoredProcedureQuery query = em.createStoredProcedureQuery("GetPasswordSalt");
        query.registerStoredProcedureParameter("Email", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("Password", String.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("Salt", String.class, ParameterMode.OUT);

        query.setParameter("Email", email);
        query.execute();

        passwordFromDB.setSalt((String) query.getOutputParameterValue("Salt"));
        passwordFromDB.setPassword((String) query.getOutputParameterValue("Password"));
        if (passwordFromDB.getPassword() != null && passwordFromDB.getSalt() != null) {
            passwordFromDB.setPasswordExists(true);
        } else {
            passwordFromDB.setPasswordExists(false);
        }
        return passwordFromDB;
    }

    public void insertLoginAttempt() throws UnknownHostException {
        InetAddress localhost = InetAddress.getLocalHost();
        String ipAddress = localhost.getHostAddress();
        StoredProcedureQuery query = em.createStoredProcedureQuery("insert_login_attempt");
        query.registerStoredProcedureParameter("userId", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("ipAddress", String.class, ParameterMode.IN);
        query.setParameter("userId", getUser_id());
        query.setParameter("ipAddress", ipAddress);
        query.execute();
        commit();
    }

    public int insertUser(SendToServer sts) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("insert_user");
        query.registerStoredProcedureParameter("firstName", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lastName", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("email", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("phone", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("address", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("password", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("salt", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("birthDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("userId", Integer.class, ParameterMode.OUT);

        query.setParameter("firstName", sts.getFirstName());
        query.setParameter("lastName", sts.getLastName());
        query.setParameter("email", sts.getEmail());
        query.setParameter("phone", sts.getPhone());
        query.setParameter("address", sts.getAddress());
        query.setParameter("password", sts.getPassword());
        query.setParameter("salt", sts.getSalt());
        query.setParameter("birthDate", sts.getDateOfBirth());
        query.execute();

        Integer userId = (Integer) query.getOutputParameterValue("userId");
        commit();
        return userId;
    }

    @Override
    public VolunteerResourcesDTO getVolunteerData() {
        String sql = "SELECT v.availability, t.nameOfTraining FROM Volunteer v JOIN v.trainingId t WHERE v.userId.id = :userId";
        TypedQuery<Object[]> query = em.createQuery(sql, Object[].class);
        query.setParameter("userId", getUser_id());

        List<Object[]> resultList = query.getResultList();

        VolunteerResourcesDTO volunteerDTO = new VolunteerResourcesDTO();
        boolean isConditionMet = false;
        boolean availability = false;
        ArrayList<String> trainingNames = new ArrayList<>();

        for (Object[] result : resultList) {
            if (!isConditionMet) {
                availability = (boolean) result[0];
                isConditionMet = true;
            }
            String traName = (String) result[1];
            trainingNames.add(traName);
        }

        volunteerDTO.setAvailability(availability);
        volunteerDTO.setNameOfTraining(trainingNames);
        return volunteerDTO;

        //jdbc
        /*try {
            VolunteerResourcesDTO volunteerDTO = new VolunteerResourcesDTO();
            boolean isConditionMet = false;
            boolean availability = false;
            ArrayList<String> trainingNames = new ArrayList<>();
            String sql = "SELECT v.availability, t.nameOfTraining FROM volunteer AS v INNER JOIN training AS t ON v.user_id = ? AND v.training_id = t.id";

            PreparedStatement pst = getConnection().prepareStatement(sql);
            pst.setInt(1, getUser_id());

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                if (!isConditionMet) {
                    availability = rs.getBoolean("availability");
                    isConditionMet = true;
                }
                String traName = rs.getString("nameOfTraining");
                trainingNames.add(traName);
                //System.out.println("Availability: " + availability);
                //System.out.println("Training Name: " + trainingName);
            }

            volunteerDTO.setAvailability(availability);
            volunteerDTO.setNameOfTraining(trainingNames);
            return volunteerDTO;

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }*/
    }

    @Override
    public VolunteerDTO getAllData() {

        StoredProcedureQuery procedureQuery = em.createStoredProcedureQuery("GetUserAndVolunteerInfo");

        procedureQuery.registerStoredProcedureParameter("userId", Integer.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("firstName", String.class, ParameterMode.OUT);
        procedureQuery.registerStoredProcedureParameter("lastName", String.class, ParameterMode.OUT);
        procedureQuery.registerStoredProcedureParameter("phone", String.class, ParameterMode.OUT);
        procedureQuery.registerStoredProcedureParameter("address", String.class, ParameterMode.OUT);
        procedureQuery.registerStoredProcedureParameter("email", String.class, ParameterMode.OUT);
        procedureQuery.registerStoredProcedureParameter("birthDate", Date.class, ParameterMode.OUT);
        procedureQuery.registerStoredProcedureParameter("volunteer_id", Integer.class, ParameterMode.OUT);
        procedureQuery.registerStoredProcedureParameter("training_id", Integer.class, ParameterMode.OUT);

        procedureQuery.setParameter("userId", getUser_id());
        procedureQuery.execute();

        String firstName = (String) procedureQuery.getOutputParameterValue("firstName");
        String lastName = (String) procedureQuery.getOutputParameterValue("lastName");
        String phone = (String) procedureQuery.getOutputParameterValue("phone");
        String address = (String) procedureQuery.getOutputParameterValue("address");
        String email = (String) procedureQuery.getOutputParameterValue("email");
        Date birthDate = (Date) procedureQuery.getOutputParameterValue("birthDate");
        Integer volunteer_id = (Integer) procedureQuery.getOutputParameterValue("volunteer_id");
        Integer training_id = (Integer) procedureQuery.getOutputParameterValue("training_id");

        VolunteerDTO volunteerDTO = new VolunteerDTO(
                getUser_id(),
                volunteer_id,
                training_id,
                firstName,
                lastName,
                email,
                phone,
                address,
                birthDate,
                getVolunteerData()
        );
        return volunteerDTO;
    }

    public void begin() {
        this.em.getTransaction().begin();
    }

    public void commit() {
        this.em.getTransaction().commit();
    }

    public void stop() {
        this.em.close();
        this.emf.close();
    }
}
