from fastapi import FastAPI
from app.users.controller import router as user_router
from app.auth.controller import router as auth_router
from app.roles.controller import router as role_router

from fastapi import status
from fastapi.responses import JSONResponse, RedirectResponse
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
app.include_router(role_router)


@app.get("/", include_in_schema=False)
async def root():
    return RedirectResponse(url="/docs")


@app.get("/health", tags=["Internal"])
async def health():
    return JSONResponse(
        status_code=status.HTTP_200_OK,
        content={"status": "up"},
    )
