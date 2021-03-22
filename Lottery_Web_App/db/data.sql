CREATE TABLE userAccounts (
Firstname varchar(50) NOT NULL,
Lastname varchar(50) NOT NULL, 
Email varchar(50) NOT NULL,
Phone varchar(50) NOT NULL,
Username varchar(50) NOT NULL,
Pwd varchar(200) NOT NULL,
Admin varchar(10) NOT NULL,
PRIMARY KEY (Username, Pwd)
);




