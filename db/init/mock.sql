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


-- Agora inclui todos os campos esperados pelo backend e frontend:
-- title, artist, year, genre, duration, average_rating, reviews_count, total_ratings, image, description

INSERT INTO Albums (title, artist, year, genre, duration, average_rating, reviews_count, total_ratings, image, description) VALUES
('Dark Side of the Moon','Pink Floyd','1973','Progressive Rock','42:49',4.8,10,15000,'/covers/darkside.jpg','Álbum icônico do Pink Floyd, considerado um dos melhores de todos os tempos.'),
('The Wall','Pink Floyd','1979','Progressive Rock','81:09',4.7,12,12000,'/covers/thewall.jpg','Álbum conceitual duplo, famoso por suas músicas e narrativa.'),
('Wish You Were Here','Pink Floyd','1975','Progressive Rock','44:28',4.9,8,9000,'/covers/wywh.jpg','Tributo a Syd Barrett, com faixas emocionantes e instrumentais.'),
('Abbey Road','The Beatles','1969','Rock','47:23',4.9,15,20000,'/covers/abbeyroad.jpg','Último álbum gravado pelos Beatles, com clássicos eternos.'),
('Sgt. Pepper''s Lonely Hearts Club Band','The Beatles','1967','Psychedelic Rock','39:52',4.8,13,18000,'/covers/sgtpepper.jpg','Revolucionou a música pop com experimentações sonoras.'),
('Revolver','The Beatles','1966','Rock','34:43',4.6,9,11000,'/covers/revolver.jpg','Álbum inovador, precursor do psicodelismo.'),
('Thriller','Michael Jackson','1982','Pop','42:19',4.9,20,25000,'/covers/thriller.jpg','Álbum mais vendido da história, com hits mundiais.'),
('Bad','Michael Jackson','1987','Pop','48:16',4.5,11,14000,'/covers/bad.jpg','Sucessos como "Bad" e "Smooth Criminal".'),
('Off the Wall','Michael Jackson','1979','Disco','42:28',4.4,7,8000,'/covers/offthewall.jpg','Mistura de disco, funk e pop, início do estrelato solo.'),
('Back in Black','AC/DC','1980','Hard Rock','41:59',4.7,14,16000,'/covers/backinblack.jpg','Tributo a Bon Scott, riffs marcantes e energia pura.'),
('Highway to Hell','AC/DC','1979','Hard Rock','41:00',4.6,10,12000,'/covers/highwaytohell.jpg','Último álbum com Bon Scott, clássico do rock.'),
('Powerage','AC/DC','1978','Hard Rock','39:00',4.3,6,7000,'/covers/powerage.jpg','Menos conhecido, mas com ótimas faixas.'),
('Rumours','Fleetwood Mac','1977','Rock','39:43',4.8,13,17000,'/covers/rumours.jpg','Um dos álbuns mais vendidos e premiados da história.'),
('Tusk','Fleetwood Mac','1979','Rock','74:25',4.2,7,6000,'/covers/tusk.jpg','Álbum experimental, sucessor de Rumours.'),
('Mirage','Fleetwood Mac','1982','Rock','42:11',4.0,5,4000,'/covers/mirage.jpg','Retorno ao som pop-rock, com hits suaves.'),
('Hotel California','Eagles','1976','Rock','43:28',4.8,12,15000,'/covers/hotelcalifornia.jpg','Contém o clássico "Hotel California".'),
('Desperado','Eagles','1973','Country Rock','35:41',4.1,6,5000,'/covers/desperado.jpg','Álbum conceitual sobre foras-da-lei do Velho Oeste.'),
('One of These Nights','Eagles','1975','Rock','43:08',4.3,8,7000,'/covers/oneofthesenights.jpg','Inclui o hit "Lyin'' Eyes".'),
('Led Zeppelin IV','Led Zeppelin','1971','Hard Rock','42:38',4.9,16,21000,'/covers/ledzep4.jpg','Inclui "Stairway to Heaven", um dos maiores clássicos do rock.'),
('Physical Graffiti','Led Zeppelin','1975','Hard Rock','82:15',4.7,9,13000,'/covers/physicalgraffiti.jpg','Álbum duplo, mistura vários estilos.'),
('Houses of the Holy','Led Zeppelin','1973','Hard Rock','41:05',4.6,8,9000,'/covers/housesoftheholy.jpg','Diversidade musical e experimentação.'),
('Nevermind','Nirvana','1991','Grunge','49:23',4.8,15,18000,'/covers/nevermind.jpg','Revolucionou o rock dos anos 90.'),
('In Utero','Nirvana','1993','Grunge','41:23',4.5,10,11000,'/covers/inutero.jpg','Álbum cru e intenso, último da banda.'),
('Bleach','Nirvana','1989','Grunge','42:37',4.0,5,4000,'/covers/bleach.jpg','Álbum de estreia, som mais pesado.'),
('A Night at the Opera','Queen','1975','Rock','43:08',4.7,11,12000,'/covers/anightattheopera.jpg','Inclui "Bohemian Rhapsody".'),
('News of the World','Queen','1977','Rock','39:28',4.4,8,8000,'/covers/newsoftheworld.jpg','Inclui "We Will Rock You" e "We Are the Champions".'),
('The Game','Queen','1980','Rock','35:42',4.3,7,6000,'/covers/thegame.jpg','Inclui "Another One Bites the Dust".'),
('OK Computer','Radiohead','1997','Alternative Rock','53:21',4.9,14,20000,'/covers/okcomputer.jpg','Álbum inovador, mistura rock e eletrônica.'),
('Kid A','Radiohead','2000','Alternative Rock','49:57',4.7,10,13000,'/covers/kida.jpg','Experimental, influenciou toda uma geração.'),
('In Rainbows','Radiohead','2007','Alternative Rock','42:39',4.8,9,11000,'/covers/inrainbows.jpg','Lançado de forma inovadora, aclamado pela crítica.'),
('Heroes','David Bowie','1977','Art Rock','40:25',4.2,6,5000,'/covers/heroes.jpg','Inclui a faixa-título "Heroes".'),
('Let''s Dance','David Bowie','1983','Pop Rock','39:41',4.0,5,4000,'/covers/letsdance.jpg','Sucesso comercial, som mais pop.'),
('Hunky Dory','David Bowie','1971','Art Rock','41:40',4.5,7,7000,'/covers/hunkydory.jpg','Inclui "Life on Mars?".'),
('The Joshua Tree','U2','1987','Rock','50:11',4.6,12,12000,'/covers/joshuatree.jpg','Inclui "With or Without You".'),
('Achtung Baby','U2','1991','Rock','55:27',4.4,8,9000,'/covers/achtungbaby.jpg','Mudança de sonoridade, mais experimental.'),
('War','U2','1983','Rock','42:03',4.1,6,5000,'/covers/war.jpg','Inclui "Sunday Bloody Sunday".'),
('Sticky Fingers','The Rolling Stones','1971','Rock','46:25',4.5,10,11000,'/covers/stickyfingers.jpg','Inclui "Brown Sugar".'),
('Let It Bleed','The Rolling Stones','1969','Rock','42:21',4.4,9,9000,'/covers/letitbleed.jpg','Inclui "Gimme Shelter".'),
('Exile on Main St.','The Rolling Stones','1972','Rock','67:07',4.3,8,8000,'/covers/exileonmainst.jpg','Álbum duplo, som cru e autêntico.'),
('Blonde on Blonde','Bob Dylan','1966','Folk Rock','72:57',4.2,7,7000,'/covers/blondeonblonde.jpg','Um dos primeiros álbuns duplos do rock.'),
('Highway 61 Revisited','Bob Dylan','1965','Folk Rock','51:26',4.1,6,6000,'/covers/highway61.jpg','Inclui "Like a Rolling Stone".'),
('Blood on the Tracks','Bob Dylan','1975','Folk Rock','51:42',4.4,8,8000,'/covers/bloodonthetracks.jpg','Álbum confessional, muito elogiado.'),
('Who''s Next','The Who','1971','Rock','43:38',4.5,9,10000,'/covers/whosnext.jpg','Inclui "Baba O''Riley".'),
('Tommy','The Who','1969','Rock Opera','75:15',4.2,7,7000,'/covers/tommy.jpg','Ópera rock clássica.'),
('Quadrophenia','The Who','1973','Rock Opera','81:46',4.0,6,6000,'/covers/quadrophenia.jpg','Outra ópera rock da banda.'),
('Master of Puppets','Metallica','1986','Thrash Metal','54:47',4.8,13,15000,'/covers/masterofpuppets.jpg','Considerado o melhor álbum do Metallica.'),
('Ride the Lightning','Metallica','1984','Thrash Metal','47:25',4.7,10,11000,'/covers/ridethelightning.jpg','Inclui "Fade to Black".'),
('Black Album','Metallica','1991','Heavy Metal','62:40',4.6,12,12000,'/covers/blackalbum.jpg','Inclui "Enter Sandman".'),
('Californication','Red Hot Chili Peppers','1999','Alternative Rock','56:24',4.5,11,10000,'/covers/californication.jpg','Retorno de John Frusciante à banda.'),
('By the Way','Red Hot Chili Peppers','2002','Alternative Rock','68:46',4.3,8,8000,'/covers/bytheway.jpg','Som mais melódico e introspectivo.'),
('Stadium Arcadium','Red Hot Chili Peppers','2006','Alternative Rock','122:34',4.2,7,7000,'/covers/stadiumarcadium.jpg','Álbum duplo, mistura de estilos.'),
('L.A. Woman','The Doors','1971','Psychedelic Rock','48:24',4.4,9,9000,'/covers/lawoman.jpg','Último álbum com Jim Morrison.'),
('Strange Days','The Doors','1967','Psychedelic Rock','35:05',4.1,6,6000,'/covers/strangedays.jpg','Inclui "People Are Strange".'),
('Morrison Hotel','The Doors','1970','Blues Rock','37:05',4.0,5,5000,'/covers/morrisonhotel.jpg','Retorno às raízes do blues.'),
('AM','Arctic Monkeys','2013','Indie Rock','41:57',4.6,10,11000,'/covers/am.jpg','Inclui "Do I Wanna Know?".'),
('Favourite Worst Nightmare','Arctic Monkeys','2007','Indie Rock','37:13',4.4,8,9000,'/covers/fwn.jpg','Segundo álbum da banda, mais pesado.'),
('Whatever People Say I Am, That''s What I''m Not','Arctic Monkeys','2006','Indie Rock','41:18',4.5,9,9500,'/covers/wpsia.jpg','Álbum de estreia, sucesso imediato.'),
('(What''s the Story) Morning Glory?','Oasis','1995','Britpop','50:02',4.7,12,12000,'/covers/morningglory.jpg','Inclui "Wonderwall".'),
('Definitely Maybe','Oasis','1994','Britpop','51:44',4.3,7,8000,'/covers/definitelymaybe.jpg','Álbum de estreia, referência do britpop.'),
('Be Here Now','Oasis','1997','Britpop','71:38',4.0,5,6000,'/covers/beherenow.jpg','Álbum mais longo e ambicioso da banda.');


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
(1,1,10,'Incrível álbum!','2025-04-20 10:00:00','2025-04-20'),
(2,1,8,'Gostei muito.','2025-04-20 11:00:00','2025-04-20'),
(3,2,9,'Clássico eterno.','2025-04-21 09:30:00','2025-04-21'),
(4,2,6,'Bom, mas não genial.','2025-04-21 14:00:00','2025-04-21'),
(5,3,7,'Ótimas faixas.','2025-04-22 08:15:00','2025-04-22'),
(6,4,4,'Não curti tanto.','2025-04-22 17:45:00','2025-04-22'),
(7,5,9,'Excelente produção.','2025-04-23 12:00:00','2025-04-23'),
(8,6,5,'Médio.','2025-04-23 15:20:00','2025-04-23'),
(9,7,8,'Vale a pena ouvir.','2025-04-24 16:10:00','2025-04-24'),
(10,8,10,'Meu favorito!','2025-04-24 18:00:00','2025-04-24'),

