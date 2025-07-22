# Sistema Distribuído de IA - Microserviços Spring Boot

## Visão Geral
Este é um sistema abrangente de microserviços distribuídos construído com **Spring Boot** e **Spring Cloud** que integra capacidades de IA. O sistema segue a metodologia do **Manifesto Twelve-Factor** e implementa **padrões de resiliência** para tolerância a falhas e **observabilidade** abrangente.

## Pré-requisitos
- **Java 17+** (JDK)
- **Maven 3.6+**
- **Docker & Docker Compose**
- **Apache JMeter** (para testes de carga)
- **Chave da API OpenAI** (configurada no arquivo `.env`)

## Tecnologias Utilizadas

### Framework Principal
- **Spring Boot 3.2.1** para desenvolvimento de microserviços
- **Spring Cloud 2023.0.0** para arquitetura distribuída
- **Spring AI 1.0.0-M4** para integração com modelos de IA
- **Netflix Eureka** para descoberta de serviços
- **Spring Cloud Gateway** para roteamento e balanceamento
- **Resilience4j** para padrões de resiliência

### Ferramentas de Desenvolvimento
- **Spring Boot DevTools** para recarga automática durante desenvolvimento
- **Maven** para gerenciamento de dependências e build
- **Docker Compose** para orquestração de containers de observabilidade
- **Spring Cloud Config** para configuração centralizada

## Componentes da Arquitetura

### Serviços Centrais
- **Eureka Server** (Porta 8761): Descoberta e registro de serviços
- **Config Server** (Porta 8888): Gerenciamento centralizado de configuração
- **API Gateway** (Porta 8080): Ponto de entrada com roteamento e circuit breakers
- **MCP Server** (Porta 3000): Servidor MCP implementado com Spring Boot

### Microserviços
1. **Microservice MCP** (Porta 8081): Interface com o MCP Server usando padrões de resiliência
2. **Microservice AI** (Porta 8082): Integração com API OpenAI usando Spring AI
3. **Microservice Serverless** (Porta 8083): Implementa funções serverless para transformação de dados

### Stack de Observabilidade (Docker)
- **Prometheus** (Porta 9090): Coleta de métricas
- **Grafana** (Porta 3001): Visualização de métricas e dashboards (admin/admin123)
- **Zipkin** (Porta 9411): Rastreamento distribuído

## Configuração e Execução

### Passo 1: Configuração da Chave OpenAI
```bash
# Copie o arquivo de exemplo (se não existir)
cp .env.example .env

# Edite o arquivo .env e adicione sua chave OpenAI
OPENAI_API_KEY=sua-chave-openai-aqui
```
⚠️ **IMPORTANTE:** O serviço AI não funcionará sem uma chave OpenAI válida no arquivo `.env`

### Passo 2: Compilação do Projeto
```bash
# Compilar todos os microserviços
mvn clean install -DskipTests
```
📝 **Nota:** Pode levar alguns minutos na primeira execução para baixar dependências

### Passo 3: Iniciar Infraestrutura de Observabilidade (Opcional)
```bash
# Iniciar Prometheus, Grafana e Zipkin
docker-compose up -d
```
📊 **Observabilidade:** Este passo é opcional mas recomendado para monitoramento completo

### Passo 4: Iniciar Microserviços

#### 🚀 Opção A: Startup Automático (Recomendado)
```bash
# Script que inicia todos os serviços automaticamente
./scripts/start-services.sh
```
⏱️ **Tempo:** ~3-5 minutos para todos os serviços estarem funcionais

#### 🔧 Opção B: Startup Manual (Para Desenvolvimento)
**IMPORTANTE: Iniciar na ordem exata listada abaixo**

