# 🐝🎵 BeeSharp   

**Um diário musical para registrar e compartilhar suas experiências com álbuns.**  
## Projeto
BeeSharp é uma aplicação WEB realizada durante a disciplina MAC0350 - Introdução ao Desenvolvimento de Sistemas de Software do Instituto de Matemática e Estatística da Universidade de São Paulo.

## Visão Geral  
Hoje, ainda não existe um espaço dedicado para registrar, avaliar e compartilhar experiências em relação a álbuns de música. Então, inspirado na plataforma Letterboxd, um  diário social de filmes, surge o BeeSharp, um diário social e digital de músicas.

“BeeSharp” faz um trocadilho com B#, si sustenido, em inglês, B sharp. Substituímos “B” por “Bee” pela semelhança sonora, dando origem ao Beebo, nosso mascote.

Nosso objetivo é oferecer um diário de música interativo e personalizável; permitir avaliações, resenhas e engajamento social em torno de álbuns; além de criar uma base de recomendações inteligentes com base nas reviews do seus amigos e mais ouvidos!

## Tecnologias  
- **Frontend:** Svelte
- **Backend:** Ktor 
- **Banco de Dados:** PostgreSQL
- **Containerização:** Docker e Docker Compose
- **API de Albums:** Integração com a API do Spotfy para obter informações detalhadas sobre os albums, para popular nosso banco de dados.

## Funcionalidades Principais  
- Cadastro e login de usuários com autenticação JWT
- Busca de albums, artistas e usuários 
- Sistema de avaliação de (0 a 5 estrelas) e com resenhas  
- Interação social, como seguir usuários e curtir resenhas
- Favoritar albums
- Algoritmo de recomendação baseadas nas reviews do seus amigos e mais ouvidos
 
## Dependências 

- Docker e Docker Compose
- JDK 17+
- Node.js + npm
## Como Executar

Antes de começar, clone o projeto em um diretório local. Então, execute, para inicializar o banco de dados:

```bash
docker-compose up -d
```
Em seguida, entre na pasta /backend e execute:

```bash
./gradlew build
./gradlew run
```

E por fim, na pasta /frontend:
```bash
npm install
npm run dev
```

## Estrutura do Projeto  
```
BeeSharp/
├── backend/             # Backend em Kotlin com Ktor
│ ├── models/            # Entidades do banco de dados
│ ├── repository/        # Acesso e manipulação de dados
│ ├── Routing.kt         # Rotas e endpoints
│ ├── Security.kt        # Autenticação JWT
│ └── test/              # Testes automatizados
│
├── frontend/            # Aplicação web com Svelte + Vite
│
├── db/                  # Scripts SQL e dados mock
│
└── docker-compose.yml 

``` 

## Uso de Inteligência Artificial
Ferramentas como ChatGPT foram utilizadas como assistentes técnicos ao longo do desenvolvimento do BeeSharp. Elas ajudaram a esclarecer dúvidas sobre arquitetura e padrões de software, sugerir e revisar trechos de código backend e frontend e apoiar na criação de testes automatizados.

---  
**🎶 "A vida sem música seria um erro."** — Nietzsche  
