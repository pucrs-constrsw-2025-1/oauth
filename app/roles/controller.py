from typing import List
from fastapi import APIRouter, Header, Path, Response, status
from app.roles.schema import RoleCreate, RoleOut, RoleUpdateFull, RoleUpdatePartial
from app.roles.service import (
    create_role,
    delete_role,
    get_role,
    get_roles,
    patch_role,
    update_role,
)

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


@router.get(
    "/{role_id}",
    response_model=RoleOut,
    status_code=status.HTTP_200_OK,
    summary="Get a client role by id",
)
async def get_role_endpoint(
    role_id: str = Path(..., description="Role UUID"),
    authorization: str = Header(..., alias="Authorization"),
):

    access_token = authorization.split(" ", 1)[1]
    return await get_role(role_id, access_token)


@router.put(
    "/{role_id}",
    status_code=status.HTTP_200_OK,
    summary="Update role name and description",
)
async def update_role_endpoint(
    role_id: str = Path(..., description="Role UUID"),
    upd: RoleUpdateFull = ...,
    authorization: str = Header(..., alias="Authorization"),
):

    access_token = authorization.split(" ", 1)[1]
    await update_role(role_id, upd, access_token)
    return {}  # empty body per spec


@router.patch(
    "/{role_id}",
    status_code=status.HTTP_200_OK,
    summary="Partially update role (name and/or description)",
)
async def patch_role_endpoint(
    role_id: str = Path(..., description="Role UUID"),
    patch: RoleUpdatePartial = ...,
    authorization: str = Header(..., alias="Authorization"),
):

    access_token = authorization.split(" ", 1)[1]
    await patch_role(role_id, patch, access_token)
    return {}  # empty body, 200 OK


@router.delete(
    "/{role_id}",
    status_code=status.HTTP_204_NO_CONTENT,
    summary="Delete a role",
)
async def delete_role_endpoint(
    role_id: str = Path(..., description="Role UUID"),
    authorization: str = Header(..., alias="Authorization"),
):

    access_token = authorization.split(" ", 1)[1]
    await delete_role(role_id, access_token)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
