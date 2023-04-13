

-- -----------------------------------------------------
-- Table `cantiniere`.`image`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS image(
    idimage SERIAL PRIMARY KEY,
    imageName VARCHAR(400)
    );



-- -----------------------------------------------------
-- Table `cantiniere`.`plat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS plat(
    idplat SERIAL ,
    label VARCHAR(100) NOT NULL,
    description  TEXT NOT NULL ,
    prixHt  DECIMAL(5,2) NOT NULL,
    categorie  VARCHAR(45) NOT NULL,
    image_idimage  INT NOT NULL ,
    quantite  INT    DEFAULT 0 ,
    status INT  NOT NULL,
    PRIMARY KEY (idplat),
    FOREIGN KEY (image_idimage) REFERENCES image(idimage) ON DELETE ON ACTION  ON UPDATE ON ACTION
    );
