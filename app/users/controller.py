from fastapi import (
    APIRouter,
    Depends,
    HTTPException,
    Path,
    Query,
    Response,
    status,
    Header,
)
from typing import List
from app.users.schema import PasswordUpdate, UserCreate, UserOut, UserUpdate
from app.users.service import (
    assign_role,
    create_user,
    disable_user,
    get_user,
    get_users,
    update_password,
    update_user,
)


router = APIRouter(prefix="/users", tags=["Users"])


@router.post(
    "",
    response_model=UserOut,
    status_code=status.HTTP_201_CREATED,
    summary="Create a new user in Keycloak",
)
async def create_user_endpoint(
    user_in: UserCreate,
    authorization: str = Header(..., alias="Authorization"),
):
    """
    Requires `Authorization: Bearer <access_token>` header.
    Body must be JSON matching UserCreate schema.
    """
    # extract raw token
    access_token = authorization.split(" ", 1)[1]

    return await create_user(user_in, access_token)


@router.get(
    "",
    response_model=List[UserOut],
    status_code=status.HTTP_200_OK,
    summary="List all users",
)
async def list_users_endpoint(
    enabled: bool | None = Query(default=None, description="Filter by enabled status"),
    authorization: str = Header(..., alias="Authorization"),
):
    """
    Requires `Authorization: Bearer <access_token>` header.
    """
    access_token = authorization.split(" ", 1)[1]
    return await get_users(access_token, enabled)


@router.get(
    "/{user_id}",
    response_model=UserOut,
    status_code=status.HTTP_200_OK,
    summary="Get user by id",
)
async def get_user_endpoint(
    user_id: str = Path(..., description="Keycloak user UUID"),
    authorization: str = Header(..., alias="Authorization"),
):
    """
    Requires `Authorization: Bearer <access_token>` header.
    """
    access_token = authorization.split(" ", 1)[1]
    return await get_user(user_id, access_token)


@router.put(
    "/{user_id}",
    status_code=status.HTTP_200_OK,
    summary="Update an existing user",
)
async def update_user_endpoint(
    user_id: str = Path(..., description="Keycloak user UUID"),
    patch: UserUpdate = ...,
    authorization: str = Header(..., alias="Authorization"),
):

    access_token = authorization.split(" ", 1)[1]
    await update_user(user_id, patch, access_token)
    return {}  # empty body, 200 OK


@router.patch(
    "/{user_id}",
    status_code=status.HTTP_200_OK,
    summary="Update user password",
)
async def update_password_endpoint(
    user_id: str = Path(..., description="Keycloak user UUID"),
    patch: PasswordUpdate = ...,
    authorization: str = Header(..., alias="Authorization"),
):

    access_token = authorization.split(" ", 1)[1]
    await update_password(user_id, patch, access_token)
    return {}  # empty body, 200 OK


@router.delete(
    "/{user_id}",
    status_code=status.HTTP_204_NO_CONTENT,
    summary="Logically delete (disable) a user",
)
async def delete_user_endpoint(
    user_id: str = Path(..., description="Keycloak user UUID"),
    authorization: str = Header(..., alias="Authorization"),
):

    access_token = authorization.split(" ", 1)[1]
    return await disable_user(user_id, access_token)


@router.post(
    "/{user_id}/roles/{role_id}",
    status_code=status.HTTP_204_NO_CONTENT,
    summary="Assign a client role to a user",
)
async def assign_role_endpoint(
    user_id: str = Path(..., description="User UUID"),
    role_id: str = Path(..., description="Role UUID"),
    authorization: str = Header(..., alias="Authorization"),
):

    access_token = authorization.split(" ", 1)[1]
    await assign_role(user_id, role_id, access_token)
    return Response(status_code=status.HTTP_204_NO_CONTENT)