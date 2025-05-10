from fastapi import APIRouter, status
from app.auth.schema import LoginResponse, LoginRequest
from app.keycloak.service import exchange_password_grant

router = APIRouter(prefix="/auth", tags=["Authentication"])


@router.post(
    "/login", response_model=LoginResponse, status_code=status.HTTP_201_CREATED
)
async def login(request: LoginRequest):
    """
    Proxy for Keycloak password‑grant authentication.
    Expects multipart/form‑data with *username* and *password*.
    """
    token_dict = await exchange_password_grant(request.username, request.password)
    return LoginResponse(**token_dict)
