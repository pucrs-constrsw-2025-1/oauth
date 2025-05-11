from fastapi import APIRouter, Depends, Header, HTTPException, status
from app.roles.schema import RoleCreate, RoleOut
from app.roles.service import create_role

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
