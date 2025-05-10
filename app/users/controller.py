from fastapi import APIRouter, Depends, HTTPException, Query, status, Header
from typing import List
from app.auth.service import verify_token
from app.users.schema import UserCreate, UserOut
from app.users.service import create_user, get_users


router = APIRouter(prefix="/users", tags=["Users"])


@router.post(
    "",
    response_model=UserOut,
    status_code=status.HTTP_201_CREATED,
    summary="Create a new user in Keycloak",
)
async def create_user_endpoint(
    user_in: UserCreate,
    token_payload=Depends(verify_token),
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
    token_payload=Depends(verify_token),
    authorization: str = Header(..., alias="Authorization"),
):

    access_token = authorization.split(" ", 1)[1]
    return await get_users(access_token, enabled)
