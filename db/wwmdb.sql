CREATE TABLE Login
(
ID VARCHAR(15) NOT NULL,
PW VARCHAR(15) NOT NULL,
Name VARCHAR(15) character set utf8 collate utf8_unicode_ci,
regID VARCHAR(200),
PRIMARY KEY(ID)
);

CREATE TABLE Room
(
RoomID INTEGER NOT NULL AUTO_INCREMENT,
Name VARCHAR(20) character set utf8 collate utf8_unicode_ci,
Number INTEGER NOT NULL,
Daycheck INTEGER NOT NULL,
Daystart VARCHAR(15),
Daystarttime INTEGER,
Dayreturnvalue INTEGER,
Placecheck INTEGER NOT NULL,
Placedefault VARCHAR(30) character set utf8 collate utf8_unicode_ci,
Placerangedefault INTEGER,
PRIMARY KEY(RoomID)
);

CREATE TABLE Roommember
(
RoomID INTEGER NOT NULL,
ID VARCHAR(15) NOT NULL,
NEWROOM BOOLEAN DEFAULT TRUE,
PRIMARY KEY(RoomID,ID),
FOREIGN KEY(ID) REFERENCES Login(ID) ON DELETE CASCADE,
FOREIGN KEY(RoomID) REFERENCES Room(RoomID) ON DELETE CASCADE
);

CREATE TABLE Meet
(
RoomID INTEGER NOT NULL,
MeetID INTEGER NOT NULL,
Day VARCHAR(15),
Starttime INTEGER,
Endtime INTEGER,
StartDate VARCHAR(15),
DateRange INTEGER,
Place VARCHAR(20) character set utf8 collate utf8_unicode_ci,
Food VARCHAR(20) character set utf8 collate utf8_unicode_ci,
Finished BOOLEAN DEFAULT FALSE,
Choice INTEGER DEFAULT 1,
PRIMARY KEY(RoomID,MeetID),
FOREIGN KEY(RoomID) REFERENCES Room(RoomID) ON DELETE CASCADE
);

CREATE TABLE MeetIDChoice
(
RoomID INTEGER NOT NULL,
MeetID INTEGER NOT NULL,
ID VARCHAR(15) NOT NULL,
Selected BOOLEAN DEFAULT FALSE,
SelectValue VARCHAR(300),
PRIMARY KEY(RoomID,MeetID,ID),
FOREIGN KEY(RoomID,MeetID) REFERENCES Meet(RoomID,MeetID) ON DELETE CASCADE,
FOREIGN KEY(ID) REFERENCES Login(ID) ON DELETE CASCADE
);

CREATE TABLE Station
(
Stationname VARCHAR(10),
Line1check BOOLEAN DEFAULT FALSE,
Line2check BOOLEAN DEFAULT FALSE,
Line3check BOOLEAN DEFAULT FALSE,
Line4check BOOLEAN DEFAULT FALSE,
Line5check BOOLEAN DEFAULT FALSE,
Line6check BOOLEAN DEFAULT FALSE,
Line7check BOOLEAN DEFAULT FALSE,
Line8check BOOLEAN DEFAULT FALSE,
Line9check BOOLEAN DEFAULT FALSE,
Linemidcheck BOOLEAN DEFAULT FALSE,
Linebundangcheck BOOLEAN DEFAULT FALSE,
Lineshinbundangcheck BOOLEAN DEFAULT FALSE,
Gu INTEGER NOT NULL,
ThemeParkcheck BOOLEAN DEFAULT FALSE,
ThemeCulturecheck BOOLEAN DEFAULT FALSE,
ThemeMallcheck BOOLEAN DEFAULT FALSE,
ThemeRoadshopcheck BOOLEAN DEFAULT FALSE,
ThemeEatcheck BOOLEAN DEFAULT FALSE,
ThemeHangangcheck BOOLEAN DEFAULT FALSE,
ThemeUnivcheck BOOLEAN DEFAULT FALSE,
ThemeChunggyecheck BOOLEAN DEFAULT FALSE,
Hotplace VARCHAR(300) default null
);

CREATE TABLE Food
(
Foodname VARCHAR(10),
Category INTEGER
);