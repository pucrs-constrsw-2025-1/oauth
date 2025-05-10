import httpx
from fastapi import HTTPException, status

from app.core.config import settings

KC_TOKEN_URL = (
    f"{settings.keycloak_base_url}/auth/realms/"
    f"{settings.keycloak_realm}/protocol/openid-connect/token"
)


async def exchange_password_grant(username: str, password: str) -> dict:
    data = {
        "grant_type": settings.keycloak_grant_type,  # "password"
        "client_id": settings.keycloak_client_id,
        "client_secret": settings.keycloak_client_secret,
        "username": username,
        "password": password,
    }

    async with httpx.AsyncClient() as client:
        try:
            resp = await client.post(KC_TOKEN_URL, data=data)
            resp.raise_for_status()
            return resp.json()
        except httpx.HTTPStatusError as exc:
            if exc.response.status_code == 400:
                raise HTTPException(
                    status_code=status.HTTP_401_UNAUTHORIZED,
                    detail="Invalid username or password",
                )
            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail="Failed to reach Keycloak",
            )
