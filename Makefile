PHONY: start env-setup

start:
	uvicorn app.main:app --reload

env-setup:
	python3 -m venv .venv
	.venv/bin/pip install -U pip setuptools
	.venv/bin/pip install poetry