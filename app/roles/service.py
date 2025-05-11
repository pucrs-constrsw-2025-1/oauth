from typing import List
from app.keycloak.service import (
    create_client_role,
    get_role_by_id,
    list_client_roles,
    update_role_in_keycloak,
)
from app.roles.schema import RoleCreate, RoleOut, RoleUpdateFull


async def create_role(role_in: RoleCreate, access_token: str) -> RoleOut:
    role_id = await create_client_role(role_in, access_token)
    return RoleOut(
        id=role_id,
        name=role_in.name,
        description=role_in.description,
        client_role=True,
    )


async def get_roles(access_token: str) -> List[RoleOut]:
    kc_roles = await list_client_roles(access_token)
    return [
        RoleOut(
            id=r["id"],
            name=r["name"],
            description=r.get("description"),
            client_role=True,
        )
        for r in kc_roles
    ]


async def get_role(role_id: str, access_token: str) -> RoleOut:
    kc_role = await get_role_by_id(role_id, access_token)
    return RoleOut(
        id=kc_role["id"],
        name=kc_role["name"],
        description=kc_role.get("description"),
        client_role=kc_role.get("clientRole", True),
    )


async def update_role(role_id: str, upd: RoleUpdateFull, access_token: str) -> None:
    await update_role_in_keycloak(role_id, upd, access_token)
