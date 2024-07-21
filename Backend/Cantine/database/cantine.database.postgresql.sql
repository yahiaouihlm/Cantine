CREATE TABLE IF NOT EXISTS tax (
    id  SERIAL PRIMARY KEY,
    tax DECIMAL(5,2) NOT NULL
);

-- -----------------------------------------------------
-- Table `cantiniere`.`image`
-- -----------------------------------------------------


CREATE TABLE IF NOT EXISTS image(
    id UUID PRIMARY KEY,
    name VARCHAR(400) NOT NULL
);



-- -----------------------------------------------------
-- Table `cantiniere`.`class_id`
-- -----------------------------------------------------


CREATE TABLE IF NOT EXISTS  studentClass (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unique(name)
 );


-- -----------------------------------------------------
-- Table `cantiniere`.`function`
-- -----------------------------------------------------


CREATE TABLE IF NOT EXISTS  adminFunction (
    id UUID PRIMARY KEY,
    name VARCHAR(300) NOT NULL,
    unique(name)
);

-- -----------------------------------------------------
-- Table `cantiniere`.`adminService`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS luser (
    id  UUID PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    birthdate DATE NOT NULL,
    registration_date DATE NOT NULL,
    email VARCHAR(1000) NOT NULL,
    password VARCHAR(2000) NOT NULL,
    Wallet DECIMAL(5,2),
    town VARCHAR(1000) NOT NULL,
    address VARCHAR(3000),
    phone VARCHAR(50) ,
    function_id UUID,
    class_id UUID,
    image_id UUID NOT NULL,
    status INT  NOT NULL  DEFAULT 0 ,   /* 0 = disabled, 1 = enabled */
    disable_date DATE DEFAULT NULL,   /*  if disable_date  is  not  null that  mean  it's   removed admin   */
    validation INT   DEFAULT  0 ,   /* 0 = Invalidated  1 = validated */
    unique(email),
    check (status IN (0,1)),
    FOREIGN KEY (function_id) REFERENCES adminFunction (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (class_id)    REFERENCES studentClass  (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (image_id)    REFERENCES image         (id) ON DELETE NO ACTION ON UPDATE NO ACTION
    );

-- -----------------------------------------------------
-- Table `cantiniere`.`role`
-- -----------------------------------------------------
CREATE TABLE role (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    label VARCHAR(200) NOT NULL,
    description TEXT,
    FOREIGN KEY (user_id) REFERENCES luser (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- -----------------------------------------------------
CREATE TYPE TransactionType AS ENUM ('REFUNDS', 'DEDUCTION', 'ADDITION', 'OTHERS');
-- -----------------------------------------------------
-- Table `cantiniere`.`payment`
-- -----------------------------------------------------
 CREATE table  if NOT EXISTS payment (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    admin_id UUID NOT NULL,
    amount DECIMAL(5,2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_time TIME NOT NULL,
    origin TransactionType NOT NULL,
    CHECK (student_id <> admin_id),

    FOREIGN KEY (student_id) REFERENCES luser (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (admin_id) REFERENCES luser (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);


-- -----------------------------------------------------
-- Table `cantiniere`.`order`
-- -----------------------------------------------------

CREATE table  if NOT EXISTS  lorder(
     id UUID PRIMARY KEY,
     student_id UUID NOT NULL,
     creation_date DATE NOT NULL,
     creation_time TIME NOT NULL,
     price DECIMAL(5,2) NOT NULL,
     status INT  NOT NULL DEFAULT 0   ,   /*0 basic statE  1=validate By Admin, 2=taken */
     isCancelled BOOLEAN NOT NULL DEFAULT FALSE,
     qr_code VARCHAR(1000), /* pour faire le qr code  we just make  the  path  to real  image  */
     unique(qr_code),
     check (status IN (0,1,2)),
     FOREIGN KEY (student_id) REFERENCES luser (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table `cantiniere`.`plat`
-- -----------------------------------------------------

CREATE TYPE MealType AS ENUM ('ENTREE', 'PLAT', 'DESSERT', 'BOISSON', 'ACCOMPAGNEMENT', 'AUTRE');


CREATE TABLE IF NOT EXISTS meal(
    id  UUID PRIMARY KEY,
    label  VARCHAR(100) NOT NULL,
    description   TEXT NOT NULL ,
    price  DECIMAL(5,2) NOT NULL,
    category   VARCHAR(101) NOT NULL,
    image_id  UUID NOT NULL ,
    quantity   INT    DEFAULT 0 ,
    status INT  NOT NULL,   /* 0 = disabled, 1 = enabled  , 2 =  to  delete  */
    meal_type MealType NOT NULL,
    CHECK (status IN (0,1,2)),
    CHECK (quantity >= 0),
    CHECK (price >= 0),
    unique (label, description, category),
    FOREIGN KEY (image_id) REFERENCES image(id) ON DELETE NO ACTION ON UPDATE NO ACTION
    );




CREATE TABLE IF NOT EXISTS menu (
    id UUID PRIMARY KEY,
    label  VARCHAR(100) NOT NULL,
    description  TEXT NOT NULL,
    status INT  NOT  NULL,  /* 0 = disabled, 1 = enabled , 3 =  to  delete */
    price  DECIMAL(5,2) NOT NULL,
    image_id  UUID NOT NULL,
    creation_date  DATE NOT NULL,
    quantity INT NOT NULL ,
    CHECK (status IN (0,1 ,2)),
    CHECK (quantity >= 0),
    CHECK (price >= 0),
    unique (label, description, price),
    FOREIGN KEY (image_id) REFERENCES image (id) ON DELETE NO ACTION ON UPDATE NO ACTION
    );

CREATE TABLE IF NOT EXISTS menu_has_meal (
     menu_id UUID NOT NULL,
     meal_id UUID NOT NULL,
     PRIMARY KEY (menu_id,  meal_id),
     FOREIGN KEY (menu_id) REFERENCES menu (id)ON DELETE CASCADE,
     FOREIGN KEY (meal_id) REFERENCES meal (id) ON DELETE RESTRICT
    );


CREATE TABLE IF NOT EXISTS lorder_has_meal (
    order_id  UUID  NOT NULL,
    meal_id UUID NOT NULL,
    FOREIGN KEY (order_id) REFERENCES lorder (id) ON DELETE CASCADE,
    FOREIGN KEY (meal_id) REFERENCES meal (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS lorder_has_menu (
    order_id UUID NOT  NULL,
    menu_id UUID NOT NULL,
    FOREIGN KEY (order_id) REFERENCES lorder (id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES menu (id) ON DELETE RESTRICT
);

CREATE TABLE "confirmation-token"(
    id            UUID PRIMARY KEY,
    token         VARCHAR(255) NOT NULL,
    uuid          INT          NOT NULL,
    creation_date TIMESTAMP    NOT NULL,
    admin_id      UUID REFERENCES luser (id),
    student_id    UUID REFERENCES luser (id),
    CONSTRAINT one_id_null CHECK (
            (admin_id IS NULL AND student_id IS NOT NULL) OR
            (admin_id IS NOT NULL AND student_id IS NULL)
        )
);


