# 🏦 API de Produtos de Empréstimos

## 👥 Desenvolvedor

- Nome: Edigar Pierott Torres
- Papel: Backend Java
- Link para o GitHub: https://github.com/EdigarTorres/desafio-java

 ---

## 📘 Descrição

Projeto desenvolvido como parte do desafio técnico para o Caixaverso. O objetivo é construir uma **API REST** para
cadastro e simulação de empréstimos, utilizando o framework **Quarkus**. A aplicação implementa as operações básicas de
um CRUD completo.

---

## 🚀 Sobre o Projeto

A aplicação permite que, a partir dos dados básicos de um produto de empréstimos, seja realizada uma simulação contendo
os dados necessários para que o cliente tome uma descisão.

---

## 💾 Tecnologias Utilizadas

- **Quarkus** (Framework principal)
- **Java 17** (Linguagem de programação)
- **Maven** (Gerenciamento de dependências e build)
- **RESTEasy** (Implementação JAX-RS para APIs REST)
- **JPA/Hibernate** (Mapeamento objeto-relacional)
- **H2 Database** (Banco de dados em memória)
- **Swagger/OpenAPI** (Documentação da API)
- **Mockito** (Testes unitários)
- **Sonarlint** (Análise estática de código)
- **Git** (Controle de versão)
- **GitHub** (Hospedagem do repositório)
- **Jacoco** (Análise de cobertura de código)

---

## ▶️ Como executar

1. Execute a aplicação em modo de desenvolvimento:
   ```bash
   ./mvnw quarkus:dev

2. Acesse a aplicação via navegador:

☑️ Interface padrão: http://localhost:8080/

☑️ Interface Swagger: http://localhost:8080/q/swagger-ui/

---

## 🧾 Como gerar uma conta

1. Envie os dados do produto no seguinte formats:
   ```
   {
     "id": "long",
     "nome": "String",
     "taxaJurosAnual": "BigDecimal",
     "prazoMaximoMeses": "Integer",
   }

2. A solicitação da simulação do empréstimo deve ser feita com os seguintes dados:
   ```
   {
    "idProduto": "Long",
    "valorSolicitado": "Double",
    "prazoMeses": "Integer",
   }
   ``` 

3. A simulação gerada terá o seguinte formato:
   ```
   {
     "produto": {
       "id": "Long",
       "nome": "String",
       "taxaJurosAnual": "BigDecimal",
       "prazoMaximoMeses": "Integer"
     },
     "valorSolicitado": "String",
     "prazoMeses": "Integer",
     "taxaJurosAnual": "String",
     "taxaJurosEfetivaMensal": "String",
     "valorTotalComJuros": "String",
     "valorParcelaMensal": "String",
     "memoriaCalculo": [
       {
         "mes": "int",
         "amortizacao": "0.00",
         "juros": "0.00",
         "saldoDevedor": "0.10"
       },
       {
         "mes": "int",
         "amortizacao": "String",
         "juros": "String",
         "saldoDevedor": "String"
       },
       ...
---

## 🔗 Endpoints da API

| Método | Rota             | Descrição                              | 
|--------|------------------|----------------------------------------| 
| GET    | `/produtos`      | Lista todos os produtos de empréstimos | 
| GET    | `/produtos/{id}` | Busca um produto de empréstimo por ID  |
| POST   | `/produtos`      | Cadastra um novo produto de empréstimo | 
| DELETE | `/produtos/{id}` | Deleta um produto de empréstimo        | 
| PUT    | `/produtos/{id}` | Atualiza um produto de empréstimo      | 
| POST   | `/simulacoes`    | Simula um empréstimo                   | 

---

🧪 Testes

✅ Como executar os testes

`./mvnw test `

🧪 Cobertura

• Testes unitários com Mockito

---



