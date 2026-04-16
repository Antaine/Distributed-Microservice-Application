CREATE TABLE IF NOT EXISTS characters (
  character_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  character_class VARCHAR(50) NOT NULL,
  character_race VARCHAR(50) NOT NULL,
  level INT NOT NULL,
  creation_date DATE NOT NULL,
  created_at TIMESTAMP NOT NULL,
  created_by VARCHAR(50) NOT NULL,
  updated_at TIMESTAMP NULL,
  updated_by VARCHAR(50) NULL
);