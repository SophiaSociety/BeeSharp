# BeeSharp 🎵📝  

**Um diário musical para registrar e compartilhar suas experiências com álbuns.**  

## 📌 Visão Geral  
O **BeeSharp** é uma plataforma onde você pode:  
- 📖 **Registrar** os álbuns que ouviu  
- ⭐ **Avaliar** e resenhar seus favoritos  
- 📊 **Criar listas** personalizadas (Top 10 do ano, melhores do gênero X, etc.)  
- 👥 **Seguir amigos** e descobrir novas recomendações  

## 🛠️ Tecnologias  
- **Frontend:** Svelte
- **Backend:** Ktor 
- **Banco de Dados:** PostgreSQL

## 🚀 Funcionalidades Planejadas (MVP)  
✅ **Cadastro/login de usuários**  
✅ **Busca e adição de álbuns** (via integração com API do Spotify/Last.fm)  
✅ **Sistema de avaliação** (0 a 5 estrelas) e resenhas  
✅ **Timeline pessoal** com histórico de escutas  
🔜 **Listas customizáveis** (WIP)
🔜 **Interação social** (seguir usuários, curtir resenhas)  

## 🌟 Diferenciais  
- 📈 **Estatísticas pessoais** (gráficos de gêneros mais ouvidos, evolução de notas ao longo do tempo)  
- 🎨 **Design minimalista** focado na experiência musical  

## 📂 Estrutura do Projeto  
```
musiclog/  
├── frontend/          # Aplicação Svelte  
├── backend/           # Servidor Ktor  

```

## 🤝 Como Contribuir  
1. Faça um fork do projeto  
2. Crie uma branch (`git checkout -b feature/nova-funcionalidade`)  
3. Commit suas mudanças (`git commit -m 'Adiciona X'`)  
4. Push para a branch (`git push origin feature/nova-funcionalidade`)  
5. Abra um Pull Request  

## 📄 Licença  
MIT (mais detalhes a definir)  

---  
**🎶 "A vida sem música seria um erro."** — Nietzsche  

*Em desenvolvimento por [seu nome/equipe]. Previsão de lançamento: Q4 2024.*  

---  

### ✨ Preview (Futura Homepage)  
```html  
<div class="hero">  
  <h1>O que você está ouvindo hoje?</h1>  
  <SearchBar placeholder="Busque álbuns, artistas ou usuários..."/>  
  <TrendingAlbumsCarousel/>  
</div>  
```  

---  
*Dados de álbuns fornecidos por [Spotify API]. Ícones por [FontAwesome].*  

---  

🔗 **Links Úteis:**  
- [Documentação da API](https://...) *(em construção)*  
- [Roadmap](https://...) *(em construção)*  

---  

**PS:** Este README será atualizado conforme o projeto evolui. Sugestões são bem-vindas!