```bash
# Terminal 1 - Eureka Server (Service Discovery) - DEVE SER O PRIMEIRO
cd eureka-server
mvn spring-boot:run
# ✅ Aguardar: http://localhost:8761 acessível

# Terminal 2 - Config Server (Centralized Configuration) - SEGUNDO
cd config-server
mvn spring-boot:run
# ✅ Aguardar: http://localhost:8888/actuator/health retorna UP

# Terminal 3 - MCP Server (Model Context Protocol Server)
cd mcp-server
mvn spring-boot:run
# ✅ Aguardar: http://localhost:3000/health retorna UP

# Terminal 4 - API Gateway (Entry Point)
cd api-gateway
mvn spring-boot:run
# ✅ Aguardar: http://localhost:8080/actuator/health retorna UP

# Terminal 5 - MCP Service (Interface with MCP Server)
cd microservice-mcp
mvn spring-boot:run
# ✅ Aguardar: http://localhost:8081/mcp/health retorna SUCCESS

# Terminal 6 - AI Service (com variáveis de ambiente) - REQUER .env
cd /home/jorgelino/TrabalhoProgDist-3
export $(cat .env | grep -v ^# | xargs)
cd microservice-ai
mvn spring-boot:run -Dspring-boot.run.profiles=local
# ✅ Aguardar: http://localhost:8082/ai/health retorna SUCCESS

# Terminal 7 - Serverless Service (Functions)
cd microservice-serverless
mvn spring-boot:run
# ✅ Aguardar: http://localhost:8083/actuator/health retorna UP
```

#### ⚡ Dicas para Startup Manual:
- **Ordem Crítica:** Eureka → Config Server → Demais Serviços
- **Aguarde cada serviço:** Não inicie o próximo até o anterior estar UP
- **Tempo entre serviços:** ~30-60 segundos entre cada startup
- **Variáveis de ambiente:** Apenas o AI Service precisa do `.env`

### Passo 5: Verificação do Sistema

#### 📋 **Checklist de Startup (Manual)**
Para startup manual, siga esta ordem e aguarde cada passo:

1. ✅ **Eureka Server** - http://localhost:8761 (deve carregar a página)
2. ✅ **Config Server** - http://localhost:8888/actuator/health retorna `{"status":"UP"}`
3. ✅ **MCP Server** - http://localhost:3000/health retorna `{"status":"UP"}`
4. ✅ **API Gateway** - http://localhost:8080/actuator/health retorna `{"status":"UP"}`
5. ✅ **MCP Service** - http://localhost:8081/mcp/health retorna `{"status":"SUCCESS"}`
6. ✅ **AI Service** - http://localhost:8082/ai/health retorna `{"status":"SUCCESS"}`
7. ✅ **Serverless Service** - http://localhost:8083/actuator/health retorna `{"status":"UP"}`

#### 🔍 **Verificação Final**
Após todos os serviços iniciarem:
- **Eureka Dashboard**: http://localhost:8761 (deve mostrar 6 serviços registrados)
- **Teste funcional**: Executar o comando de teste completo (veja seção Troubleshooting)

#### ⏱️ **Tempos Esperados**
- **Startup automático**: 3-5 minutos total
- **Startup manual**: 5-8 minutos (aguardando entre serviços)
- **Primeiro startup**: +2-3 minutos (download de dependências Maven)

## Endpoints da API

### Acesso Direto aos Microserviços

#### Serviço AI (Porta 8082)
- `GET /ai/generate/{topico}` - Gerar texto sobre um tópico
- `POST /ai/prompt` - Processar prompt personalizado de IA
- `GET /ai/health` - Health check do serviço AI

#### Serviço MCP (Porta 8081)
- `POST /mcp/execute` - Executar comando MCP
- `GET /mcp/status` - Obter status do servidor MCP
- `GET /mcp/health` - Health check do serviço MCP

#### Serviço Serverless (Porta 8083)
- `POST /validate` - Executar função de transformação de dados
- `GET /actuator/health` - Health check do serviço Serverless

**Exemplo de uso do /validate:**
```bash
curl -X POST http://localhost:8083/validate \
  -H "Content-Type: application/json" \
  -d '{
    "data": "hello world",
    "transformation_type": "uppercase",
    "parameters": {}
  }'
```

**Tipos de transformação suportados:**
- `uppercase` - Converter texto para maiúsculas
- `lowercase` - Converter texto para minúsculas  
- `reverse` - Reverter ordem dos elementos
- `sort` - Ordenar elementos
- `filter` - Filtrar elementos
- `aggregate` - Agregar dados

#### API Gateway (Porta 8080)
- `GET /actuator/health` - Health check do Gateway
- Roteamento para microserviços via `/api/{service-name}/*`

