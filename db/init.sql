CREATE TABLE DATA (
  ID NUMBER auto_increment,
  DepCode VARCHAR2(20 char) NOT NULL,
  DepJob VARCHAR2(100 char) NOT NULL,
  Description VARCHAR2(255),
  CONSTRAINT pk_data                 PRIMARY KEY (ID)
);

insert into DATA (DepCode, DepJob, Description) values('DC1', 'DJ1', 'DJ1D');
insert into DATA (DepCode, DepJob, Description) values('DC2', 'DJ2', null);
