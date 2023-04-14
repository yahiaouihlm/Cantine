

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
    category   VARCHAR(45) NOT NULL,
    image_idimage  INT NOT NULL ,
    quantity   INT    DEFAULT 0 ,
    status INT  NOT NULL,   /* 0 = disabled, 1 = enabled */
    PRIMARY KEY (id),
    CHECK (status IN (0,1)),
    CHECK (quantity >= 0),
    CHECK (price >= 0),
    unique (label, description , price, category),
    FOREIGN KEY (image_idimage) REFERENCES image(idimage) ON DELETE RESTRICT ON UPDATE RESTRICT
);