## Dashboards de Monitoramento

| **Serviço** | **URL** | **Credenciais** | **Função** |
|-------------|---------|-----------------|------------|
| **Eureka Dashboard** | http://localhost:8761 | - | Descoberta de serviços |
| **Prometheus** | http://localhost:9090 | - | Métricas do sistema |
| **Grafana** | http://localhost:3001 | admin/admin123 | Dashboards e visualização |
| **Zipkin** | http://localhost:9411 | - | Rastreamento distribuído |

## Testes de Carga com JMeter

### Configuração JMeter
O projeto inclui um plano de teste JMeter completo em `jmeter/sistema-microservicos-teste-carga.jmx`

#### Cenários de Teste Configurados:
1. **Teste de Carga Normal** (habilitado):
   - 50 usuários simultâneos
   - 10 loops por usuário
   - Testa todos os microserviços diretamente

2. **Teste de Stress** (desabilitado por padrão):
   - 200 usuários simultâneos
   - 20 loops por usuário

#### Endpoints Testados:
- **AI Service**: `localhost:8082/ai/generate/tecnologia`
- **MCP Service**: `localhost:8081/mcp/status`
- **Serverless Service**: `localhost:8083/validate`
- **Gateway Health**: `localhost:8080/actuator/health`
- **AI Custom Prompt**: `localhost:8082/ai/prompt`

### Executando Testes JMeter

#### Interface Gráfica (Recomendado para desenvolvimento):
```bash
# Instalar JMeter (se necessário)
sudo apt install jmeter

# Abrir GUI do JMeter com o plano de teste
jmeter -t jmeter/sistema-microservicos-teste-carga.jmx

# OU abrir JMeter e carregar o arquivo manualmente
jmeter
```

**Instruções no JMeter GUI:**
1. O plano de teste será carregado automaticamente
2. Verifique se o "Teste de Carga Normal" está habilitado
3. Clique no botão "Start" (▶️) para executar
4. Visualize resultados em tempo real nos listeners configurados
5. Para modificar cenários, habilite/desabilite os Thread Groups

#### Linha de Comando (Para CI/CD):
```bash
# Executar teste sem interface
jmeter -n -t jmeter/sistema-microservicos-teste-carga.jmx -l results.jtl

# Gerar relatório HTML
jmeter -g results.jtl -o html-report/
```

## Padrões de Resiliência Implementados

### Circuit Breaker
- Implementado usando Resilience4j em todos os microserviços
- Previne falhas em cascata com mecanismos de fallback
- Configurado com limites de falha e timeouts de recuperação

### Padrão Retry
- Retry automático com backoff exponencial
- Tentativas configuráveis e durações de espera
- Aplicado a chamadas de serviços externos

### Padrão Timeout
- Timeouts de requisição configurados para todas as chamadas
- Previne esgotamento de recursos por requisições pendentes

### Padrão Bulkhead
- Isolamento de serviços através de thread pools separadas
- Isolamento de recursos entre diferentes operações

## Funcionalidades de Observabilidade

- **Métricas em tempo real**: Via Prometheus/Grafana
- **Logs centralizados**: Spring Boot Logging
- **Rastreamento distribuído**: Zipkin para requisições entre serviços
- **Health Checks**: Endpoints de saúde em todos os serviços
- **Service Discovery**: Monitoramento via Eureka Dashboard

## Integração com IA

### OpenAI Integration
- **Modelo**: GPT-3.5-turbo
- **Configuração**: Via Spring AI
- **Fallback**: Respostas de fallback quando API indisponível
- **Circuit Breaker**: Proteção contra falhas da API OpenAI

### Endpoints de IA:
```bash
# Geração de texto por tópico
curl "http://localhost:8082/ai/generate/microservicos"

# Prompt personalizado
curl -X POST "http://localhost:8082/ai/prompt" \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Explique Spring Boot"}'
```

## Arquitetura Twelve-Factor

