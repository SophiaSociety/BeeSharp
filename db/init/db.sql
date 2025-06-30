-- 1) Usuários e sessão
CREATE TABLE Users (
    id           SERIAL PRIMARY KEY,
    username     VARCHAR(255) NOT NULL UNIQUE,
    password_hash     VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    profile_image  BYTEA
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
    title       VARCHAR(255) NOT NULL,
    artist      VARCHAR(255),
    year        VARCHAR(10),         -- Novo campo
    genre       VARCHAR(100),        -- Novo campo
    duration    VARCHAR(10),         -- Novo campo
    average_rating NUMERIC(3,2) NOT NULL DEFAULT 0.00,
    reviews_count  INTEGER     NOT NULL DEFAULT 0,
    total_ratings  INTEGER     NOT NULL DEFAULT 0, -- Novo campo
    image       VARCHAR(255),        -- Novo campo
    description TEXT                 -- Novo campo
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
    rating         INTEGER NOT NULL CHECK (rating >= 0 AND rating <= 10), -- agora aceita .5
    content      TEXT NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
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

-- 7) Curtidas em resenhas
CREATE TABLE ReviewLikes (
    id         SERIAL PRIMARY KEY,
    review_id  INT NOT NULL,
    user_id    INT NOT NULL,
    FOREIGN KEY (review_id) REFERENCES Reviews(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id)   REFERENCES Users(id)   ON DELETE CASCADE,
    CONSTRAINT unique_like_per_user_review UNIQUE (review_id, user_id)
);

-- 8) Artistas
CREATE TABLE Artists (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- 9) Ligação Artistas-Álbuns (muitos-para-muitos)
CREATE TABLE ArtistAlbums (
    artist_id INT NOT NULL,
    album_id INT NOT NULL,
    FOREIGN KEY (artist_id) REFERENCES Artists(id) ON DELETE CASCADE,
    FOREIGN KEY (album_id) REFERENCES Albums(id) ON DELETE CASCADE,
    PRIMARY KEY (artist_id, album_id)
);