# Grupo 8 – Setup do Projeto

_Alunos: Filipe Giroleti, Lucca Mazzoccato e Matheus Melo_

Este arquivo contém as instruções para configurar o ambiente de desenvolvimento do projeto do **Grupo 8**. Para os esclarecimentos de decisões arquiteturais, se referir ao arquivo `architecture.md`

A rota para a documentação da API é `/docs`

---

## Pré-requisitos

Antes de começar, verifique se você tem o **Python 3** instalado. Para criar um ambiente virtual e instalar as dependências, siga os passos abaixo.

---

## Configuração do Ambiente

### 1. Crie um ambiente virtual

```bash
python3 -m venv .venv
```

### 2. Ative o ambiente virtual

No Linux/macOS:

```bash
source .venv/bin/activate
```

No Windows (PowerShell):

```powershell
.venv\Scripts\Activate.ps1
```

### 3. Atualize o `pip` e instale o `poetry`

```bash
.venv/bin/pip install -U pip setuptools
.venv/bin/pip install poetry
```

---

## Instalação das Dependências

Com o ambiente virtual **ativado**, instale todas as dependências do projeto utilizando o Poetry:

```bash
poetry install
```

---
