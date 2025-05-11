from typing import List
from fastapi import APIRouter, Header, status
from app.roles.schema import RoleCreate, RoleOut
from app.roles.service import create_role, get_roles

router = APIRouter(prefix="/roles", tags=["Roles"])


@router.post(
    "",
    response_model=RoleOut,
    status_code=status.HTTP_201_CREATED,
    summary="Create a new client role",
)
async def create_role_endpoint(
    role_in: RoleCreate,
    authorization: str = Header(..., alias="Authorization"),
):

    access_token = authorization.split(" ", 1)[1]
    return await create_role(role_in, access_token)


@router.get(
    "",
    response_model=List[RoleOut],
    status_code=status.HTTP_200_OK,
    summary="List all client roles",
)
async def list_roles_endpoint(
    authorization: str = Header(..., alias="Authorization"),
):

    access_token = authorization.split(" ", 1)[1]
    return await get_roles(access_token)
