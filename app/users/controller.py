from fastapi import APIRouter, Depends
from app.auth.service import verify_token

router = APIRouter(prefix="users", tags=["Users"])

@router.get("/profile")
def get_profile(user=Depends(verify_token)):
    return {"message": "You're Authenticated!", "user": user}
