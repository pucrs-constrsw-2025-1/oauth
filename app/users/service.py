from app.keycloak.service import create_user_in_keycloak
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
