import uuid
import httpx
from uuid import UUID
from fastapi import HTTPException, status
from datetime import datetime, timezone
from typing import List
from app.core.config import settings
from app.users.schema import UserUpdate
from app.roles.schema import RoleCreate, RoleUpdateFull, RoleUpdatePartial

_client_id_cache: dict[str, str] = {}  # clientId -> uuid


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


async def reset_user_password_in_keycloak(
    user_id: str, new_password: str, token: str
) -> None:
    """
    Resets the password of a single user by id in Keycloak Admin REST.
    """
    try:
        UUID(user_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="Malformed user id")

    url = f"{settings.admin_url}/users/{user_id}/reset-password"

    headers = {"Authorization": f"Bearer {token}"}
    payload = {
        "type": "password",
        "value": new_password,
        "temporary": False,
    }

    async with httpx.AsyncClient() as client:
        resp = await client.put(url, json=payload, headers=headers)

    if resp.status_code == 204:  # KC returns 204 on success
        return
    if resp.status_code == 401:
        raise HTTPException(401, "Invalid access token")
    if resp.status_code == 403:
        raise HTTPException(403, "Forbidden")
    if resp.status_code == 404:
        raise HTTPException(404, "User not found")

    raise HTTPException(502, "Keycloak password‑reset failed")


async def disable_user_in_keycloak(user_id: str, token: str) -> None:
    """
    Logical delete of a user in Keycloak, simply sets its 'enabled' flag to false.
    """
    try:
        UUID(user_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="Malformed user id")

    url = f"{settings.admin_url}/users/{user_id}"

    headers = {"Authorization": f"Bearer {token}"}
    payload = {"enabled": False}

    async with httpx.AsyncClient() as client:
        resp = await client.put(url, json=payload, headers=headers)

    if resp.status_code == 204:
        return
    if resp.status_code == 401:
        raise HTTPException(401, "Invalid access token")
    if resp.status_code == 403:
        raise HTTPException(403, "Forbidden")
    if resp.status_code == 404:
        raise HTTPException(404, "User not found")

    raise HTTPException(502, "Keycloak disable-user failed")


async def get_client_uuid(client_id: str, token: str) -> str:
    if client_id in _client_id_cache:
        return _client_id_cache[client_id]

    url = f"{settings.admin_url}/clients?clientId={client_id}"

    headers = {"Authorization": f"Bearer {token}"}

    async with httpx.AsyncClient() as client:
        resp = await client.get(url, headers=headers)

    if resp.status_code == 401:
        raise HTTPException(401, "Invalid access token")

    if resp.status_code != 200:
        raise HTTPException(502, "Failed to resolve client uuid")

    data = resp.json()
    if not data:
        raise HTTPException(404, "Client not found")

    uuid = data[0]["id"]
    _client_id_cache[client_id] = uuid
    return uuid


async def create_client_role(role: RoleCreate, token: str) -> str:
    """
    POST /admin/realms/{realm}/clients/{client_uuid}/roles
    """
    client_uuid = await get_client_uuid(settings.keycloak_client_id, token)

    url = f"{settings.admin_url}/clients/{client_uuid}/roles"

    headers = {"Authorization": f"Bearer {token}"}
    payload = {"name": role.name, "description": role.description}

    async with httpx.AsyncClient() as client:
        resp = await client.post(url, json=payload, headers=headers)

    if resp.status_code == 201:
        # KC returns Location header /{id}
        return resp.headers.get("Location", "").rsplit("/", 1)[-1]

    if resp.status_code == 401:
        raise HTTPException(401, "Invalid access token")
    if resp.status_code == 403:
        raise HTTPException(403, "Forbidden")
    if resp.status_code == 409:
        raise HTTPException(409, "Role already exists")

    raise HTTPException(502, "Keycloak role‑creation failed")


async def list_client_roles(token: str) -> List[dict]:
    """
    GET /admin/realms/{realm}/clients/{client_uuid}/roles
    """
    client_uuid = await get_client_uuid(settings.keycloak_client_id, token)

    url = f"{settings.admin_url}/clients/{client_uuid}/roles"
    headers = {"Authorization": f"Bearer {token}"}

    async with httpx.AsyncClient() as client:
        resp = await client.get(url, headers=headers)

    if resp.status_code == 200:
        return resp.json()

    if resp.status_code == 401:
        raise HTTPException(401, "Invalid access token")
    if resp.status_code == 403:
        raise HTTPException(403, "Forbidden")

    raise HTTPException(502, "Keycloak role‑list failed")


