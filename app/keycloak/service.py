import httpx
from uuid import UUID
from fastapi import HTTPException, status
from typing import List
from app.core.config import settings
from app.users.schema import UserUpdate


async def exchange_password_grant(username: str, password: str) -> dict:
    """
    Exchanges a password grant for an access token.
    """
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
    Returns the new Keycloak user_id (UUID) on success.
    Raises HTTPException otherwise.
    """
    url = f"{settings.admin_url}/users"
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
    raise HTTPException(status_code=502, detail="Keycloak user‑creation failed")


async def list_users_in_keycloak(token: str, enabled: bool | None = None) -> List[dict]:
    """
    Returns the raw list of KC user dicts.
    Optional `enabled` filter maps to KC query ?enabled=true/false.
    """
    url = f"{settings.admin_url}/users"
    params = {}
    if enabled is not None:
        params["enabled"] = str(enabled).lower()  # "true" | "false"

    headers = {"Authorization": f"Bearer {token}"}

    async with httpx.AsyncClient() as client:
        resp = await client.get(url, headers=headers, params=params)

    if resp.status_code == 200:
        return resp.json()

    if resp.status_code == 401:
        raise HTTPException(status_code=401, detail="Invalid access token")
    if resp.status_code == 403:
        raise HTTPException(status_code=403, detail=f"Forbidden")

    # Unexpected
    raise HTTPException(status_code=502, detail="Keycloak user‑list failed")


async def get_user_in_keycloak(user_id: str, token: str) -> dict:
    """
    Fetch a single user by id from Keycloak Admin REST.
    Raises HTTPException on 401, 403, 404.
    """
    try:
        # quick UUID sanity‑check → 400 if invalid
        UUID(user_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="Malformed user id")

    url = f"{settings.admin_url}/users/{user_id}"
    headers = {"Authorization": f"Bearer {token}"}

    async with httpx.AsyncClient() as client:
        resp = await client.get(url, headers=headers)

    if resp.status_code == 200:
        return resp.json()
    if resp.status_code == 401:
        raise HTTPException(401, "Invalid access token")
    if resp.status_code == 403:
        raise HTTPException(403, "Forbidden")
    if resp.status_code == 404:
        raise HTTPException(404, "User not found")

    raise HTTPException(502, "Keycloak user‑read failed")


async def update_user_in_keycloak(user_id: str, patch: UserUpdate, token: str) -> None:
    """
    Update a single user by id in Keycloak Admin REST.
    Raises HTTPException on 401, 403, 404.
    """
    try:
        UUID(user_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="Malformed user id")

    url = f"{settings.admin_url}/users/{user_id}"
    headers = {"Authorization": f"Bearer {token}"}

    # build payload only with provided fields
    payload: dict = {}
    if patch.username is not None:
        payload["username"] = patch.username
        payload["email"] = patch.username
    if patch.first_name is not None:
        payload["firstName"] = patch.first_name
    if patch.last_name is not None:
        payload["lastName"] = patch.last_name
    if patch.enabled is not None:
        payload["enabled"] = patch.enabled

    if not payload:
        raise HTTPException(status_code=400, detail="No fields to update")

    async with httpx.AsyncClient() as client:
        resp = await client.put(url, json=payload, headers=headers)

    if resp.status_code == 204:  # Keycloak returns 204 No Content on success
        return
    if resp.status_code == 401:
        raise HTTPException(401, "Invalid access token")
    if resp.status_code == 403:
        raise HTTPException(403, "Forbidden")
    if resp.status_code == 404:
        raise HTTPException(404, "User not found")

    raise HTTPException(502, "Keycloak user‑update failed")
