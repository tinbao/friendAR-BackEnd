package tk.friendar.api;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Root resource (exposed at "friendships" path)
 */
@Path("friendships")
public class FriendshipEndpoint {

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

            List<FriendshipDB> friendshipsDB = session.createCriteria(FriendshipDB.class).list();
            JSONObject json = new JSONObject();
            for (FriendshipDB friendship : friendshipsDB) {
                json.append("friendships: ", friendship.toJson());
            }

            return json.toString();
        } catch (Exception e) {
            return e.toString();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(String friendshipJson) throws JSONException {
        try {

            JSONObject json = new JSONObject(friendshipJson);
            FriendshipDB friendship = new FriendshipDB();

            friendship.setUserA_ID(json.getInt("userA_ID"));
            friendship.setUserB_ID(json.getInt("userB_ID"));

            try (Session session = SessionFactorySingleton.getInstance().openSession()) {
                session.beginTransaction();
                session.save(friendship);
                String respone = friendship.toJson().toString();
                session.getTransaction().commit();
                return respone;
            }
        } catch (Exception e) {
            return e.toString();
        }
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@PathParam("id") String id) {
        try (Session session = SessionFactorySingleton.getInstance().openSession()) {
            try {
                return session.get(FriendshipDB.class, Integer.valueOf(id)).toJson().toString();
            } catch (Exception e) {
                return e.toString();
            }
        }
    }

    @Path("{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String delete(@PathParam("id") String id) {
        try (Session session = SessionFactorySingleton.getInstance().openSession()) {
            try {
                session.beginTransaction();
                FriendshipDB friendship = session.get(FriendshipDB.class, Integer.valueOf(id));
                session.delete(friendship);
                session.getTransaction().commit();
                return friendship.toJson().toString();
            } catch (HibernateException | JSONException e) {
                return e.toString();
            }
        }
    }
}