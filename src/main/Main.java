package main;

import controllers.FirstNameJpaController;
import java.util.Date;
import javax.persistence.ParameterMode;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("My_server_project_JPAPU");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        //Stored Procedure
        /*StoredProcedureQuery spq = em.createStoredProcedureQuery("insert_firstName");
        spq.registerStoredProcedureParameter("name", String.class, ParameterMode.IN);
        spq.registerStoredProcedureParameter("out", Integer.class, ParameterMode.OUT);
        spq.setParameter("name", "Yair");
        spq.execute();

        Integer ret = (Integer) spq.getOutputParameterValue("out");
        System.out.println(ret);

        //#######################################        
        //stored procedure USER
        StoredProcedureQuery query = em.createStoredProcedureQuery("insert_user");
        query.registerStoredProcedureParameter("firstName_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lastName_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("email_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("phone_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("address_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("password_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("birthDate", Date.class, ParameterMode.IN);

        query.setParameter("firstName_id", ret);
        query.setParameter("lastName_id", 1);
        query.setParameter("email_id", 1);
        query.setParameter("phone_id", 1);
        query.setParameter("address_id", 1);
        query.setParameter("password_id", 1);
        query.setParameter("birthDate", new Date());
        query.execute();*/
        //#######################################
        
        StoredProcedureQuery query3 = em.createStoredProcedureQuery("GetPasswordSalt");
        query3.registerStoredProcedureParameter("Email", String.class, ParameterMode.IN);
        query3.registerStoredProcedureParameter("Password", String.class, ParameterMode.OUT);
        query3.registerStoredProcedureParameter("Salt", String.class, ParameterMode.OUT);

        query3.setParameter("Email", "yairlimudim@gmail.com");
        query3.execute();

        String pass = (String) query3.getOutputParameterValue("Password");
        String salt = (String) query3.getOutputParameterValue("Salt");
        System.out.println("password: " + pass + " salt: " + salt);
        
        em.getTransaction().commit();
        /*
        //Insert Normal
        FirstNameJpaController controller = new FirstNameJpaController(emf);
        FirstName fn = new FirstName();
        fn.setFirstName("he");
        controller.create(fn);
         */
        em.close();
        emf.close();

    }

}
