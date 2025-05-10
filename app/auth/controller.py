from fastapi import APIRouter, Depends
from app.auth.service import verify_token

router = APIRouter(prefix="auth", tags=["Authentication"])

@router.get("/login")
def login():
    pass
