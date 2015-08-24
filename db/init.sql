CREATE TABLE DATA (
  ID NUMBER auto_increment,
  DepCode VARCHAR2(20 char) NOT NULL,
  DepJob VARCHAR2(100 char) NOT NULL,
  Description VARCHAR2(255),
  CONSTRAINT pk_data                 PRIMARY KEY (ID),
  CONSTRAINT u_data_natkey           UNIQUE (DepCode,DepJob)
);

insert into DATA (DepCode, DepJob, Description) values('DC1', 'DJ1', 'DJ1D');
insert into DATA (DepCode, DepJob, Description) values('DC2', 'DJ2', null);
insert into DATA (DepCode, DepJob, Description) values('DC3', 'DJ3', 'DJ3D');
insert into DATA (DepCode, DepJob, Description) values('DC4', 'DJ4', null);
insert into DATA (DepCode, DepJob, Description) values('DC5', 'DJ5', 'DJ5D');