(1,1,10,'Obra-prima do Pink Floyd!','2025-04-20 10:00:00','2025-04-20'),
(1,2,8,'Muito bom, mas prefiro The Wall.','2025-04-20 11:00:00','2025-04-20'),
(1,3,9,'Clássico absoluto.','2025-04-20 12:00:00','2025-04-20'),
(1,4,10,'Sempre me emociono ouvindo.','2025-04-20 13:00:00','2025-04-20'),
(1,5,7,'Excelente produção.','2025-04-20 14:00:00','2025-04-20'),
(1,6,10,'Melhor álbum da banda.','2025-04-20 15:00:00','2025-04-20'),
(1,7,9,'Impossível enjoar.','2025-04-20 16:00:00','2025-04-20'),
(1,8,6,'Muito bom, mas superestimado.','2025-04-20 17:00:00','2025-04-20'),
(1,9,10,'Faixas perfeitas.','2025-04-20 18:00:00','2025-04-20'),
(1,10,10,'Meu favorito da vida.','2025-04-20 19:00:00','2025-04-20'),
(2,1,8,'The Wall é incrível do início ao fim.','2025-04-21 10:00:00','2025-04-21'),
(2,2,10,'Melhor conceito de álbum.','2025-04-21 11:00:00','2025-04-21'),
(2,3,9,'Faixas marcantes demais.','2025-04-21 12:00:00','2025-04-21'),
(2,4,7,'Gosto muito, mas prefiro DSOTM.','2025-04-21 13:00:00','2025-04-21'),
(2,5,10,'Obra de arte.','2025-04-21 14:00:00','2025-04-21'),
(2,6,10,'Pink Floyd no auge.','2025-04-21 15:00:00','2025-04-21'),
(2,7,6,'Algumas faixas são longas demais.','2025-04-21 16:00:00','2025-04-21'),
(2,8,9,'Sempre volto a ouvir.','2025-04-21 17:00:00','2025-04-21'),
(2,9,10,'Álbum essencial.','2025-04-21 18:00:00','2025-04-21'),
(2,10,9,'Top 3 da banda.','2025-04-21 19:00:00','2025-04-21'),

