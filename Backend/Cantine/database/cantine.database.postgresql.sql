CREATE TABLE IF NOT EXISTS taxe (
    id  SERIAL PRIMARY KEY,
    taxe DECIMAL(5,2) NOT NULL,
);

-- -----------------------------------------------------
-- Table `cantiniere`.`image`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS image(
    idimage SERIAL PRIMARY KEY,
    imageName VARCHAR(400) NOT NULL
);

-- -----------------------------------------------------
-- Table `cantiniere`.`class_id`
-- -----------------------------------------------------


CREATE TABLE IF NOT EXISTS  class_id {
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,

    }

-- -----------------------------------------------------
-- Table `cantiniere`.`function`
-- -----------------------------------------------------


CREATE TABLE IF NOT EXISTS "function" (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
    );

-- -----------------------------------------------------
-- Table `cantiniere`.`Student`
-- -----------------------------------------------------

CREATE TABLE  IF NOT EXISTS  studient (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    birthdate DATE NOT NULL,
    registration_date DATE NOT NULL,
    email VARCHAR(300) NOT NULL,
    password VARCHAR(2000) NOT NULL,
    town VARCHAR(400) NOT NULL,
    phone VARCHAR(50) ,
    class_id INT NOT NULL,
    image_idimage INT NOT NULL,
    status INT  NOT NULL,   /* 0 = disabled, 1 = enabled */
    Disable_date DATE,
    unique(email),

    FOREIGN KEY (image_idimage) REFERENCES image(idimage) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (class_id) REFERENCES class_id (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    )
-- -----------------------------------------------------
-- Table `cantiniere`.`admin`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS "admin" (
      id SERIAL PRIMARY KEY,
       firstname VARCHAR(100) NOT NULL,
       lastname VARCHAR(100) NOT NULL,
      birthdate DATE NOT NULL,
      registration_date DATE NOT NULL,
      email VARCHAR(300) NOT NULL,
      password VARCHAR(2000) NOT NULL,
      town VARCHAR(400) NOT NULL,
      address VARCHAR(1000) NOT NULL,
      phone VARCHAR(50) NOT NULL,
      function_id INT NOT NULL,
      image_idimage INT NOT NULL,
      status INT  NOT NULL,   /* 0 = disabled, 1 = enabled */
      unique(email),
      FOREIGN KEY (function_id) REFERENCES "function" (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
      FOREIGN KEY (image_idimage) REFERENCES image(idimage) ON DELETE NO ACTION ON UPDATE NO ACTION
    )



 CREATE table  if NOT EXIST payment (
    id SERIAL PRIMARY KEY,
    student_id INT NOT NULL,
    admin_id INT NOT NULL,
    amount DECIMAL(5,2) NOT NULL,
    payement_date DATE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES student (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (admin_id) REFERENCES admin (id) ON DELETE NO ACTION ON UPDATE NO ACTION

)

-- -----------------------------------------------------
-- Table `cantiniere`.`order`
-- -----------------------------------------------------

CREATE table  if NOT EXIST "order" (
     id SERIAL PRIMARY KEY,
     student_id INT NOT NULL,
     status INT  NOT NULL,   /* 0 = disabled, 1 = enabled */
     creation_date DATE NOT NULL,
     creation_time TIME NOT NULL,
     price DECIMAL(5,2) NOT NULL,
     status INT  NOT NULL,   /* 0 = disabled, 1 = enabled */pour fai
     qr_code VARCHAR(100) NOT NULL , /* pour faire le qr code  we just make  the  path  to real  image  */
     unique(qr_code),
    FOREIGN KEY (student_id) REFERENCES student (id) ON DELETE NO ACTION ON UPDATE NO ACTION
)

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


CREATE TABLE IF NOT EXISTS order_has_meal (
    order_idorder INT NOT NULL,
    meal_idmeal INT NOT NULL,
    PRIMARY KEY (order_idorder, meal_idmeal),
    FOREIGN KEY (order_idorder) REFERENCES "order" (id) ON DELETE CASCADE,
    FOREIGN KEY (meal_idmeal) REFERENCES meal (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS order_has_menu (
    order_idorder INT NOT NULL,
    menu_idmenu INT NOT NULL,
    PRIMARY KEY (order_idorder, menu_idmenu),
    FOREIGN KEY (order_idorder) REFERENCES "order" (id) ON DELETE CASCADE,
    FOREIGN KEY (menu_idmenu) REFERENCES menu (id) ON DELETE RESTRICT
);
