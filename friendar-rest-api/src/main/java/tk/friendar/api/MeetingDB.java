package tk.friendar.api;

import org.hibernate.Session;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name = "meetings")
public class MeetingDB implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meetingid", nullable = false)
    public int meetingid; //not null

    private String meetingName;
    private Timestamp timeDate;

    @OneToMany (targetEntity = MeetingUserDB.class, mappedBy = "meetingid", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	public Collection<MeetingUserDB> meetingUsers = new ArrayList<MeetingUserDB>();

    @OneToMany (targetEntity = MessageDB.class, mappedBy = "meeting", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    public Collection<MessageDB> messages = new ArrayList<MessageDB>();

    @ManyToOne
    @JoinColumn(name = "placeid")
    private PlaceDB place;

    public int getMeetingid() {
        return meetingid;
    }

    public void setMeetingid(int meetingID) {
        this.meetingid = meetingID;
    }

    public Timestamp getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(Timestamp timeDate) {
        this.timeDate = timeDate;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

	public Collection<MeetingUserDB> getMeetingUsers() {
		return meetingUsers;
	}

	public void setMeetingUsers(ArrayList<MeetingUserDB> meetingUsers) {
		this.meetingUsers = meetingUsers;
	}

    public void setPlace(int placeID){
        try (Session session = SessionFactorySingleton.getInstance().openSession()) {
            this.place = session.get(PlaceDB.class,placeID);
        }
    }
    public PlaceDB getPlace(){
        return this.place;
    }

    JSONObject toJson(Boolean nextLevelDeep) throws JSONException {
        JSONObject meetingJSON = new JSONObject();
        meetingJSON.put("id", this.getMeetingid());
        meetingJSON.put("meetingName", this.getMeetingName());
        if(nextLevelDeep){
            for (MeetingUserDB meetingUsers : this.getMeetingUsers()) {
                meetingJSON.append("meeting users", meetingUsers.toJson(false));
            }
            meetingJSON.put("place", this.getPlace().toJson(false).toString());
        }
        meetingJSON.put("Time", this.getTimeDate());
        return meetingJSON;

    }
}