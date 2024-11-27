## UrlShortener com AWS Lambda e API Gateway

Este projeto utiliza Java com Maven e Lombok implementando um serviço de encurtador de URLs utilizando AWS Lambda, API Gateway e S3 (Bucket). A ideia principal é fornecer uma maneira simples de gerar links encurtados e realizar o redirecionamento para o URL original.

### Tecnologias utilizadas

- AWS Lambda: Funções serverless para o processamento.

- API Gateway: Gerencia as requisições HTTP e invoca as Lambdas.

- S3: Armazenamento dos dados das URLs encurtadas.


### Como funciona?

O projeto possui duas funções principais:

- Create: Gera uma URL encurtada e armazena os dados no S3.

- Redirect: Redireciona o usuário para o URL original, verificando se a URL ainda é válida (com base no tempo de expiração).

### Fluxo

A função Create recebe um URL original e um tempo de expiração em segundos, gera um código de URL curto (um UUID), e armazena as informações no S3.

A função Redirect recebe o código curto, verifica o tempo de expiração, e redireciona para o URL original.

### Configuração do Ambiente

- Pré-requisitos:

  - AWS CLI configurado com as credenciais da sua conta AWS.
  - Java 21 instalado no seu computador.
  - Maven instalado para compilar o código.
  - Conta AWS com permissões para Lambda, S3 e API Gateway.
 
### Passos para rodar

#### Clone o repositório:

git clone https://github.com/seu-usuario/url-shortener.git
cd url-shortener

#### Compile o projeto:

mvn clean install

### Implemente as funções no AWS Lambda:

Crie duas funções Lambda: Create e Redirect.

Faça o deploy das funções utilizando o AWS CLI ou console AWS.

Configure o API Gateway para as funções Lambda.

Verifique se o bucket S3 está configurado para armazenar os dados das URLs encurtadas. O código curto gerado será armazenado como arquivos JSON no S3, onde cada arquivo conterá a URL original e o tempo de expiração.

### Estrutura do Projeto

#### O projeto está dividido em duas Lambdas:

- Create Lambda: Responsável por gerar o código encurtado e armazenar os dados no S3.

- Redirect Lambda: Responsável por redirecionar o usuário para o URL original.

### Função Create

- Endpoint: POST /create

- Entrada: JSON contendo o URL original e o tempo de expiração.

- Saída: JSON com o código curto gerado.

### Função Redirect

- Endpoint: GET /{shortUrlCode}

- Entrada: Código da URL encurtada.

- Saída: Redirecionamento (código HTTP 302) para a URL original.

### Exemplos de Requisições

#### Criar URL Encurtada

##### POST /create

{
  "originalUrl": "https://www.exemplo.com",
  "expirationTime": "1734846656"
}

##### Resposta:

{
  "code": "42314d"
}

### Redirecionar

GET /42314d

Resposta: Redireciona para "https://www.exemplo.com."

### Como funciona o processo de redirecionamento?

Quando o código encurtado é acessado via o API Gateway, ele chama a função Lambda de Redirect que consulta o S3 para verificar se o código existe e se o tempo de expiração não passou. Se o tempo de expiração tiver passado, retorna um código 410. Caso contrário, retorna um redirecionamento (HTTP 302) para o URL original.

## Conclusão

Esse projeto exemplifica como utilizar as funções serverless da AWS para criar um serviço de encurtador de URLs simples, escalável e com baixo custo. Você pode expandir facilmente a aplicação, como adicionar autenticação, melhorar o processo de expiração, ou até mesmo adicionar uma interface de usuário para facilitar o uso.
