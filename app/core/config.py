from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    keycloak_admin: str
    keycloak_admin_password: str
    keycloak_realm: str
    keycloak_client_id: str
    keycloak_client_secret: str = ""
    keycloak_base_url: str
    algorithm: str = "RS256"

    @property
    def token_url(self):
        return f"{self.keycloak_base_url}/realms/{self.keycloak_realm}/protocol/openid-connect/token"

    @property
    def jwks_url(self):
        return f"{self.keycloak_base_url}/realms/{self.keycloak_realm}/protocol/openid-connect/certs"

    @property
    def issuer(self):
        return f"{self.keycloak_base_url}/realms/{self.keycloak_realm}"

    class Config:
        env_file = ".env"

settings = Settings()
