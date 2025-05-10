from pathlib import Path
from pydantic_settings import BaseSettings, SettingsConfigDict
from functools import cached_property

BASE_DIR = Path(__file__).resolve().parents[3]


class Settings(BaseSettings):
    kc_health_enabled: bool = True  # KC_HEALTH_ENABLED
    keycloak_realm: str  # KEYCLOAK_REALM

    keycloak_internal_host: str  # KEYCLOAK_INTERNAL_HOST
    keycloak_internal_port: int  # KEYCLOAK_INTERNAL_PORT
    keycloak_external_host: str  # KEYCLOAK_EXTERNAL_HOST
    keycloak_external_port: int  # KEYCLOAK_EXTERNAL_PORT

    keycloak_admin: str  # KEYCLOAK_ADMIN
    keycloak_admin_password: str  # KEYCLOAK_ADMIN_PASSWORD

    keycloak_client_id: str  # KEYCLOAK_CLIENT_ID
    keycloak_client_secret: str = ""  # KEYCLOAK_CLIENT_SECRET
    keycloak_grant_type: str = "password"  # KEYCLOAK_GRANT_TYPE

    # generic
    algorithm: str = "RS256"

    use_internal_network: bool = True

    @cached_property
    def keycloak_base_url(self) -> str:
        if self.use_internal_network:
            return f"http://{self.keycloak_internal_host}:{self.keycloak_internal_port}"
        return f"http://{self.keycloak_external_host}:{self.keycloak_external_port}"

    @cached_property
    def token_url(self) -> str:
        return (
            f"{self.keycloak_base_url}/realms/{self.keycloak_realm}"
            "/protocol/openid-connect/token"
        )

    @cached_property
    def jwks_url(self) -> str:
        return (
            f"{self.keycloak_base_url}/realms/{self.keycloak_realm}"
            "/protocol/openid-connect/certs"
        )

    @cached_property
    def issuer(self) -> str:
        return f"{self.keycloak_base_url}/realms/{self.keycloak_realm}"

    # pydantic‑settings config
    model_config = SettingsConfigDict(
        env_file=BASE_DIR / ".env",
        env_prefix="",
        extra="ignore",
    )


settings = Settings()
