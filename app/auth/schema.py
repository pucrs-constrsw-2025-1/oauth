from pydantic import BaseModel, Field


class LoginRequest(BaseModel):
    username: str
    password: str

class LoginResponse(BaseModel):
    token_type: str
    access_token: str = Field(alias="access_token")
    expires_in: int
    refresh_token: str | None = None
    refresh_expires_in: int | None = Field(default=None, alias="refresh_expires_in")