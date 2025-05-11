## Estrutura do Projeto & Arquitetura Adotada

Esse backend segue uma **arquitetura modular que prioriza features** (popularizada pelo NestJS e agora adotada aqui com o FastAPI).
Em vez de agrupar o código por preocupação técnica (_controllers_, _services_, _schemas/DTOs_ em pastas globais), cada recurso de domínio vive em seu próprio diretório e contém tudo o que precisa.

```
backend/
│
├── app/ ↴ código da aplicação FastAPI
│ ├── auth/ ↴ ✓ Autenticação / login
│ │ ├── controller.py – endpoint HTTP (/login)
│ │ ├── service.py – lógica de domínio
│ │ └── schema.py – modelos de E/S Pydantic
│ │
│ ├── users/
│ │ ├── controller.py – CRUD + rotas de mapeamento de roles
│ │ ├── service.py – conexão com Keycloak
│ │ └── schema.py
│ │
│ ├── roles/
│ │ ├── controller.py – CRUD para roles do client 'oauth'
│ │ ├── service.py
│ │ └── schema.py
│ │
│ ├── keycloak/ ↴ adapter / SDK para a API Admin do Keycloak
│ │ └── service.py – helpers de baixo nível (chamadas REST)
│ │
│ ├── core/
│ │ └── config.py – configuração do .env + URLs padrões do Keycloak
│ │
│ └── main.py – registro do app + roteador do FastAPI
│
└── Dockerfile – container com Poetry + Uvicorn

```

---

### Por quê esse layout?

| Módulo                                             | Responsabilidade                                                                                                              | Benefícios                                                                 |
| -------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------- |
| **Folders de Features** (`auth`, `users`, `roles`) | Cada um contém **schema → service → controller** para um domínio.                                                             | Fácil onboarding; você abre _uma pasta_ para ver tudo que uma feature faz. |
| **Adapter `keycloak/`**                            | Centraliza _todas_ as chamadas REST do Keycloak.<br> Mantém detalhes de terceiros fora do código que tenha lógica de negócio. | Serviço de IdP pode ser trocado; mockável para testes unitários.           |
| **`core/`**                                        | Preocupações transversais (configurações, dependências compartilhadas).                                                       | Evita a duplicação da análise de ambiente e do código de conexão.          |

---

### Camadas

```
          Requisição HTTP de entrada
                    │
                    ▼
┌──────────────────────────────┐
│        controller.py         │  ◄─►  roteamento / validação FastAPI
└──────────────────────────────┘
                    │  chama
                    ▼
┌──────────────────────────────┐
│          service.py          │  ◄─►  Lógica de negócio / use-cases
└──────────────────────────────┘
                    │  delega para infraestrutura
                    ▼
┌──────────────────────────────┐
│      keycloak.service.py     │  ◄─►  Chamadas REST de baixo nível
└──────────────────────────────┘
                    │  HTTP pela rede
                    ▼
           REST API do Keycloak

(routing) (business logic) (infrastructure)
```

- Controllers sabem **apenas** detalhes sobre o protocolo HTTP (códigos, headers, ...)
- Services contém **lógica de negócio/use-case** (criar usuário, atribuir roles, ...)
- `keycloak.service` é uma fina **camada de infraestrutura** que apenas faz operações HTTP.

---

### Extensibilidade

- **Novas features** podem ser adicionadas através da mesma estrutura das pastas existentes (`schema.py`, `service.py`, `controller.py`)
- Swap Identity Provider: replace `keycloak/admin.py` with another adapter (ex: AWS Cognito, Supabase, ...); services keep their signatures.
- Trocar de IdP: troque o `keycloak/service.py` por outro adapter (AWS Cognito, Supabase, ...); os services irão manter suas assinaturas (get_user, create_user, ...).
- Add a database: inject SQLModel/SQLAlchemy session via `core/dependencies.py` and call it in service layer.
- Adição de um banco de dados: uma session do SQLModel/SQLAlchemy (Python) pode ser injetada através de algo como `core/dependencies.py`, depois ser chamada na camada de serviço.

---

> **RESUMÃO** – Esse projeto foi organizado por **módulos de features**, cada um deles autônomo, seguindo um fluxo limpo de controller → service → adapter.<br>
> Isso reflete o sistema de módulos do framework NestJS, ao mesmo tempo em que se mantém idiomático à FastAPI e fornece limites claros e de fácil manutenção para o crescimento futuro.
