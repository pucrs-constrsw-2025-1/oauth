from fastapi import FastAPI
from app.users import controller as user_controller

app = FastAPI()

app.include_router(user_controller.router, prefix="/users", tags=["users"])
