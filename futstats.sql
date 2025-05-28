DROP DATABASE IF EXISTS futstats;
CREATE DATABASE IF NOT EXISTS futstats;
USE futstats;

/*--------------- TABELA DE USUARIOS ----------------------*/
CREATE TABLE IF NOT EXISTS users(
	id_user INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL UNIQUE,
    user_password VARCHAR(12) NOT NULL,
    retrieve_code VARCHAR(12) NOT NULL,
    rank_code VARCHAR(12) NOT NULL,
    user_image VARCHAR(100),
    user_rank INT DEFAULT 1
);
ALTER TABLE users ADD CONSTRAINT chk_email
CHECK(email LIKE "%@%" AND email LIKE "%.com%"); -- Checar email

ALTER TABLE users ADD CONSTRAINT chk_user_rank
CHECK(user_rank BETWEEN 1 AND 3); -- Niveís do usuário

ALTER TABLE users ADD CONSTRAINT chk_rank_code
CHECK(LENGTH(rank_code) >= 12); -- 12 Caracteres

/*--------------- TABELA DE TIMES ----------------------*/
CREATE TABLE IF NOT EXISTS teams (
    id_team INT PRIMARY KEY AUTO_INCREMENT,
    team_name VARCHAR(20) NOT NULL UNIQUE,
    season_wins INT DEFAULT 0,
    season_losses INT DEFAULT 0,
    goals_scored INT DEFAULT 0 CHECK (goals_scored >= 0),
	goals_conceded INT DEFAULT 0 CHECK (goals_conceded >= 0),
    team_feeling VARCHAR(4) DEFAULT "OK" CHECK (team_feeling IN ('OK', 'RUIM', 'BOA')),
    table_stading INT CHECK (table_standing BETWEEN 1 AND 20),
    weak_performance INT CHECK (weak_performance BETWEEN 0 AND 100),
    strong_performance INT CHECK (strong_performance BETWEEN 0 AND 100),
    total_competitions INT CHECK (total_competitions BETWEEN 1 AND 5),
    motivation VARCHAR(4) DEFAULT "OK" CHECK (motivation IN ('OK', 'RUIM', 'BOA')),
    team_image VARCHAR(300)
);

/*--------------- TABELA DE JOGOS ----------------------*/
CREATE TABLE IF NOT EXISTS games(
	id_game INT PRIMARY KEY AUTO_INCREMENT,
	game_day DATE NOT NULL,
    total_goals INT DEFAULT 0 CHECK (total_goals >= 0),
    home_id INT NOT NULL,
    home_goals INT DEFAULT 0 CHECK (home_goals >= 0),
    home_corners INT DEFAULT 0 CHECK (home_corners >= 0),
    home_shots INT DEFAULT 0 CHECK (home_shots >= 0),
    away_id INT NOT NULL,
    away_goals INT DEFAULT 0 CHECK (away_goals >= 0),
    away_corners INT DEFAULT 0 CHECK (away_corners >= 0),
    away_shots INT DEFAULT 0 CHECK (away_shots >= 0),
    game_importance  VARCHAR(10) CHECK (game_importance IN ("ALTA","REGULAR","BAIXA")),
    FOREIGN KEY(home_id) REFERENCES teams(id_team) ON DELETE CASCADE,
    FOREIGN KEY(away_id) REFERENCES teams(id_team) ON DELETE CASCADE
);

/*--------------- TABELA DE APOSTAS ----------------------*/
CREATE TABLE IF NOT EXISTS guesses(
	id_guess INT PRIMARY KEY AUTO_INCREMENT,
    id_game INT NOT NULL,
    myteam_id INT NOT NULL,
    otherteam_id INT NOT NULL,
    id_user INT NOT NULL,
    winner_id INT,
    odd DECIMAL(10,2) NOT NULL CHECK(odd >= 0),
    bet_amount DECIMAL(4,2) DEFAULT 0 CHECK(bet_amount >= 0),
    profit_loss DECIMAL(10,2),
    FOREIGN KEY(id_game) REFERENCES games(id_game),
    FOREIGN KEY(myteam_id) REFERENCES teams(id_team),
    FOREIGN KEY(otherteam_id) REFERENCES teams(id_team),
    FOREIGN KEY(id_user) REFERENCES users(id_user) ON DELETE CASCADE
);

/*--------------- INSERÇÃO DO ADM ----------------------*/
INSERT INTO users (username, email, user_password, retrieve_code, rank_code, user_image, user_rank)
VALUES (
  'admin', 
  'william.cesarbds2016@gmail.com', 
  'admin123',
  'recupera123', 
  'rankadm12345', 
  NULL,        
  3     
);

/*--------------- EVENTO PARA ATUALIZAÇÃO ----------------------*/
CREATE EVENT IF NOT EXISTS trim_games_event
ON SCHEDULE EVERY 1 HOUR
DO
  DELETE FROM games
  WHERE id_game IN (
    SELECT id_game FROM (
      SELECT id_game FROM games ORDER BY game_day ASC LIMIT 10
    ) AS sub
  )
  AND (SELECT COUNT(*) FROM games) > 50;

SET GLOBAL event_scheduler = ON;

    
    

