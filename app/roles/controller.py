from typing import List
from fastapi import APIRouter, Header, Path, status
from app.roles.schema import RoleCreate, RoleOut
from app.roles.service import create_role, get_role, get_roles

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
    # if "admin" not in token_payload["realm_access"]["roles"]:
    #     raise HTTPException(status_code=403, detail="Forbidden")

    access_token = authorization.split(" ", 1)[1]
    return await get_role(role_id, access_token)
