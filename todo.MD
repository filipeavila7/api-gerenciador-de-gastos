# TODO - Gerenciador de Família

## Família
- [x] Criar família (admin automático)
- [x] Listar minhas famílias
- [x] Buscar família por ID (validando acesso)
- [x] Atualizar família (somente admin)
- [x] Deletar família (somente admin)
- [x] Listar membros da família
- [x] Adicionar membro (somente admin)
- [x] Remover membro (somente admin)
- [x] Sair da família (com troca de admin se necessário)
- [x] Admin geral poder adcionar qualquer usuario a qualquer familia

---
****
## Categorias
- [x] Criar categoria (somente admin)
- [X] Listar categorias por família
- [X] Atualizar categoria
- [X] Deletar categoria
- [X] Paginação de categorias
- [X] Controller de categorias
- [X] Validar categoria pertence à família ao usar em produtos
- [X] rotas admin geral para categorias - CategoryAdminService

---

## Produtos
- [x] Criar produto
- [x] Listar produtos por família
- [X] Atualizar produto
- [x] Deletar produto
- [X] Deletar produtos em lote
- [x] Associar produto à categoria
- [x] Validar categoria da mesma família
- [ ] Filtrar por categoria
- [x] Paginação de produtos
- [X] controller de produtos
- [x] metodos admin geral 

---

## Segurança
- [x] Verificação de admin
- [x] Verificação de membro da família
- [ ] Centralizar regras de autorização melhor (helper/service)
- [ ] Validar acesso a categorias
- [ ] Evitar repetição de lógica de permissão

---

---

## purchase
- [X] Criar compra
- [X] Adcionar produto na compra
- [X] Adcionar varios produto na compra
- [X] Calcular total de preço da compra
- [X] Deletar compra
- [X] Deletar varias compras
- [X] listar todas as compras de uma familia - paginados
- [X] listar todos os produtos em uma compra - paginados
- [X] Editar compra
- [X] Deletar produto da compra
- [X] Deletar varios produto da compra
- [X] Editar dados do produto dentro da compra - quantidade e preço unico
- [X] Controller de purchase
- [ ] Metodos admin
- [x] Verificação de produtos caso tente apagar mas ele exista em purchaseItens

---

## Testes
- [x] Testes de criação de família
- [x] Testes de adicionar membro
- [x] Testes de permissão (admin vs não-admin)
- [ ] Testes de categoria
- [ ] Testes de produto
- [ ] Testes de exceções (AccessDenied, NotFound)

---

## Transactions
- [x] Criar transação de receita (INCOME)
- [x] Criar transação de despesa (EXPENSE)
- [x] Criar transação de despesa automaticamente ao fechar compra
- [x] Listar todas as transações da família (paginado)
- [x] Buscar transação por ID
- [x] Buscar detalhes de uma transação
- [x] Exibir itens da compra vinculada em transações do tipo EXPENSE
- [x] Associar transação de despesa a uma compra
- [x] Permitir transações sem compra associada (INCOME)
- [x] Calcular saldo da família (INCOME - EXPENSE)
- [ ] Filtrar transações por tipo
- [ ] Filtrar transações por período
- [ ] Editar transação manual
- [ ] Deletar transação manual
- [ ] Métodos admin geral


---

## Shopping List
- [X] Criar lista de compras
- [X] Listar listas de compras por família
- [ ] Buscar lista de compras por ID validando família
- [X] Atualizar lista de compras
- [X] Deletar lista de compras
- [X] Deletar várias listas de compras
- [X] Paginação de listas de compras
- [ ] Adicionar produtos na lista de compras
- [ ] Remover produtos da lista de compras
- [ ] Editar quantidade dos produtos da lista
- [ ] Listar produtos dentro de uma lista de compras
- [ ] Validar produtos pertencentes à mesma família
- [ ] Controller de Shopping List
- [ ] Permissões de acesso (membro/admin)
- [ ] Métodos admin geral para listas de compras

---

## Admin
- [ ] Painel admistrativo estatístico - quantidade de familias, usuarios etc


---

## Melhorias futuras
- [ ] Paginação geral padrão
- [x] Soft delete
- [ ] Logs de ações
- [ ] Auditoria (createdAt, createdBy)