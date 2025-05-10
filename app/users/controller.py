from fastapi import APIRouter, Depends, HTTPException, status, Header
from app.auth.service import verify_token
from app.users.schema import UserCreate, UserOut
from app.users.service import create_user


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

    # role check
    if "admin" not in token_payload["realm_access"]["roles"]:
        # spec says 403 when token lacks permission
        raise HTTPException(status_code=403, detail="Forbidden")

    return await create_user(user_in, access_token)


@router.get("/profile")
def get_profile(user=Depends(verify_token)):
    return {"message": "You're Authenticated!", "user": user}
