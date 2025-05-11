from typing import List
from app.keycloak.service import (
    create_user_in_keycloak,
    disable_user_in_keycloak,
    get_user_in_keycloak,
    list_users_in_keycloak,
    reset_user_password_in_keycloak,
    update_user_in_keycloak,
)
from app.users.schema import PasswordUpdate, UserCreate, UserOut, UserUpdate


async def create_user(user_in: UserCreate, token: str) -> UserOut:
    user_id = await create_user_in_keycloak(
        username=user_in.username,
        password=user_in.password,
        first_name=user_in.first_name,
        last_name=user_in.last_name,
        token=token,
    )

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


async def get_user(user_id: str, access_token: str) -> UserOut:
    kc_user = await get_user_in_keycloak(user_id, access_token)
    return UserOut(
        id=kc_user["id"],
        username=kc_user["username"],
        first_name=kc_user.get("firstName", ""),
        last_name=kc_user.get("lastName", ""),
        enabled=kc_user.get("enabled", True),
    )


async def update_user(user_id: str, patch: UserUpdate, access_token: str) -> None:
    await update_user_in_keycloak(user_id, patch, access_token)


async def update_password(
    user_id: str, patch: PasswordUpdate, access_token: str
) -> None:
    await reset_user_password_in_keycloak(user_id, patch.password, access_token)


async def disable_user(user_id: str, access_token: str) -> None:
    await disable_user_in_keycloak(user_id, access_token)
