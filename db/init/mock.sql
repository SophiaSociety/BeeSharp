-- Simulação de dados para BeeSharp

-- Limpa tabelas para re-população completa

DELETE FROM Reviews;
DELETE FROM Commentaries;
DELETE FROM Userfollows;
DELETE FROM ListeningHistory;
DELETE FROM AlbumFavorites;
DELETE FROM Albums;
DELETE FROM Users;

INSERT INTO Users (username, password_hash, email) VALUES 
('alice',  'hash1',  'alice@example.com'),
('bob',    'hash2',  'bob@example.com'),
('carol',  'hash3',  'carol@example.com'),
('dave',   'hash4',  'dave@example.com'),
('eve',    'hash5',  'eve@example.com'),
('frank',  'hash6',  'frank@example.com'),
('grace',  'hash7',  'grace@example.com'),
('heidi',  'hash8',  'heidi@example.com'),
('ivan',   'hash9',  'ivan@example.com'),
('judy',   'hash10', 'judy@example.com');

INSERT INTO UserSessions (user_id, start_time, end_time) VALUES 
(1, '2025-04-20 08:00:00+00','2025-04-20 09:00:00+00'),
(2, '2025-04-20 09:15:00+00','2025-04-20 10:00:00+00'),
(3, '2025-04-21 07:30:00+00','2025-04-21 08:45:00+00'),
(4, '2025-04-21 11:00:00+00','2025-04-21 12:00:00+00'),
(5, '2025-04-22 13:00:00+00','2025-04-22 14:30:00+00'),
(6, '2025-04-22 15:00:00+00','2025-04-22 16:15:00+00'),
(7, '2025-04-23 10:00:00+00','2025-04-23 11:20:00+00'),
(8, '2025-04-23 12:00:00+00','2025-04-23 13:45:00+00'),
(9, '2025-04-24 09:00:00+00','2025-04-24 09:50:00+00'),
(10,'2025-04-24 14:00:00+00','2025-04-24 14:30:00+00');

INSERT INTO UserFollows (user_id, follower_id) VALUES
(1,2),
(2,3),
(3,4),
(4,5),
(5,6),
(6,7),
(7,8),
(8,9),
(9,10),
(10,1);

INSERT INTO Albums (title, artist) VALUES
('Dark Side of the Moon','Pink Floyd'),
('Abbey Road','The Beatles'),
('Thriller','Michael Jackson'),
('Back in Black','AC/DC'),
('Rumours','Fleetwood Mac'),
('Hotel California', 'Eagles'),
('Led Zeppelin IV','Led Zeppelin'),
('The Wall', 'Pink Floyd'),
('Sgt. Pepper''s Lonely Hearts Club Band','The Beatles'),
('Nevermind', 'Nirvana');

INSERT INTO AlbumFavorites (album_id1, album_id2, album_id3, album_id4, user_id) VALUES
(1,2,3,4,1),
(2,3,4,5,2),
(3,4,5,6,3),
(4,5,6,7,4),
(5,6,7,8,5),
(6,7,8,9,6),
(7,8,9,10,7),
(8,9,10,1,8),
(9,10,1,2,9),
(10,1,2,3,10);

INSERT INTO ListeningHistory (user_id, album_id) VALUES
(1,1),
(1,2),
(2,3),
(2,4),
(3,5),
(4,6),
(5,7),
(6,8),
(7,9),
(8,10);

INSERT INTO Reviews (album_id, user_id, rating, content, created_at, modified_date) VALUES
(1,1,5,'Incrível álbum!','2025-04-20 10:00:00','2025-04-20'),
(2,1,4,'Gostei muito.','2025-04-20 11:00:00','2025-04-20'),
(3,2,5,'Clássico eterno.','2025-04-21 09:30:00','2025-04-21'),
(4,2,3,'Bom, mas não genial.','2025-04-21 14:00:00','2025-04-21'),
(5,3,4,'Ótimas faixas.','2025-04-22 08:15:00','2025-04-22'),
(6,4,2,'Não curti tanto.','2025-04-22 17:45:00','2025-04-22'),
(7,5,5,'Excelente produção.','2025-04-23 12:00:00','2025-04-23'),
(8,6,3,'Médio.','2025-04-23 15:20:00','2025-04-23'),
(9,7,4,'Vale a pena ouvir.','2025-04-24 16:10:00','2025-04-24'),
(10,8,5,'Meu favorito!','2025-04-24 18:00:00','2025-04-24');


INSERT INTO Commentaries (review_id, user_id, commentary, creation_date, modified_date) VALUES
(1,2,'Concordo totalmente!','2025-04-20','2025-04-20'),
(1,3,'Melhores faixas são ''Time'' e ''Money''.','2025-04-20','2025-04-20'),
(2,1,'Boa escolha de álbum.','2025-04-20','2025-04-20'),
(2,4,'Também gostei muito.','2025-04-20','2025-04-20'),
(3,1,'O melhor disco de todos.','2025-04-21','2025-04-21'),
(4,2,'Interessante sua opinião.','2025-04-21','2025-04-21'),
(5,3,'Faixas incríveis mesmo.','2025-04-22','2025-04-22'),
(6,4,'Entendo seu ponto.','2025-04-22','2025-04-22'),
(7,5,'Excelente escolha.','2025-04-23','2025-04-23'),
(8,6,'Comentário interessante.','2025-04-23','2025-04-23');