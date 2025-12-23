# 🏰 Clan System - Plugin Minecraft/Spigot

> **Plugin inicial de sistema de clans para Minecraft/Spigot com comentários divertidos e educacionais!** 🎮

[![Minecraft](https://img.shields.io/badge/Minecraft-1.8.8-green.svg)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## 📖 Sobre o Projeto

Este é um **plugin educacional** desenvolvido para servir como **ponto de partida** para desenvolvedores que querem aprender a criar plugins para Minecraft/Spigot. 

### 🎯 Por que este projeto é diferente?

- ✅ **Comentários divertidos e humanizados** em todo o código
- ✅ **Arquitetura limpa e organizada** (fácil de entender)
- ✅ **Código comentado** explicando cada decisão técnica
- ✅ **Pronto para modificar** - use como base para seus próprios projetos
- ✅ **Exemplos práticos** de GUI, comandos, listeners e persistência

---

## ⚡ Características

### 🎮 Sistema de Clans Completo
- **Criação e gerenciamento** de clans
- **Sistema de hierarquia** (Dono → Oficiais → Membros)
- **Convites** com expiração automática
- **KDR (Kill/Death Ratio)** tracking
- **Sistema de banco** (preparado para economia)

### ⚔️ Sistema de Guerras
- **Declaração de guerra** entre clans
- **Duração configurável** (padrão: 10 minutos)
- **Placar de kills** em tempo real
- **Sistema de aceitação** de desafios
- **Anúncios automáticos** de início e fim

### 🎨 Interface Gráfica (GUI)
- **Menu principal** interativo
- **Lista de membros** com paginação
- **Configurações** com toggle visual
- **Menu de guerra** com informações
- **Design moderno** com cores e animações

### 🔥 Toggle de Fogo Amigo
- **Ativação/desativação** via GUI ou comando
- **Visual interativo** (bloco verde/vermelho)
- **Proteção automática** entre membros do mesmo clan
- **Apenas o dono** pode alterar

### 💾 Persistência de Dados
- **Salvamento automático** em YAML
- **Carregamento** ao iniciar o servidor
- **Backup** de dados dos clans

---

## 📦 Instalação

### Requisitos
- **Minecraft/Spigot** 1.8.8 ou superior
- **Java** 21 ou superior
- **Maven** 3.9+ (para compilar)

### Passos

1. **Clone o repositório:**
```bash
git clone https://github.com/MarcosFG-Dev/clan-system.git
cd clan-system
```

2. **Compile o plugin:**
```bash
mvn clean package
```

3. **Copie o JAR para sua pasta de plugins:**
```bash
cp target/Clan-1.0.0-SNAPSHOT.jar /caminho/para/servidor/plugins/
```

4. **Reinicie ou recarregue o servidor:**
```
reload
```

---

## 🕹️ Como Usar

### Comandos Disponíveis

| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/clan` | Abre o menu principal | Todos |
| `/clan criar <tag> <nome>` | Cria um novo clan | Todos |
| `/clan convidar <player>` | Convida um jogador | Líder/Oficial |
| `/clan aceitar <tag>` | Aceita um convite | Todos |
| `/clan sair` | Sai do clan atual | Membros |
| `/clan expulsar <player>` | Expulsa um membro | Líder/Oficial |
| `/clan ff` | Alterna fogo amigo | Dono |
| `/clan info [tag]` | Mostra informações | Todos |
| `/clan guerra <tag>` | Declara guerra | Dono |
| `/clan aceitar_guerra <tag>` | Aceita uma guerra | Dono |
| `/clan remover` | Deleta o clan | Dono |

### Usando a GUI

1. **Abra o menu:** `/clan`
2. **Navegue pelos menus** clicando nos itens
3. **Configure seu clan** no menu de configurações
4. **Declare guerras** pelo menu de guerra

---

## 🏗️ Estrutura do Projeto

```
src/main/java/com/clan/
├── Clan.java                    # Classe principal do plugin
├── comandos/
│   └── clanCommand.java         # Handler de comandos
├── gui/
│   └── ClanGUI.java            # Sistema de menus interativos
├── listeners/
│   ├── ClanInventoryListener.java  # Eventos de cliques na GUI
│   └── ClanPlayerListener.java     # Eventos de jogadores
├── managers/
│   └── ClanManager.java        # Gerenciador central de clans
├── model/
│   ├── ClanData.java           # Modelo de dados do clan
│   └── ClanWar.java            # Modelo de guerra entre clans
└── utils/
    ├── ColorUtil.java          # Utilitário de cores
    └── MessageManager.java     # Gerenciador de mensagens
```

---

## 🎓 Aprenda com o Código

### 💬 Comentários Divertidos

Este projeto tem comentários meio duvidosos mas do meu jeitinho ta facil de entender:

```java
// Toggle de fogo amigo - a estrela do show caralho
boolean ffStatus = clan.isFriendlyFire();

// Só o dono muda essa porra
if (!clan.getOwner().equals(player.getUniqueId())) {
    player.sendMessage("Eita! Só o dono pode mudar isso aí, parça.");
    return;
}

// Ué, cadê o clan? Voltou pra casa?
if (clan == null) return;
```

### 📚 O Que Você Vai Aprender

- ✅ **Estruturar um plugin** do zero
- ✅ **Criar comandos** customizados
- ✅ **Desenvolver GUIs** interativas
- ✅ **Gerenciar eventos** (listeners)
- ✅ **Persistir dados** em YAML
- ✅ **Trabalhar com cores** no Minecraft
- ✅ **Sistema de permissões** e hierarquia
- ✅ **Boas práticas** de desenvolvimento

---

## ⚙️ Configuração

O arquivo `config.yml` permite customizar o plugin:

```yaml
settings:
  max-tag-length: 5          # Tamanho máximo da tag
  max-name-length: 20        # Tamanho máximo do nome
  max-members: 50            # Membros máximos por clan
  creation-cost: 0           # Custo para criar (requer Vault)
  default-friendly-fire: false  # Fogo amigo padrão

wars:
  default-duration: 10       # Duração das guerras (minutos)
  invite-timeout: 60         # Timeout dos convites (segundos)
  broadcast-wars: true       # Anunciar guerras globalmente

messages:
  prefix: '&8[&6Clan&8]&r '  # Prefixo das mensagens
  # ... mais mensagens customizáveis
```

---

## 🛠️ Tecnologias Utilizadas

- **Java 21** - Linguagem de programação
- **Spigot API 1.8.8** - API do Minecraft
- **Maven** - Gerenciamento de dependências
- **YAML** - Persistência de dados

---

## 🤝 Contribuindo

Contribuições são **super bem-vindas**! Este é um projeto educacional, então:

1. **Fork** o projeto
2. Crie uma **branch** para sua feature (`git checkout -b feature/MinhaFeature`)
3. **Commit** suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. **Push** para a branch (`git push origin feature/MinhaFeature`)
5. Abra um **Pull Request**

### 💡 Ideias para Contribuir

- [ ] Sistema de níveis de clans
- [ ] Integração com economia (Vault)
- [ ] Sistema de aliados
- [ ] Território de clans
- [ ] Conquistas e recompensas
- [ ] Sistema de chat privado do clan
- [ ] API para outros plugins
- [ ] Suporte multi-idioma


## 👨‍💻 Autor

Desenvolvido com ❤️ e muito ☕ por **Marcos**

---

## 🙏 Agradecimentos

- **Spigot** pela API incrível
- **Comunidade Minecraft** por ser foda
- Todos os **desenvolvedores iniciantes** - este projeto é para vocês! 🚀

---

## 📞 Suporte

Encontrou um bug? Tem uma sugestão? 

- 🐛 Abra uma [Issue](https://github.com/seu-usuario/clan-system/issues)
- 💬 Inicie uma [Discussion](https://github.com/seu-usuario/clan-system/discussions)
- ⭐ Deixe uma estrela se o projeto te ajudou!

---

<div align="center">

**⭐ Se este projeto te ajudou, considere dar uma estrela! ⭐**

Made with 💜 for the Minecraft dev community

</div>



