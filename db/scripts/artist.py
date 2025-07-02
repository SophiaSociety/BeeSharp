import requests
import base64
import random

# --- SPOTIFY CREDENTIALS ---
CLIENT_ID = "SEU_CLIENT_ID"
CLIENT_SECRET = "SEU_CLIENT_SECRET"

# --- Lista de artistas ---
artists = [
    "Pink Floyd", "The Beatles", "Michael Jackson", "AC/DC", "Fleetwood Mac",
    "Eagles", "Led Zeppelin", "Nirvana", "Queen", "Radiohead",
    "David Bowie", "U2", "The Rolling Stones", "Bob Dylan", "The Who",
    "Metallica", "Red Hot Chili Peppers", "The Doors", "Arctic Monkeys", "Oasis"
]

# --- Biografias fictícias ---
bios = [
    "Um dos artistas mais influentes da música moderna.",
    "Reconhecido mundialmente por seu som único e inovador.",
    "Lendário(a) por álbuns que marcaram gerações.",
    "Revolucionou o cenário musical com sua criatividade.",
    "Dono(a) de um repertório icônico e aclamado pela crítica.",
    "Figura central na história do rock e da música pop.",
    "Influência fundamental no desenvolvimento da música contemporânea.",
    "Responsável por alguns dos maiores clássicos da música.",
    "Adorado(a) por fãs ao redor do mundo.",
    "Protagonista de momentos inesquecíveis na história da música."
]

# --- Autenticação ---
def get_token(client_id, client_secret):
    auth = base64.b64encode(f"{client_id}:{client_secret}".encode()).decode()
    headers = {"Authorization": f"Basic {auth}"}
    data = {"grant_type": "client_credentials"}
    r = requests.post("https://accounts.spotify.com/api/token", headers=headers, data=data)
    return r.json().get("access_token") if r.status_code == 200 else None

# --- Pega imagem do artista ---
def get_artist_image(token, name):
    headers = {"Authorization": f"Bearer {token}"}
    params = {"q": name, "type": "artist", "limit": 1}
    r = requests.get("https://api.spotify.com/v1/search", headers=headers, params=params)
    items = r.json().get("artists", {}).get("items", [])
    return items[0]["images"][0]["url"] if items and items[0].get("images") else None

# --- Escapa strings para SQL ---
def esc_sql(s):
    return s.replace("'", "''") if s else ""

# --- Execução ---
token = get_token(CLIENT_ID, CLIENT_SECRET)
if not token:
    exit("Erro ao autenticar com Spotify.")

sql_lines = []
for name in artists:
    image_url = get_artist_image(token, name) or ""
    descricao = random.choice(bios)
    sql = (
        "INSERT INTO Artists (name, descricao, image) "
        f"VALUES ('{esc_sql(name)}', '{esc_sql(descricao)}', '{esc_sql(image_url)}');"
    )
    sql_lines.append(sql)

# --- Salva em arquivo ---
with open("insert_artists.sql", "w") as f:
    f.write("-- Inserts para tabela Artists com imagem e descrição\n")
    f.write("\n".join(sql_lines))

print("Arquivo 'insert_artists.sql' gerado com sucesso.")
