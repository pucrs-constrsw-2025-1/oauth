from typing import List
from app.keycloak.service import create_client_role, list_client_roles
from app.roles.schema import RoleCreate, RoleOut


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