(3,1,10,'Wish You Were Here é emocionante.','2025-04-22 10:00:00','2025-04-22'),
(3,2,10,'Shine On You Crazy Diamond é perfeita.','2025-04-22 11:00:00','2025-04-22'),
(3,3,7,'Muito bom, mas não é meu favorito.','2025-04-22 12:00:00','2025-04-22'),
(3,4,9,'Letra e melodia incríveis.','2025-04-22 13:00:00','2025-04-22'),
(3,5,10,'Pink Floyd sempre surpreende.','2025-04-22 14:00:00','2025-04-22'),
(3,6,8,'Gosto mais do The Wall.','2025-04-22 15:00:00','2025-04-22'),
(3,7,10,'Álbum para ouvir inteiro.','2025-04-22 16:00:00','2025-04-22'),
(3,8,9,'Faixas longas, mas valem a pena.','2025-04-22 17:00:00','2025-04-22'),
(3,9,10,'Recomendo para todos.','2025-04-22 18:00:00','2025-04-22'),
(3,10,10,'Obra-prima.','2025-04-22 19:00:00','2025-04-22'),

(4,1,8,'Beatles no auge!','2025-04-23 10:00:00','2025-04-23'),
(4,2,7,'Abbey Road é clássico.','2025-04-23 11:00:00','2025-04-23'),
(4,3,10,'Faixas inesquecíveis.','2025-04-23 12:00:00','2025-04-23'),
(4,4,9,'Sempre atual.','2025-04-23 13:00:00','2025-04-23'),
(4,5,8,'Muito bom, mas prefiro Revolver.','2025-04-23 14:00:00','2025-04-23'),
(4,6,10,'Beatles são geniais.','2025-04-23 15:00:00','2025-04-23'),
(4,7,10,'Melhor álbum deles.','2025-04-23 16:00:00','2025-04-23'),
(4,8,8,'Ótima produção.','2025-04-23 17:00:00','2025-04-23'),
(4,9,9,'Recomendo demais.','2025-04-23 18:00:00','2025-04-23'),
(4,10,10,'Top 5 da minha vida.','2025-04-23 19:00:00','2025-04-23'),

