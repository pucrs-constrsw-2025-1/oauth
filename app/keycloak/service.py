import httpx
from fastapi import HTTPException, status

from app.core.config import settings


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
            resp = await client.post(settings.token_url, data=data)
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


async def create_user_in_keycloak(
    username: str, password: str, first_name: str, last_name: str, token: str
) -> str:
    """
    Returns the new Keycloak user‑id (UUID) on success.
    Raises HTTPException otherwise.
    """
    url = f"{settings.keycloak_base_url}/admin/realms/{settings.keycloak_realm}/users"
    headers = {"Authorization": f"Bearer {token}"}
    payload = {
        "username": username,
        "email": username,
        "enabled": True,
        "firstName": first_name,
        "lastName": last_name,
        "credentials": [{"type": "password", "value": password, "temporary": False}],
    }

    async with httpx.AsyncClient() as client:
        resp = await client.post(url, json=payload, headers=headers)

    if resp.status_code == 201:
        kc_location = resp.headers.get("Location", "")
        user_id = kc_location.rsplit("/", 1)[-1]  # last path segment
        return user_id

    if resp.status_code == 401:
        raise HTTPException(status_code=401, detail="Invalid access token")
    if resp.status_code == 403:
        raise HTTPException(status_code=403, detail="Forbidden")
    if resp.status_code == 409:
        raise HTTPException(status_code=409, detail="Username already exists")

    # Anything else: propagate as bad gateway
    raise HTTPException(status_code=502, detail="Keycloak user‑creation failed")
