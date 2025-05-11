from pydantic import BaseModel, StringConstraints
from typing import Annotated, Optional


class RoleCreate(BaseModel):
    name: Annotated[str, StringConstraints(strip_whitespace=True, min_length=1)]
    description: str | None = None


class RoleOut(BaseModel):
    id: str
    name: str
    description: str | None = None
    client_role: bool = True


class RoleUpdateFull(BaseModel):
    name: Annotated[str, StringConstraints(strip_whitespace=True, min_length=1)]
    description: str


class RoleUpdatePartial(BaseModel):
    name: Annotated[str, StringConstraints(strip_whitespace=True, min_length=1)] = None
    description: Optional[str] = None

    # at least one field must be present
    model_config = {"min_anystr_length": 1}

    @classmethod
    def validate(cls, data):
        if not data.get("name") and not data.get("description"):
            raise ValueError("Nothing to update")
        return super().model_validate(data)
