from app.keycloak.service import create_client_role
from app.roles.schema import RoleCreate, RoleOut


async def create_role(role_in: RoleCreate, access_token: str) -> RoleOut:
    role_id = await create_client_role(role_in, access_token)
    return RoleOut(
        id=role_id,
        name=role_in.name,
        description=role_in.description,
        client_role=True,
    )
