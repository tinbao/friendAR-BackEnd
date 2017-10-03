package tk.friendar.api;

import org.hibernate.Session;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Root resource (exposed at "users" path)
 */
@Path("users")
public class UsersEndpoint {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a application/json response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get() throws JSONException {
        try (Session session = SessionFactorySingleton.getInstance().openSession()) {

            List<UserDB> usersDB = session.createCriteria(UserDB.class).list();
            JSONObject json = new JSONObject();
            for (UserDB user : usersDB) {
                json.append("users", user.toJson(true));
            }

            return json.toString();
        } catch (Exception e) {
            return e.toString();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserDB create(String userJson) throws JSONException {
        try {

            JSONObject json = new JSONObject(userJson);
            UserDB user = new UserDB();

            user.setFullName(json.getString("fullName"));
            user.setUsersname(json.getString("usersname"));
            user.setUsersPassword(json.getString("userspassword"));
            user.setEmail(json.getString("email"));
            user.setLatitude(json.getDouble("latitude"));
            user.setLongitude(json.getDouble("longitude"));

            try (Session session = SessionFactorySingleton.getInstance().openSession()) {
                session.beginTransaction();
                session.save(user);
                session.getTransaction().commit();
                return user;
            }
        } catch (Exception e) {
            throw new JSONException(e);
        }
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@PathParam("id") String id) {
        try (Session session = SessionFactorySingleton.getInstance().openSession()) {
            try {
                return session.get(UserDB.class, Integer.valueOf(id)).toJson(true).toString();
            } catch (JSONException e) {
                return e.toString();
            }
        }
    }
}