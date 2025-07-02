import requests
import base64
from time import sleep
import random

# --- CREDENCIAIS DO SPOTIFY ---
CLIENT_ID = "SEU CLIENT ID"
CLIENT_SECRET = "SEU CLIENT SECRET"

# --- AUTENTICAÇÃO SPOTIFY ---
def get_spotify_token(client_id, client_secret):
    auth = base64.b64encode(f"{client_id}:{client_secret}".encode()).decode()
    headers = {"Authorization": f"Basic {auth}"}
    data = {"grant_type": "client_credentials"}
    r = requests.post("https://accounts.spotify.com/api/token", headers=headers, data=data)
    if r.status_code != 200:
        print(r.text)
        return None
    return r.json()['access_token']

# --- BUSCA ID DE ÁLBUM POR ARTISTA E TÍTULO ---
def search_album_id(token, artist_name, album_name):
    headers = {"Authorization": f"Bearer {token}"}
    query = f"album:{album_name} artist:{artist_name}"
    params = {"q": query, "type": "album", "limit": 1}
    r = requests.get("https://api.spotify.com/v1/search", headers=headers, params=params)
    if r.status_code != 200:
        return None
    items = r.json().get("albums", {}).get("items", [])
    if not items:
        return None
    return items[0]["id"]

# --- PEGA ÁLBUNS COM DADOS COMPLETOS ---
def get_album_data(token, album_id):
    headers = {"Authorization": f"Bearer {token}"}

    r = requests.get(f"https://api.spotify.com/v1/albums/{album_id}", headers=headers)
    if r.status_code != 200:
        return None
    album = r.json()

    artist_id = album['artists'][0]['id']
    r = requests.get(f"https://api.spotify.com/v1/artists/{artist_id}", headers=headers)
    if r.status_code != 200:
        return None
    artist = r.json()

    total_ms = sum(track['duration_ms'] for track in album['tracks']['items'])
    total_minutes = total_ms // 60000
    hours = total_minutes // 60
    minutes = total_minutes % 60
    duration = f"{hours}:{minutes:02}"

    return {
        "title": album['name'],
        "artist": artist['name'],
        "year": album['release_date'][:4],
        "genre": artist['genres'][0] if artist.get('genres') else None,
        "duration": duration,
        "image": album['images'][0]['url'] if album.get('images') else None,
        "description": f"Álbum do artista {artist['name']}",
        "average_rating": round(random.uniform(2.5, 5.0), 2),
        "reviews_count": random.randint(5, 100),
        "total_ratings": random.randint(5, 300)
    }

# --- GERA SQL ---
def album_to_sql(album):
    def esc(s):
        return s.replace("'", "''") if s else "NULL"

    return f"INSERT INTO Albums (title, artist, year, genre, duration, average_rating, reviews_count, total_ratings, image, description) VALUES ('{esc(album['title'])}', '{esc(album['artist'])}', '{album['year']}', '{esc(album['genre'])}', '{album['duration']}', {album['average_rating']}, {album['reviews_count']}, {album['total_ratings']}, '{esc(album['image'])}', '{esc(album['description'])}');"

# --- EXECUÇÃO ---
if __name__ == "__main__":
    token = get_spotify_token(CLIENT_ID, CLIENT_SECRET)
    if not token:
        exit("Token inválido. Verifique suas credenciais.")

    # se quiser adicionar mais albuns mockados com dados da API, só mudar essa lista aqui
    albums_to_search = [
        ("Tame Impala", "Currents"),
        ("Norah Jones", "Come Away With Me"),
        ("Kings of Leon", "Only by the Night"),
        ("Fleetwood Mac", "Rumours"),
        ("Green Day", "American Idiot"),
        ("John Mayer", "Continuum"),
        ("Amy Winehouse", "Back to Black"),
        ("Radiohead", "In Rainbows"),
        ("Pearl Jam", "Ten"),
        ("Red Hot Chili Peppers", "Californication")
    ]

    sql_lines = []
    for artist, album in albums_to_search:
        album_id = search_album_id(token, artist, album)
        if not album_id:
            continue

        album_data = get_album_data(token, album_id)
        if album_data:
            sql_lines.append(album_to_sql(album_data))
        sleep(0.3)

    with open("albums_from_ids.sql", "w") as f:
        f.write("-- Dados dos álbuns selecionados\n")
        f.write("\n".join(sql_lines))
