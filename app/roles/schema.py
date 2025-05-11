from pydantic import BaseModel, StringConstraints
from typing import Annotated


class RoleCreate(BaseModel):
    name: Annotated[str, StringConstraints(strip_whitespace=True, min_length=1)]
    description: str | None = None


class RoleOut(BaseModel):
    id: str
    name: str
    description: str | None = None
    client_role: bool = True
