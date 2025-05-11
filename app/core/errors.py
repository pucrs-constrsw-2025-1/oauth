from fastapi import HTTPException, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from starlette import status
import traceback
import uuid

ERROR_SOURCE = "OAuthAPI"          # change per micro‑service

def _build_payload(
    status_code: int,
    description: str,
    stack: list[dict] | None = None,
) -> dict:
    return {
        "error_code": f"OA-{status_code}",
        "error_description": description,
        "error_source": ERROR_SOURCE,
        "error_stack": stack or [],
    }


async def http_exception_handler(request: Request, exc: HTTPException):
    payload = _build_payload(exc.status_code, exc.detail)
    return JSONResponse(status_code=exc.status_code, content=payload)


async def validation_exception_handler(request: Request, exc: RequestValidationError):
    payload = _build_payload(
        status.HTTP_400_BAD_REQUEST,
        "Validation error",
        stack=exc.errors(),      # Pydantic already returns a nice list
    )
    return JSONResponse(status_code=status.HTTP_400_BAD_REQUEST, content=payload)


async def unhandled_exception_handler(request: Request, exc: Exception):
    tb = traceback.format_exception(exc.__class__, exc, exc.__traceback__)
    payload = _build_payload(
        status.HTTP_500_INTERNAL_SERVER_ERROR,
        "Internal server error",
        stack=[{"trace": tb}],
    )
    return JSONResponse(status_code=500, content=payload)
