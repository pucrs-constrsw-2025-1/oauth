import logging
import httpx

from fastapi import Depends, HTTPException
from fastapi.security import OAuth2PasswordBearer
from jose import jwt, JWTError

from app.core.config import settings

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

# this caches the public keys in memory TODO: check if this is standard?
_jwks_cache = {}

def get_public_key(kid: str) -> dict:
    global _jwks_cache

    # if cached, return it
    if kid in _jwks_cache:
        return _jwks_cache[kid]

    jwks_url = settings.jwks_url
    response = httpx.get(jwks_url)
    response.raise_for_status()

    keys = response.json()["keys"]

    for key in keys:
        if key["kid"] == kid:
            _jwks_cache[kid] = key
            return key

    raise HTTPException(status_code=401, detail="Public key not found")

def verify_token(token: str = Depends(oauth2_scheme)):
    try:
        logging.info("Verifying token...")
        # decode the req header to get key ID (kid)
        unverified_header = jwt.get_unverified_header(token)
        key = get_public_key(unverified_header["kid"])

        payload = jwt.decode(
            token,
            key=key,
            algorithms=[settings.algorithm],
            issuer=settings.issuer,
        )
        return payload

    except JWTError as e:
        raise HTTPException(status_code=401, detail="Invalid token")
