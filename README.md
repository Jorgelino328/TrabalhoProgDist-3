# Sistema Distribuído de IA - Microserviços Spring Boot

## Visão Geral
Este é um sistema abrangente de microserviços distribuídos construído com **Spring Boot** e **Spring Cloud** que integra capacidades de IA. O sistema segue a metodologia do **Manifesto Twelve-Factor** e implementa **padrões de resiliência** para tolerância a falhas e **observabilidade** abrangente.

## Pré-requisitos
- **Java 17+** (JDK)
- **Maven 3.6+**
- **Docker & Docker Compose**
- **Apache JMeter** (para testes de carga)
- **Chave da API OpenAI**

## Tecnologias Utilizadas

### Framework Principal
- **Spring Boot** para desenvolvimento de microserviços
- **Spring Cloud** para arquitetura distribuída
- **Spring AI** para integração com modelos de IA
- **Netflix Eureka** para descoberta de serviços
- **Spring Cloud Gateway** para roteamento e balanceamento
- **Resilience4j** para padrões de resiliência

### Ferramentas de Desenvolvimento
- **Spring Boot DevTools** para recarga automática durante desenvolvimento
- **Maven** para gerenciamento de dependências e build
- **Docker Compose** para orquestração de containers
- **Configuração centralizada** via Spring Cloud Config

## Componentes da Arquitetura

### Serviços Centrais
- **Eureka Server** (Porta 8761): Descoberta e registro de serviços
- **Config Server** (Porta 8888): Gerenciamento centralizado de configuração
- **API Gateway** (Porta 8080): Ponto de entrada com roteamento e circuit breakers

### Microserviços
1. **Microservice MCP** (Porta 8081): Interface com servidores MCP externos
2. **Microservice AI** (Porta 8082): Integração com API OpenAI usando Spring AI
3. **Microservice Serverless** (Porta 8083): Implementa funções serverless para transformação de dados

### Stack de Observabilidade
- **Prometheus** (Porta 9090): Coleta de métricas
- **Grafana** (Porta 3001): Visualização de métricas e dashboards
- **Zipkin** (Porta 9411): Rastreamento distribuído

### Testes
- **JMeter**: Testes de carga e avaliação de performance
- **Mock MCP Server** (Porta 3000): Simula servidor MCP externo para testes

## Conformidade com Twelve-Factor App

1. **Base de Código**: Código único rastreado em controle de versão
2. **Dependências**: Maven gerencia todas as dependências explicitamente
3. **Configuração**: Configuração específica do ambiente via Config Server
4. **Serviços de Apoio**: Serviços externos (OpenAI API, MCP Server) tratados como recursos anexados
5. **Build/Release/Run**: Separação clara entre estágios de build, release e execução
6. **Processos**: Processos stateless com arquitetura shared-nothing
7. **Vinculação de Porta**: Serviços exportam HTTP via port binding
8. **Concorrência**: Escalonamento horizontal através de processos
9. **Descartabilidade**: Inicialização rápida e encerramento gracioso
10. **Paridade Dev/Prod**: Paridade de ambiente mantida entre estágios
11. **Logs**: Tratamento de logs como fluxos de eventos via Spring Boot logging
12. **Processos Admin**: Tarefas administrativas executadas como processos únicos

## Padrões de Resiliência

### Circuit Breaker
- Implementado usando Resilience4j em todos os microserviços
- Previne falhas em cascata com mecanismos de fallback
- Limites de falha configuráveis e timeouts de recuperação

### Padrão Retry
- Retry automático com backoff exponencial
- Tentativas de retry configuráveis e durações de espera
- Aplicado a chamadas de serviços externos

### Padrão Timeout
- Timeouts de requisição configurados para todas as chamadas externas
- Previne esgotamento de recursos por requisições pendentes

### Padrão Bulkhead
- Isolamento de serviços através de thread pools separadas
- Isolamento de recursos entre diferentes operações de serviço

## Começando

### Configuração Inicial
1. **Definir variáveis de ambiente**:
   ```bash
   export OPENAI_API_KEY=sua-chave-openai-aqui
   ```

2. **Construir o sistema**:
   ```bash
   ./build-system.sh
   ```

3. **Iniciar com script otimizado para Spring Boot**:
   ```bash
   ./start-spring-boot-system.sh
   ```

4. **Alternativa - Iniciar serviços individuais**:
   ```bash
   # Iniciar monitoramento
   docker-compose up -d
   
   # Iniciar serviços em ordem
   cd eureka-server && mvn spring-boot:run &
   cd config-server && mvn spring-boot:run &
   cd api-gateway && mvn spring-boot:run &
   cd microservice-mcp && mvn spring-boot:run &
   cd microservice-ai && mvn spring-boot:run &
   cd microservice-serverless && mvn spring-boot:run &
   ```

### Testando o Sistema
```bash
# Executar testes de integração
./test-system.sh

# Executar testes de carga
./run-load-test.sh
```

## Endpoints da API

### Serviço AI
- `GET /api/ai/generate/{topico}` - Gerar texto sobre um tópico
- `POST /api/ai/prompt` - Processar prompt personalizado de IA

### Serviço MCP  
- `POST /api/mcp/execute` - Executar comando MCP
- `GET /api/mcp/status` - Obter status do servidor MCP

### Serviço Serverless
- `POST /api/serverless/function/{nomeFuncao}` - Executar função serverless
- `POST /api/serverless/validate` - Validar dados

## Estratégia de Testes de Carga

### Descoberta da Capacidade Knee
O sistema inclui testes de carga abrangentes para determinar:
- **Knee Capacity**: Ponto onde o tempo de resposta começa a degradar significativamente
- **Usable Capacity**: Taxa de transferência máxima sustentada com tempos de resposta aceitáveis

### Cenários de Teste
1. Testes de carga normal com usuários simultâneos configuráveis
2. Injeção de falhas para testar comportamento do circuit breaker
3. Testes de recuperação quando serviços voltam online
4. Testes de capacidade para encontrar limites do sistema

### Características de Observabilidade
- Métricas em tempo real via Prometheus e Grafana
- Rastreamento distribuído via Zipkin
- Monitoramento do estado do circuit breaker
- Métricas de performance e monitoramento de SLA
- Health checks e probes de prontidão

## Teste de Tolerância a Falhas
Durante a apresentação, o sistema demonstra:
1. **Zero erros** sob condições de carga normal
2. **Degradação graciosa** quando serviços falham
3. **Recuperação automática** quando serviços reiniciam
4. **Mudanças de estado do circuit breaker** visíveis no monitoramento
5. **Medição de impacto na performance** durante falhas

## Padrões de Microserviços Implementados
- **Padrão API Gateway**: Ponto único de entrada para todas as requisições
- **Padrão Service Discovery**: Registro e descoberta automática de serviços
- **Padrão Circuit Breaker**: Tolerância a falhas e mecanismos de fallback
- **Padrão Configuration Server**: Gerenciamento centralizado de configuração
- **Padrão Health Check**: Monitoramento da saúde dos serviços
- **Padrão Distributed Tracing**: Rastreamento de requisições através dos serviços
- **Padrão Bulkhead**: Isolamento de recursos
- **Padrão Retry**: Recuperação automática de falhas

Este sistema demonstra uma arquitetura de microserviços pronta para produção com observabilidade abrangente, padrões de resiliência e capacidades de integração de IA.
