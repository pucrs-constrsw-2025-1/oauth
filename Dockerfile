# ---- base image ----
FROM python:3.12-slim

# ---- environment setup ----
# 1) PYTHONDONTWRITEBYTECODE = prevent .pyc files not needed in containers
# 2) PYTHONUNBUFFERED = flush output immediately, logs in real time
ENV PYTHONDONTWRITEBYTECODE=1 \ 
    PYTHONUNBUFFERED=1 \
    POETRY_VERSION=2.1.2

# ---- install Poetry ----
RUN pip install "poetry==$POETRY_VERSION"
RUN poetry config virtualenvs.create false

# ---- set work directory ----
WORKDIR /app

# ---- copy project files ----
COPY pyproject.toml poetry.lock* ./
RUN poetry install --no-root --only main

COPY . .

# ---- expose port & run ----
EXPOSE 8080
CMD ["poetry", "run", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8080"]
