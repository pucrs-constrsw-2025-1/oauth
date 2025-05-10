from typing import List
from app.keycloak.service import (create_user_in_keycloak, list_users_in_keycloak)
from app.users.schema import UserCreate, UserOut


async def create_user(user_in: UserCreate, token: str) -> UserOut:
    user_id = await create_user_in_keycloak(user_in, token)
    return UserOut(
        id=user_id,
        username=user_in.username,
        first_name=user_in.first_name,
        last_name=user_in.last_name,
        enabled=True,
    )

async def get_users(access_token: str, enabled: bool | None = None) -> List[UserOut]:
    kc_users = await list_users_in_keycloak(access_token, enabled)
    return [
        UserOut(
            id=u["id"],
            username=u["username"],
            first_name=u.get("firstName", ""),
            last_name=u.get("lastName", ""),
            enabled=u.get("enabled", True),
        )
        for u in kc_users
    ]