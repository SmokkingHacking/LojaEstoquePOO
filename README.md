# Loja UNIEURO — Entrada de Produtos no Estoque (H2 local)

Aplicacao didatica em Java com interface Swing, orientacao a objetos, JDBC, `PreparedStatement`, validacao de regras de negocio e transacao SQL. O banco e H2 local em arquivo; nao precisa instalar um servidor PostgreSQL.

> **Aviso sobre o enunciado:** as orientacoes do trabalho fornecidas mencionam PostgreSQL/Supabase como requisito. Devido a infraestrutura da Unieuro, usaremos o banco local H2, serve para praticar JDBC e persistencia local.

## Requisitos

- JDK 17 ou superior (Java 21 tambem serve).
- IntelliJ IDEA com suporte a Maven.
- Internet na primeira sincronizacao Maven para baixar a dependencia H2.

## Como abrir e executar no IntelliJ IDEA

1. Extraia o ZIP para uma pasta local.
2. Abra o IntelliJ IDEA e escolha **File > Open**. Selecione a pasta `LojaUNIEURO` que contem `pom.xml`.
3. Se solicitado, carregue o projeto como Maven. Aguarde a sincronizacao e o download das dependencias. Se necessario, abra o painel Maven e clique em **Reload All Maven Projects**.
4. Em **File > Project Structure > Project**, selecione JDK 17 ou superior.
5. Na pasta do projeto, abra o terminal e execute:

   ```bash
   cp src/main/resources/db.properties.example src/main/resources/db.properties
   ```

6. Abra `src/main/java/br/edu/unieuro/loja/Main.java` e clique no triângulo verde ao lado de `main`, ou clique com o botao direito no arquivo e escolha **Run 'Main.main()'**.
7. Na primeira execucao, a aplicacao criara automaticamente a pasta `data`, o arquivo do banco, as tabelas e tres produtos de demonstracao. Nao e necessario executar `schema.sql` para iniciar a aplicacao.

## Onde ficam os dados?

O H2 e um banco embutido. O arquivo persistente fica em `data/loja_unieuro.mv.db`, relativo ao diretorio de trabalho do projeto. Ao fechar e abrir a aplicacao novamente, os registros continuam no arquivo. **Nao apague esse arquivo** se quiser preservar os dados de teste.

A dependencia esta configurada no `pom.xml` e o Maven faz o download do H2 2.5.252 na primeira sincronizacao. O H2 e um banco Java com API JDBC e modo embutido, documentado no site oficial: https://h2database.com/ .

## Teste de demonstracao

1. Quando a tela abrir, selecione `MOU001 - Mouse USB`, que inicialmente tem 12 unidades.
2. Digite `8` em **Quantidade recebida**.
3. Informe uma observacao, por exemplo `Reposicao para demonstracao`.
4. Clique em **Registrar entrada**. O estoque devera passar de 12 para 20.
5. Tente quantidade `0`, `-3` e texto. A aplicacao devera rejeitar valores invalidos.
6. Clique em **Atualizar produtos** para recarregar as quantidades.

Cada entrada valida atualiza o saldo e registra o historico dentro da mesma transacao. Se uma etapa falhar, a aplicacao executa `rollback()` e nao confirma a operacao.

## Consultar os registros

Pode-se consultar os dados na aplicacao ou abrir o H2 Console. O console esta disponivel dentro da dependencia Maven; no IntelliJ, tambem e possivel criar uma conexao de banco H2 com JDBC URL `jdbc:h2:file:CAMINHO_ABSOLUTO/LojaUNIEURO/data/loja_unieuro`, usuario `sa` e senha vazia. Garanta que o caminho aponta para o mesmo arquivo do projeto.

Exemplo SQL para conferir o saldo e as entradas:

```sql
SELECT p.codigo, p.nome, p.quantidade_estoque,
       e.id AS entrada_id, e.quantidade AS quantidade_recebida,
       e.data_entrada, e.observacao
FROM produto p
LEFT JOIN entrada_estoque e ON e.produto_id = p.id
ORDER BY e.id DESC NULLS LAST;
```

O script `sql/schema.sql` tambem contem o esquema completo e os produtos de demonstracao, caso o professor queira conferir a parte SQL. O programa cria as mesmas tabelas automaticamente no primeiro inicio.

## Organizacao das classes

- `Main`: prepara o banco e inicia a interface Swing.
- `db/Database`: configura e abre conexoes JDBC com H2 local.
- `db/DatabaseInitializer`: cria tabelas e produtos de demonstracao se ainda nao existirem.
- `model/Cliente`: representa cliente no dominio geral da loja.
- `model/Produto`: representa produto, com atributos encapsulados.
- `model/EntradaEstoque`: representa uma movimentacao de entrada.
- `ui/TelaEntradaEstoque`: interface Swing e eventos dos botoes.
- `service/EstoqueService`: aplica validacoes e regras de negocio.
- `dao/ProdutoDAO`: consulta os produtos usando `PreparedStatement`.
- `dao/EstoqueDAO`: atualiza o saldo e grava o historico usando `PreparedStatement` e transacao.
- `exception/RegraNegocioException`: representa violacoes de regras de negocio.

## Estrutura da entrega

- `src/main/java`: codigo Java.
- `src/main/resources/db.properties.example`: conexao local H2.
- `sql/schema.sql`: script SQL para criar tabelas e inserir exemplos.
- `pom.xml`: dependencias e configuracao Maven.
- `README.md`: instrucoes e declaracao de uso de IA.

## Declaracao de uso de IA
> Foi utilizado o ChatGPT (OpenAI) como ferramenta de apoio para estruturar o exemplo de projeto, revisar conceitos de Java Swing, JDBC, transacoes SQL e preparar instrucoes de execucao. Eu revisei, adaptei e testei o codigo antes da entrega.