async def get_role_by_id(role_id: str, token: str) -> dict:
    """
    Keycloak 21+ exposes *any* role (realm or client) at:
      GET /admin/realms/{realm}/roles-by-id/{role_id}
    We use that because it works for client roles too.
    """
    try:
        UUID(role_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="Malformed role id")

    url = f"{settings.admin_url}/roles-by-id/{role_id}"

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
        raise HTTPException(404, "Role not found")

    raise HTTPException(502, "Keycloak role‑read failed")


async def update_role_in_keycloak(
    role_id: str, upd: RoleUpdateFull, token: str
) -> None:
    """
    PUT  /admin/realms/{realm}/roles-by-id/{role_id}
    Quarkus Keycloak supports full role update at that URL.
    """
    try:
        UUID(role_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="Malformed role id")

    url = f"{settings.admin_url}/roles-by-id/{role_id}"

    headers = {"Authorization": f"Bearer {token}"}
    payload = {"name": upd.name, "description": upd.description}

    async with httpx.AsyncClient() as client:
        resp = await client.put(url, json=payload, headers=headers)

    if resp.status_code == 204:  # 204 on success
        return
    if resp.status_code == 401:
        raise HTTPException(401, "Invalid access token")
    if resp.status_code == 403:
        raise HTTPException(403, "Forbidden")
    if resp.status_code == 404:
        raise HTTPException(404, "Role not found")

    raise HTTPException(502, "Keycloak role‑update failed")


async def patch_role_in_keycloak(role_id: str, patch: RoleUpdatePartial, token: str):
    try:
        UUID(role_id)
    except ValueError:
        raise HTTPException(400, "Malformed role id")

    # Step 1 – get current role data
    current = await get_role_by_id(role_id, token)

    # Step 2 – merge values
    updated = {
        "name": patch.name or current["name"],
        "description": (
            patch.description
            if patch.description is not None
            else current.get("description")
        ),
    }

    # Step 3 – send full PUT payload
    url = f"{settings.admin_url}/roles-by-id/{role_id}"

    headers = {"Authorization": f"Bearer {token}"}

    async with httpx.AsyncClient() as client:
        resp = await client.put(url, json=updated, headers=headers)

    if resp.status_code == 204:
        return
    if resp.status_code == 401:
        raise HTTPException(401, "Invalid access token")
    if resp.status_code == 403:
        raise HTTPException(403, "Forbidden")
    if resp.status_code == 404:
        raise HTTPException(404, "Role not found")

    raise HTTPException(502, "Keycloak partial update failed")


async def hard_delete_role(role_id: str, token: str) -> None:
    """
    Permanently deletes a client role in Keycloak.
    """
    try:
        UUID(role_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="Malformed role id")

    url = f"{settings.admin_url}/roles-by-id/{role_id}"
    
    headers = {"Authorization": f"Bearer {token}"}

    async with httpx.AsyncClient() as client:
        resp = await client.delete(url, headers=headers)

    if resp.status_code in (204, 200):
        return
    if resp.status_code == 401:
        raise HTTPException(401, "Invalid access token")
    if resp.status_code == 403:
        raise HTTPException(403, "Forbidden")
    if resp.status_code == 404:
        raise HTTPException(404, "Role not found")

    raise HTTPException(502, "Keycloak delete‑role failed")


async def add_role_to_user(user_id: str, role_id: str, token: str) -> None:
    """
    Assign a client role to a user.
    """
    for _id in (user_id, role_id):
        try:
            UUID(_id)
        except ValueError:
            raise HTTPException(status_code=400, detail="Malformed id")

    client_uuid = await get_client_uuid(settings.keycloak_client_id, token)

    role_repr = await get_role_by_id(role_id, token)

    url = (
        f"{settings.admin_url}/users/{user_id}/role-mappings/clients/{client_uuid}"
    )
    headers = {"Authorization": f"Bearer {token}"}

    async with httpx.AsyncClient() as client:
        resp = await client.post(url, json=[role_repr], headers=headers)

    if resp.status_code in (204, 200):
        return
    if resp.status_code == 401:
        raise HTTPException(401, "Invalid access token")
    if resp.status_code == 403:
        raise HTTPException(403, "Forbidden")
    if resp.status_code == 404:
        raise HTTPException(404, "User or role not found")

    raise HTTPException(502, "Keycloak add‑role failed")