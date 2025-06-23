CREATE TABLE `two_rooms_and_a_boom`.`team` (
    `teamId` INT NOT NULL AUTO_INCREMENT,
    `colour` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`teamId`)
);

CREATE TABLE `two_rooms_and_a_boom`.`player` (
    `playerId` INT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`playerId`),
    UNIQUE INDEX `username_UNIQUE` (`username` ASC)
);

CREATE TABLE `two_rooms_and_a_boom`.`card` (
    `cardId` INT NOT NULL AUTO_INCREMENT,
    `cardTitle` VARCHAR(45) NOT NULL,
    `isActive` TINYINT NOT NULL DEFAULT 0,
    `isBasic` TINYINT NOT NULL DEFAULT 0,
    `fileName` VARCHAR(45) NOT NULL,
    `teamId` INT NOT NULL,
    PRIMARY KEY (`cardId`),
    INDEX `teamId_idx` (`teamId` ASC) VISIBLE,
    CONSTRAINT `teamId`
       FOREIGN KEY (`teamId`)
           REFERENCES `two_rooms_and_a_boom`.`team` (`teamId`)
           ON DELETE NO ACTION
           ON UPDATE NO ACTION
);

CREATE TABLE `two_rooms_and_a_boom`.`gameState` (
    `gameId` INT NOT NULL AUTO_INCREMENT,
    `gameState` ENUM('WAITING_FOR_HOST','IN_PROGRESS', 'REVEAL_CARDS') NOT NULL,
    `gameStartTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `matchEndTime` TIMESTAMP NULL,
    PRIMARY KEY (`gameId`)
);

INSERT INTO `two_rooms_and_a_boom`.`gameState` (`gameId`,`gameState`) VALUES (1,'WAITING_FOR_HOST');

CREATE TABLE `two_rooms_and_a_boom`.`game` (
    `playerId` INT NOT NULL,
    `gameId` INT NOT NULL,
    `cardId` INT NOT NULL,
    `room` ENUM('A', 'B') NOT NULL,
    PRIMARY KEY (`playerId`),
    INDEX `player_idx` (`playerId` ASC) VISIBLE,
    INDEX `card_idx` (`cardId` ASC) VISIBLE,
    CONSTRAINT `player`
       FOREIGN KEY (`playerId`)
           REFERENCES `two_rooms_and_a_boom`.`player` (`playerId`)
           ON DELETE NO ACTION
           ON UPDATE NO ACTION,
    CONSTRAINT `card`
       FOREIGN KEY (`cardId`)
           REFERENCES `two_rooms_and_a_boom`.`card` (`cardId`)
           ON DELETE NO ACTION
           ON UPDATE NO ACTION,
   CONSTRAINT `gameState`
       FOREIGN KEY (`gameId`)
           REFERENCES `two_rooms_and_a_boom`.`gameState` (`gameId`)
           ON DELETE CASCADE
           ON UPDATE NO ACTION
);