(5,1,7,'Sgt. Pepper é revolucionário.','2025-04-24 10:00:00','2025-04-24'),
(5,2,10,'Beatles sempre inovando.','2025-04-24 11:00:00','2025-04-24'),
(5,3,9,'Álbum histórico.','2025-04-24 12:00:00','2025-04-24'),
(5,4,8,'Muito criativo.','2025-04-24 13:00:00','2025-04-24'),
(5,5,10,'Obra-prima da música.','2025-04-24 14:00:00','2025-04-24'),
(5,6,8,'Prefiro Abbey Road, mas esse é ótimo.','2025-04-24 15:00:00','2025-04-24'),
(5,7,9,'Beatles nunca decepcionam.','2025-04-24 16:00:00','2025-04-24'),
(5,8,10,'Faixas incríveis.','2025-04-24 17:00:00','2025-04-24'),
(5,9,8,'Muito bom mesmo.','2025-04-24 18:00:00','2025-04-24'),
(5,10,10,'Top 10 da história.','2025-04-24 19:00:00','2025-04-24');


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

INSERT INTO ReviewLikes (review_id, user_id) VALUES
(1, 2),
(1, 3),
(1, 4),
(2, 1),
(2, 4),
(3, 1),
(3, 2),
(3, 5),
(4, 2),
(5, 3);

