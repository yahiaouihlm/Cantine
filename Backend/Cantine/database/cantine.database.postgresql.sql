

-- -----------------------------------------------------
-- Table `cantiniere`.`image`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS image(
    idimage SERIAL PRIMARY KEY,
    imageName VARCHAR(400) NOT NULL
);



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
    status INT  NOT  NULL,
    price  DECIMAL(5,2) NOT NULL,
    image_idimage  INT NOT NULL,
    creatoin_date  DATE NOT NULL,
    quantite INT NOT NULL ,
    CHECK (status IN (0,1)),
    CHECK (quantity >= 0),
    CHECK (price >= 0),
    unique (label, description),
    FOREIGN KEY (image_idimage) REFERENCES image (idimage) ON DELETE NO ACTION ON UPDATE NO ACTION
    );

CREATE TABLE IF NOT EXISTS menu_has_meal (
     menu_idMenu INT NOT NULL,
     meal_idmeal INT NOT NULL,
     PRIMARY KEY (menu_idMenu, plat_idplat),
    FOREIGN KEY (menu_idMenu) REFERENCES menu (id)ON DELETE CASCADE,
    FOREIGN KEY (meal_idmeal) REFERENCES  meal (id) ON DELETE RESTRICT
    );


