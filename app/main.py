from fastapi import FastAPI
from app.users.controller import router as user_router
from app.auth.controller import router as auth_router
from starlette.middleware.cors import CORSMiddleware

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Allows all origins
    allow_credentials=True,
    allow_methods=["*"],  # Allows all methods
    allow_headers=["*"],  # Allows all headers
)

app.include_router(user_router)
app.include_router(auth_router)

@app.get("/")
async def root():
    return {"message": "THE LOATHSOME DUNGEATER"}