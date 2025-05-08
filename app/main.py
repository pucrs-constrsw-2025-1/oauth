from fastapi import FastAPI
from app.users import controller as user_controller
from starlette.middleware.cors import CORSMiddleware

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Allows all origins
    allow_credentials=True,
    allow_methods=["*"],  # Allows all methods
    allow_headers=["*"],  # Allows all headers
)

app.include_router(user_controller.router, prefix="/users", tags=["users"])

@app.get("/")
async def root():
    return {"message": "THE LOATHSOME DUNGEATER"}