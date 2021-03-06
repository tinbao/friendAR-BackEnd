package tk.friendar.api;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.*;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class UserDB implements Serializable {

    private static final int iterations = 1; // too high a value adds minimal increases in security and significantly slows down processing.
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;
    private static char[] passChar;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid", nullable = false)
    public int userID;
    @Column(name = "fullname", nullable = false)
    public String fullName;
    @Column(name = "username", unique = true, nullable = false)
    public String username;
    @Column(name = "userspassword")
    public String usersPassword;
    @Column(name = "email")
    public String email;
    @OneToMany(targetEntity = FriendshipDB.class, mappedBy = "userA_ID", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //@OneToMany (targetEntity = FriendshipDB.class, mappedBy = "userB_ID", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    public List<FriendshipDB> friends = new ArrayList<>();
    @OneToMany(targetEntity = MeetingUserDB.class, mappedBy = "userID", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Collection<MeetingUserDB> meetings = new ArrayList<MeetingUserDB>();
    @OneToMany(targetEntity = MessageDB.class, mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Collection<MessageDB> messages = new ArrayList<MessageDB>();
    @Column(nullable = true)
    private double longitude;
    @Column(nullable = true)
    private double latitude;
    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date locationLastUpdated;
    @Column(name = "salt")
    private String salt;

    private static char[] hashPas(char[] password, byte[] salt, int iterationNum, int keyLen) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterationNum, keyLen);
        SecretKey key = skf.generateSecret(spec);
        return Base64.encodeBase64String(key.getEncoded()).toCharArray();

    }

    public int getUserID() {
        return userID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getLocationLastUpdated() {
        return locationLastUpdated;
    }

    public void setLocationLastUpdated(Date locationLastUpdated) {
        this.locationLastUpdated = locationLastUpdated;
    }

    public List<UserDB> getFriends() {
        List<UserDB> friends = new ArrayList<>();
        for (FriendshipDB friend : this.friends) {
            friends.add(friend.getUserB_ID());
        }
        return friends;
    }

    public void setFriends(List<FriendshipDB> friends) {
        this.friends = friends;
    }

    public Collection<MeetingUserDB> getMeetings() {
        return meetings;
    }

    public void setMeetings(ArrayList<MeetingUserDB> meetingUsers) {
        this.meetings = meetingUsers;
    }

    public Collection<MessageDB> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<MessageDB> messages) {
        this.messages = messages;
    }

    public String getUsersPassword() {
        return usersPassword;
    }

    public void setUsersPassword(String usersPassword) throws Exception {
        this.usersPassword = setUserPassword(usersPassword);
        assert validPassword(usersPassword);
    }

    public boolean validPassword(String password) {
        try {
            return usersPassword.equals(String.valueOf(hashPas(password.toCharArray(), this.salt.getBytes(), iterations, desiredKeyLen)));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false; // default to invalid password as a safeguard.
    }

    private String setUserPassword(String password) throws InvalidKeySpecException, NoSuchAlgorithmException, UnsupportedEncodingException {
        if (password == null || password.length() == 0) {
            throw new IllegalArgumentException("Empty passwords are not supported.");
        }
        passChar = password.toCharArray();
        this.salt = genSalt();
        return String.valueOf(hashPas(passChar, salt.getBytes(), iterations, desiredKeyLen));
    }

    private String genSalt() {
        return randomString(saltLen);
    }

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ ) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    JSONObject toJson(Boolean nextLevelDeep) throws JSONException {
        JSONObject userJSON = new JSONObject();
        userJSON.put("id", this.getUserID());
        userJSON.put("username", this.getUsername());
        userJSON.put("fullName", this.getFullName());
        userJSON.put("email", this.getEmail());
        userJSON.put("latitude", this.getLatitude());
        userJSON.put("longitude", this.getLongitude());
        userJSON.put("locationLastUpdated", this.getLocationLastUpdated());
        if (nextLevelDeep) {
            for (UserDB friend : this.getFriends()) {
                userJSON.append("friends", friend.toJson(false));
            }
        }
        return userJSON;

    }
}