
# Desafio técnico LuizaLabs

O desafio foi desenvolvido na linguagem Java 8 ([openjdk "1.8.0_282"](https://docs.datastax.com/en/jdk-install/doc/jdk-install/installOpenJdkDeb.html)) utilizando o framework [spring boot](https://spring.io/projects/spring-boot) para construir a api e consumir a api externa disponibilizada.

Como mecanismo de segurança na autenticação e autorização foi utilizado o [JWT](https://jwt.io/), o banco de dados [postgres](https://www.postgresql.org/) e documentação [Swagger](https://swagger.io/).

Acessar a documentação em: http://localhost:8080/swagger-ui/

## Execução da aplicação
O desafio pode ser executado utilizando [docker](https://www.docker.com/) ou [docker-compose](https://docs.docker.com/compose/).

* [Sem uso de docker](#Sem-uso-de-docker)
* [Docker com testes](#Usando-docker-e-realizando-os-testes)
* [Docker-compose com testes](#Usando-docker-e-realizando-os-testes)

### [Sem uso de docker](/java-spring)
Com o banco de dados postgres configurado para responder [localhost:5432/client](), executar os seguintes comandos (Linux):
``` bash  
./mvnw clean install  
java -jar target/clientes-api-0.0.1-SNAPSHOT.jar  
```  

### [Docker com testes](/docker-com-teste)
``` bash  
docker run -it -p 5432:5432 --name postgresqldb -e POSTGRES_PASSWORD=luizalabs -e POSTGRES_USER=luizalabs -e POSTGRES_DB=client -d postgres  
./mvnw clean install  
java -jar target/clientes-api-0.0.1-SNAPSHOT.jar  
```  
### [Docker-compose com testes](/docker-compose-com-teste)
``` bash  
docker-compose -f docker-compose-db.yml up -d  
./mvnw clean install  
docker-compose -f docker-compose.yml up -d  
```  

## Autenticando
Abaixo tem um exemplo de autenticação utilizando um usuário com perfil USER.

**URL**: http://localhost:8080/authenticate

**Body**:
```json  
{  
  "username": "luizalabs",  
  "password": "luizalabs"  
}  
```  
**Retorno:**
```json  
{  
  "jwtToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsdWl6YWxhYnMiLCJleHAiOjE2MTM3MzA4NzQsImlhdCI6MTYxMzcxMjg3NH0.5L_hdktrxTNsrg7lFf2inPJ6tghQH2hPPQief6-2aE0AhriYhgnnbr1c5kiq48DgVDp1VvvFvblLUBIEQEHYmw"  
}  
```  

**Observação:** Os usuários estão no /clientesapi/service/UserService. Não foi realizado o controle no banco por praticidade no teste. Abaixo a tabela de usuários para teste:
| username      | password | role |
| --------- | -----:|-----:|
| admin     | admin | ADMIN, USER |
| luizalabs |luizalabs |USER |

### Autorizações
A api contém o usuário com perfil ADMIN e USER, por padrão se o usuário é ADMIN também ter permissões do perfil de USER.

ROLE_ADMIN:
* Permissão de listar sem filtrar. Ex: GET http://localhost:8080/api/v1/client;
* Inclui permissões para todas operações do perfil de USER.

ROLE_USER:
* Permissão de listar somente com filtros;
* Permissão de criar, alterar e deletar.