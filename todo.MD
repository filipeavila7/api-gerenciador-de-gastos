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

---
****
## Categorias
- [x] Criar categoria (somente admin)
- [X] Listar categorias por família
- [X] Atualizar categoria
- [X] Deletar categoria
- [X] Paginação de categorias
- [ ] Controller de categorias
- [ ] Validar categoria pertence à família ao usar em produtos
- [ ] rotas admin geral para categorias - ProductAdminService

---

## Produtos
- [ ] Criar produto
- [ ] Listar produtos por família
- [ ] Atualizar produto
- [ ] Deletar produto
- [ ] Deletar produtos em lote
- [ ] Associar produto à categoria
- [ ] Validar categoria da mesma família
- [ ] Filtrar por categoria
- [ ] Paginação de produtos

---

## Segurança
- [x] Verificação de admin
- [x] Verificação de membro da família
- [ ] Centralizar regras de autorização melhor (helper/service)
- [ ] Validar acesso a categorias
- [ ] Evitar repetição de lógica de permissão

---

## Testes
- [x] Testes de criação de família
- [x] Testes de adicionar membro
- [x] Testes de permissão (admin vs não-admin)
- [ ] Testes de categoria
- [ ] Testes de produto
- [ ] Testes de exceções (AccessDenied, NotFound)

---

## Melhorias futuras
- [ ] Paginação geral padrão
- [ ] Soft delete
- [ ] Logs de ações
- [ ] Auditoria (createdAt, createdBy)