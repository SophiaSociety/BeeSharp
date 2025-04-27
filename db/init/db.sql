-- 1) Usuários e sessão
CREATE TABLE Users (
    id           SERIAL PRIMARY KEY,
    username     VARCHAR(255) NOT NULL UNIQUE,
    password_hash     VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE UserSessions (
    id         SERIAL PRIMARY KEY,
    user_id    INT NOT NULL,
    start_time TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time   TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);

-- 2) Seguir usuários
CREATE TABLE UserFollows (
    user_id     INT NOT NULL,
    follower_id INT NOT NULL,
    FOREIGN KEY (user_id)     REFERENCES Users(id) ON DELETE CASCADE, -- se apagar um user, o follow deixa de existir também
    FOREIGN KEY (follower_id) REFERENCES Users(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, follower_id)
);

-- 3) Álbuns e favoritar álbuns 
CREATE TABLE Albums (
    id          SERIAL PRIMARY KEY,
    -- external_id VARCHAR(255) NOT NULL UNIQUE,  -- ID da API (Spotify/Last.fm) mais para frente
    title       VARCHAR(255) NOT NULL,
    artist      VARCHAR(255),
    reviews_count  INTEGER     NOT NULL DEFAULT 0,
    average_rating NUMERIC(3,2) NOT NULL DEFAULT 0.00
);

CREATE TABLE AlbumFavorites (
    album_id1    INT,
    album_id2    INT,
    album_id3    INT,
    album_id4    INT,
    user_id INT NOT NULL,
    FOREIGN KEY (album_id1)    REFERENCES Albums(id) ON DELETE CASCADE,
    FOREIGN KEY (album_id2)    REFERENCES Albums(id) ON DELETE CASCADE,
    FOREIGN KEY (album_id3)    REFERENCES Albums(id) ON DELETE CASCADE,
    FOREIGN KEY (album_id4)    REFERENCES Albums(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    PRIMARY KEY (album_id1, album_id2, album_id3, album_id4, user_id)
);

-- 4) Histórico de audições
CREATE TABLE ListeningHistory (
    id          SERIAL PRIMARY KEY,
    user_id     INT    NOT NULL,
    album_id    INT    NOT NULL,
    FOREIGN KEY (user_id)  REFERENCES Users(id)  ON DELETE CASCADE,
    FOREIGN KEY (album_id) REFERENCES Albums(id) ON DELETE CASCADE
);

-- 5) Avaliações e resenhas de álbum
CREATE TABLE Reviews (
    id             SERIAL PRIMARY KEY,
    album_id       INT    NOT NULL,
    user_id        INT    NOT NULL,
    rating         INT NOT NULL CHECK (rating >= 0 AND rating <= 5),
    review    TEXT,
    creation_date  DATE   NOT NULL,
    modified_date  DATE   NOT NULL,
    FOREIGN KEY (user_id)  REFERENCES Users(id)  ON DELETE CASCADE,
    FOREIGN KEY (album_id) REFERENCES Albums(id) ON DELETE CASCADE
);

-- 6) Comentários em resenhas
CREATE TABLE Commentaries (
    id             SERIAL PRIMARY KEY,
    review_id      INT    NOT NULL,
    user_id   INT    NOT NULL,
    commentary     TEXT   NOT NULL,
    creation_date  DATE   NOT NULL,
    modified_date  DATE   NOT NULL,
    FOREIGN KEY (review_id)     REFERENCES Reviews(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id)  REFERENCES Users(id)   ON DELETE CASCADE
);