package SQL_connection;

import clientServer.SendToServer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;
import java.sql.Date;

public class Repository {

    private EntityManagerFactory emf;
    private EntityManager em;

    public Repository() {
        this.emf = Persistence.createEntityManagerFactory("My_server_project_JPAPU");
        this.em = emf.createEntityManager();
        begin();
    }

    public int CheckUserByEmail(String email) {
        int userId;
        StoredProcedureQuery query = em.createStoredProcedureQuery("CheckUserByEmail_login");
        query.registerStoredProcedureParameter("Email", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("Result", Integer.class, ParameterMode.OUT);

        query.setParameter("Email", email);
        query.execute();
        userId = (Integer) query.getOutputParameterValue("Result");
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

    public void insertLoginAttempt(int userId) throws UnknownHostException {
        InetAddress localhost = InetAddress.getLocalHost();
        String ipAddress = localhost.getHostAddress();
        StoredProcedureQuery query = em.createStoredProcedureQuery("insert_login_attempt");
        query.registerStoredProcedureParameter("userId", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("ipAddress", String.class, ParameterMode.IN);
        query.setParameter("userId", userId);
        query.setParameter("ipAddress", ipAddress);
        query.execute();
        commit();
    }

    public int insertUser(SendToServer sts) {
        //begin();
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
