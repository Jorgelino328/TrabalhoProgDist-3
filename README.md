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

### Microserviços
1. **Microservice MCP** (Porta 8081): Interface com servidores MCP externos
2. **Microservice AI** (Porta 8082): Integração com API OpenAI usando Spring AI
3. **Microservice Serverless** (Porta 8083): Implementa funções serverless para transformação de dados

### Stack de Observabilidade (Docker)
- **Prometheus** (Porta 9090): Coleta de métricas
- **Grafana** (Porta 3001): Visualização de métricas e dashboards (admin/admin123)
- **Zipkin** (Porta 9411): Rastreamento distribuído

## Configuração e Execução

### 1. Configuração da Chave OpenAI
```bash
# Copie o arquivo de exemplo
cp .env.example .env

# Edite o arquivo .env e adicione sua chave OpenAI
OPENAI_API_KEY=sua-chave-openai-aqui
```

### 2. Compilação do Projeto
```bash
# Compilar todos os microserviços
mvn clean install -DskipTests
```

### 3. Iniciar Infraestrutura de Observabilidade
```bash
# Iniciar Prometheus, Grafana e Zipkin
docker-compose up -d
```

### 4. Iniciar Microserviços (em ordem)
```bash
# Terminal 1 - Eureka Server
cd eureka-server && mvn spring-boot:run

# Terminal 2 - Config Server
cd config-server && mvn spring-boot:run

# Terminal 3 - API Gateway
cd api-gateway && mvn spring-boot:run

# Terminal 4 - MCP Service
cd microservice-mcp && mvn spring-boot:run

# Terminal 5 - AI Service
cd microservice-ai && mvn spring-boot:run

# Terminal 6 - Serverless Service
cd microservice-serverless && mvn spring-boot:run
```

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
- `POST /validate` - Validar dados
- `GET /health` - Health check do serviço Serverless

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

### Problemas Comuns:
1. **Serviços não iniciam**: Verificar se Eureka e Config Server iniciaram primeiro
2. **AI usando fallback**: Verificar chave OpenAI no arquivo `.env`
3. **Portas ocupadas**: Verificar se portas 8080-8083, 8761, 8888 estão livres
4. **JMeter falha**: Certificar que todos os serviços estão rodando

### Verificação do Sistema:
```bash
# Verificar serviços registrados no Eureka
curl http://localhost:8761

# Testar endpoints dos microserviços diretamente
curl http://localhost:8081/mcp/status
curl http://localhost:8082/ai/health
curl http://localhost:8083/health
curl http://localhost:8080/actuator/health

# Teste rápido da funcionalidade de IA
curl "http://localhost:8082/ai/generate/teste"
```

### Logs dos Serviços:
Cada microserviço gera logs no console. Para troubleshooting:
- Verifique se todos os serviços se registraram no Eureka
- Confirme se o Config Server está fornecendo configurações
- Monitore logs de erro relacionados à API OpenAI

Este sistema demonstra uma arquitetura de microserviços completa, pronta para produção, com **integração de IA**, **padrões de resiliência**, **observabilidade abrangente** e **testes de carga automatizados**.