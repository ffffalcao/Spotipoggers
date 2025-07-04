# Spotipoggers - Clone do Spotify em Java

Um player de música simples inspirado no Spotify, desenvolvido em Java com interface gráfica Swing.

## Funcionalidades

- ✅ **Reprodução de múltiplos formatos**: MP3, WAV, FLAC, OGG
- ✅ **Detecção automática de formato**: O sistema identifica automaticamente o tipo de arquivo
- ✅ **Carregamento automático de músicas**: Escaneia uma pasta e carrega todas as músicas
- ✅ **Interface gráfica**: Lista de músicas, botão play/pause, barra de progresso
- ✅ **Capa do álbum**: Exibe imagem da capa (quando disponível)

## Como usar

### 1. Configurar a pasta de músicas

No arquivo `src/passatempo/TelaSpotipoggers.java`, linha 42, altere o caminho:

```java
String pastaMusicas = "C:/caminho/para/sua/pasta/musicas"; // Ajuste este caminho
```

Exemplo:
```java
String pastaMusicas = "C:/Users/SeuUsuario/Music";
```

### 2. Executar o projeto

#### Via NetBeans:
1. Abra o projeto no NetBeans
2. Clique com botão direito no projeto
3. Selecione "Run"

#### Via linha de comando:
```bash
# Compilar
javac -cp "lib/*;src" src/passatempo/*.java

# Executar
java -cp "lib/*;src" passatempo.Passatempo
```

#### Via script (recomendado):
```bash
.\executar_simples.bat
```

### 3. Usar a interface

1. **Selecionar música**: Clique em uma música na lista
2. **Tocar/Pausar**: Clique no botão "Tocar" ou "Pausar"
3. **Barra de progresso**: Mostra o progresso da música (funciona para WAV, FLAC, OGG)

## Estrutura do projeto

```
Spotipoggers/
├── src/passatempo/
│   ├── Passatempo.java          # Classe principal
│   ├── TelaSpotipoggers.java    # Interface gráfica
│   ├── Musica.java              # Modelo de dados
│   ├── PlayerUniversal.java     # Player universal (detecta formato)
│   ├── PlayerMp3.java           # Player para MP3
│   ├── PlayerWav.java           # Player para WAV
│   ├── PlayerFlac.java          # Player para FLAC/OGG
│   └── CarregadorMusicas.java   # Carrega músicas da pasta
├── lib/
│   ├── jl1.0.1.jar              # Biblioteca JLayer (MP3)
│   ├── vlcj-5.0.0-20250206.193047-46.jar  # Biblioteca VLCJ
│   ├── vlcj-natives-5.0.0-20250207.090925-54.jar  # Nativas VLCJ
│   ├── jna-5.13.0.jar           # Biblioteca JNA
│   ├── jna-platform-5.13.0.jar  # Plataforma JNA
│   ├── slf4j-api-1.7.36.jar     # Logging API
│   └── slf4j-simple-1.7.36.jar  # Logging Simple
├── executar_simples.bat         # Script para executar
└── README.md                    # Documentação
```

## Dependências

- **JLayer**: Para reprodução de arquivos MP3
- **VLCJ**: Para reprodução de arquivos FLAC e OGG
- **Java Sound API**: Para reprodução de arquivos WAV (nativo)

## Formatos suportados

| Formato | Player | Barra de Progresso | Status |
|---------|--------|-------------------|--------|
| MP3     | JLayer | Aproximada | ✅ Funcionando |
| WAV     | Java Sound | Precisa | ✅ Funcionando |
| FLAC    | Java Sound | Precisa | ⚠️ Suporte limitado |
| OGG     | Java Sound | Precisa | ⚠️ Suporte limitado |

## Melhorias futuras

- [ ] Extração de metadados (artista, álbum, ano)
- [ ] Controles de volume
- [ ] Botões próximo/anterior
- [ ] Playlist personalizada
- [ ] Equalizador
- [ ] Modo aleatório/repetir

## Solução de problemas

### Erro "Pasta não encontrada"
- Verifique se o caminho da pasta está correto
- Use barras normais (/) ou barras invertidas (\\) no Windows

### Erro de biblioteca
- Certifique-se de que os arquivos .jar estão na pasta `lib/`
- Verifique se o classpath está configurado corretamente

### Erro de dependências
- Certifique-se de que todos os arquivos .jar estão na pasta `lib/`
- Verifique se o classpath está configurado corretamente
- Para problemas com VLCJ, o sistema funciona com MP3 e WAV

### Música não toca
- Verifique se o arquivo existe no caminho especificado
- Certifique-se de que o formato é suportado (MP3, WAV, FLAC, OGG)

## Desenvolvimento

Este projeto foi desenvolvido para fins educacionais, demonstrando:
- Programação orientada a objetos em Java
- Interface gráfica com Swing
- Manipulação de arquivos de áudio
- Detecção automática de formatos
- Carregamento dinâmico de conteúdo 