O sistema implementa todos os 12 fatores:
1. **Codebase**: Código único versionado
2. **Dependencies**: Dependências explícitas via Maven
3. **Config**: Configuração via ambiente (.env, Config Server)
4. **Backing services**: Serviços externos como recursos anexados
5. **Build, release, run**: Separação clara de estágios
6. **Processes**: Processos stateless
7. **Port binding**: Serviços exportam via HTTP
8. **Concurrency**: Escalonamento por processos
9. **Disposability**: Inicialização rápida e shutdown gracioso
10. **Dev/prod parity**: Ambientes similares
11. **Logs**: Logs como streams de eventos
12. **Admin processes**: Processos administrativos isolados

## Troubleshooting

### Problemas de Startup Comuns:

#### 🔴 **Erro: "Port already in use"**
```bash
# Verificar quais portas estão ocupadas
netstat -tlnp | grep -E "(8080|8081|8082|8083|8761|8888|3000)"

# Parar processos Java que podem estar rodando
pkill -f "spring-boot:run"
```

#### 🔴 **Erro: "Connection refused" ou serviços não se registram**
- **Causa:** Eureka Server não iniciou primeiro
- **Solução:** Sempre iniciar Eureka Server primeiro e aguardar estar UP

#### 🔴 **Erro: AI Service retorna fallback responses**
```bash
# Verificar se .env existe e tem chave OpenAI
cat .env | grep OPENAI_API_KEY

# Verificar se variáveis foram carregadas
echo $OPENAI_API_KEY
```

#### 🔴 **Erro: "Config Server not available"**
- **Causa:** Config Server não iniciou antes dos outros serviços
- **Solução:** Iniciar Config Server em segundo lugar (após Eureka)

### Problemas Funcionais:

#### 🟡 **Serviços registrados mas não respondem**
```bash
# Verificar se todas as portas estão respondendo
curl -f http://localhost:8761/actuator/health  # Eureka
curl -f http://localhost:8888/actuator/health  # Config Server
curl -f http://localhost:8080/actuator/health  # API Gateway
curl -f http://localhost:3000/health           # MCP Server
curl -f http://localhost:8081/mcp/health       # MCP Service
curl -f http://localhost:8082/ai/health        # AI Service
curl -f http://localhost:8083/actuator/health  # Serverless Service
```

#### 🟡 **JMeter testes falhando**
- **Verificar:** Todos os serviços estão UP antes de executar testes
- **Aguardar:** 2-3 minutos após último serviço iniciar

### Comandos de Diagnóstico:

#### ✅ **Verificação Rápida do Sistema Completo**

### Verificação do Sistema:
```bash
# Verificar serviços registrados no Eureka
curl http://localhost:8761

# Testar endpoints dos microserviços diretamente
curl http://localhost:8081/mcp/status
curl http://localhost:8082/ai/health
curl http://localhost:8083/actuator/health
curl http://localhost:8080/actuator/health

# Teste rápido da funcionalidade de IA
curl "http://localhost:8082/ai/generate/teste"

# Teste completo de saúde de todos os serviços (one-liner)
echo "=== TESTING ALL SERVICES ===" && \
echo "1. Eureka:" && curl -s http://localhost:8761/actuator/health | grep -o '"status":"[^"]*"' && \
echo "2. Config Server:" && curl -s http://localhost:8888/actuator/health | grep -o '"status":"[^"]*"' && \
echo "3. API Gateway:" && curl -s http://localhost:8080/actuator/health | grep -o '"status":"[^"]*"' && \
echo "4. MCP Server:" && curl -s http://localhost:3000/health | grep -o '"status":"[^"]*"' && \
echo "5. Microservice MCP:" && curl -s http://localhost:8081/mcp/health | grep -o '"status":"[^"]*"' && \
echo "6. Microservice AI:" && curl -s http://localhost:8082/ai/health | grep -o '"status":"[^"]*"' && \
echo "7. Microservice Serverless:" && curl -s http://localhost:8083/actuator/health | grep -o '"status":"[^"]*"'
```

### Logs dos Serviços:
Cada microserviço gera logs no console. Para troubleshooting:
- Verifique se todos os serviços se registraram no Eureka
- Confirme se o Config Server está fornecendo configurações
- Monitore logs de erro relacionados à API OpenAI

Este sistema demonstra uma arquitetura de microserviços completa, pronta para produção, com **integração de IA**, **padrões de resiliência**, **observabilidade abrangente** e **testes de carga automatizados**.