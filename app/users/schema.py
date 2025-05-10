from pydantic import BaseModel, Field, EmailStr, StringConstraints, constr
from typing import Annotated


class UserCreate(BaseModel):
    username: EmailStr  # e‑mail
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
