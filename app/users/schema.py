from pydantic import BaseModel, Field, EmailStr, StringConstraints, constr
from typing import Annotated, Optional


class UserCreate(BaseModel):
    username: EmailStr
    password: Annotated[
        str,
        StringConstraints(min_length=6),
    ]
    first_name: str
    last_name: str


class UserOut(BaseModel):
    id: str
    username: str
    first_name: str
    last_name: str
    enabled: bool = True


class UserUpdate(BaseModel):
    username: Optional[EmailStr] = None
    first_name: Optional[str] = None
    last_name: Optional[str] = None
    enabled: Optional[bool] = None


class PasswordUpdate(BaseModel):
    password: Annotated[
        str,
        StringConstraints(min_length=6),
    ]
