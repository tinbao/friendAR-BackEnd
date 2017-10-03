CREATE TABLE IF NOT EXISTS Users(
    UserID  SERIAL PRIMARY KEY NOT NULL,
    fullName TEXT,
    usersName TEXT NOT NULL,
    usersPassword TEXT NOT NULL,
    salt TEXT NOT NULL,
    email TEXT NOT NULL,
    latitude DECIMAL,
    longitude DECIMAL,
    locationLastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Places(
    placeID SERIAL PRIMARY KEY NOT NULL,
    placeName TEXT,
    latitude DECIMAL,
    longitude DECIMAL
);

CREATE TABLE IF NOT EXISTS Meetings(
    meetingID SERIAL PRIMARY KEY NOT NULL,
    placeID SERIAL REFERENCES Places (placeID) NOT NULL,
    meetingName TEXT,
    timeDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS MeetingUsers(
    meetingUserID SERIAL PRIMARY KEY NOT NULL,
    meetingID SERIAL REFERENCES Meetings (meetingID) NOT NULL,
    userID SERIAL REFERENCES Users (UserID) NOT NULL
);

CREATE TABLE IF NOT EXISTS Friendships(
    friendshipID SERIAL PRIMARY KEY NOT NULL,
    userA_ID SERIAL REFERENCES Users (UserID) NOT NULL,
    userB_ID SERIAL REFERENCES Users (UserID) NOT NULL
);

CREATE TABLE IF NOT EXISTS Messages(
    messageID SERIAL PRIMARY KEY NOT NULL,
    meetingID SERIAL REFERENCES Meetings (meetingID) NOT NULL,
    userID SERIAL REFERENCES Users (UserID) NOT NULL,
	content TEXT,
	timeSent TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
