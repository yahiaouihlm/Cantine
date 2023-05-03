

-- -----------------------------------------------------
-- Table `cantiniere`.`image`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS image(
    idimage SERIAL PRIMARY KEY,
    imageName VARCHAR(400) NOT NULL
);

CREATE TABLE IF NOT EXISTS  class {
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,

    }

CREATE TABLE IF NOT EXISTS  "function" {
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    }

CREATE TABLE  IF NOT EXISTS  studient {
    id SERIAL PRIMARY KEY,
    "name" VARCHAR(100) NOT NULL,
    fullname VARCHAR(100) NOT NULL,
    birthdate DATE NOT NULL,
    registration_date DATE NOT NULL,
    email VARCHAR(300) NOT NULL,
    password VARCHAR(2000) NOT NULL,
    town VARCHAR(100) NOT NULL,
    phone VARCHAR(100);
class_id INT NOT NULL,

    }
-- -----------------------------------------------------
-- Table `cantiniere`.`admin`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS "admin" {
      id SERIAL PRIMARY KEY,
      "name" VARCHAR(100) NOT NULL,
      fullname VARCHAR(100) NOT NULL,
      birthdate DATE NOT NULL,
      registration_date DATE NOT NULL,
      email VARCHAR(300) NOT NULL,
      password VARCHAR(2000) NOT NULL,
      town VARCHAR(100) NOT NULL,
      address VARCHAR(100) NOT NULL,
      phone VARCHAR(100) NOT NULL,
      function_id INT NOT NULL,
      image_idimage INT NOT NULL,
      status INT  NOT NULL,   /* 0 = disabled, 1 = enabled */
      FOREIGN KEY (function_id) REFERENCES "function" (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
      FOREIGN KEY (image_idimage) REFERENCES image(idimage) ON DELETE NO ACTION ON UPDATE NO ACTION
    }




-- -----------------------------------------------------
-- Table `cantiniere`.`plat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS meal(
    id  SERIAL ,
    label  VARCHAR(100) NOT NULL,
    description   TEXT NOT NULL ,
    price  DECIMAL(5,2) NOT NULL,
    category   VARCHAR(46) NOT NULL,
    image_idimage  INT NOT NULL ,
    quantity   INT    DEFAULT 0 ,
    status INT  NOT NULL,   /* 0 = disabled, 1 = enabled */
    PRIMARY KEY (id),
    CHECK (status IN (0,1)),
    CHECK (quantity >= 0),
    CHECK (price >= 0),
    unique (label, description, category),
    FOREIGN KEY (image_idimage) REFERENCES image(idimage) ON DELETE NO ACTION ON UPDATE NO ACTION
    );


CREATE TABLE IF NOT EXISTS menu (
    id SERIAL PRIMARY KEY,
    label  VARCHAR(100) NOT NULL,
    description  TEXT NOT NULL,
    status INT  NOT  NULL,  /* 0 = disabled, 1 = enabled */
    price  DECIMAL(5,2) NOT NULL,
    image_idimage  INT NOT NULL,
    creation_date  DATE NOT NULL,
    quantity INT NOT NULL ,
    CHECK (status IN (0,1)),
    CHECK (quantity >= 0),
    CHECK (price >= 0),
    unique (label, description, price),
    FOREIGN KEY (image_idimage) REFERENCES image (idimage) ON DELETE NO ACTION ON UPDATE NO ACTION
    );

CREATE TABLE IF NOT EXISTS menu_has_meal (
     menu_idMenu INT NOT NULL,
     meal_idmeal INT NOT NULL,
     PRIMARY KEY (menu_idMenu,  meal_idmeal),
    FOREIGN KEY (menu_idMenu) REFERENCES menu (id)ON DELETE CASCADE,
    FOREIGN KEY (meal_idmeal) REFERENCES  meal (id) ON DELETE RESTRICT
    );