-- Artistas
INSERT INTO Artists (name) VALUES
('Pink Floyd'),
('The Beatles'),
('Michael Jackson'),
('AC/DC'),
('Fleetwood Mac'),
('Eagles'),
('Led Zeppelin'),
('Nirvana'),
('Queen'),
('Radiohead'),
('David Bowie'),
('U2'),
('The Rolling Stones'),
('Bob Dylan'),
('The Who'),
('Metallica'),
('Red Hot Chili Peppers'),
('The Doors'),
('Arctic Monkeys'),
('Oasis');

-- Ligação Artistas-Álbuns (3 álbuns por artista)
INSERT INTO ArtistAlbums (artist_id, album_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 4), (2, 5), (2, 6),
(3, 7), (3, 8), (3, 9),
(4,10), (4,11), (4,12),
(5,13), (5,14), (5,15),
(6,16), (6,17), (6,18),
(7,19), (7,20), (7,21),
(8,22), (8,23), (8,24),
(9,25), (9,26), (9,27),
(10,28), (10,29), (10,30),
(11,31), (11,32), (11,33),
(12,34), (12,35), (12,36),
(13,37), (13,38), (13,39),
(14,40), (14,41), (14,42),
(15,43), (15,44), (15,45),
(16,46), (16,47), (16,48),
(17,49), (17,50), (17,51),
(18,52), (18,53), (18,54),
(19,55), (19,56), (19,57),
(20,58), (20,59), (20,60);
