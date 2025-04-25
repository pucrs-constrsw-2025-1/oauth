package org.firpy.oauth.utils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class WebApplicationResponseUtils
{
	@ExceptionHandler(WebApplicationException.class)
	public ResponseEntity<Object> toSpringResponseEntity(WebApplicationException exception)
	{
		return toSpringResponseEntity(exception.getResponse());
	}

	public static ResponseEntity<Object> toSpringResponseEntity(Response jaxRsResponse)
	{
		if (jaxRsResponse == null)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					       .body("JAX-RS exception with no response");
		}

		int status = jaxRsResponse.getStatus();

		MultivaluedMap<String, Object> jaxRsHeaders = jaxRsResponse.getHeaders();
		HttpHeaders springHeaders = new HttpHeaders();
		for (Map.Entry<String, List<Object>> entry : jaxRsHeaders.entrySet())
		{
			for (Object value : jaxRsHeaders.get(entry.getKey()))
			{
				springHeaders.add(entry.getKey(), value.toString());
			}
		}

		Object entity = jaxRsResponse.getEntity();

		if (entity instanceof InputStream inputStream)
		{
			byte[] data = readAllBytes(inputStream);
			return new ResponseEntity<>(new ByteArrayResource(data), springHeaders, HttpStatus.valueOf(status));
		}

		try
		{
			Class<?> builtResponseClass = Class.forName("org.jboss.resteasy.specimpl.BuiltResponse");
			if (builtResponseClass.isInstance(entity))
			{
				Object inner = builtResponseClass.getMethod("getEntity").invoke(entity);
				if (inner instanceof InputStream inputStream)
				{
					byte[] data = readAllBytes(inputStream);
					return new ResponseEntity<>(new ByteArrayResource(data), springHeaders, HttpStatus.valueOf(status));
				}
				else
				{
					return new ResponseEntity<>(inner, springHeaders, HttpStatus.valueOf(status));
				}
			}
		}
		catch (Exception ignored)
		{
			// Fall through to default handler
		}

		// Default fallback
		return new ResponseEntity<>(entity, springHeaders, HttpStatus.valueOf(status));
	}

	private static byte[] readAllBytes(InputStream inputStream)
	{
		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream())
		{
			inputStream.transferTo(buffer);
			return buffer.toByteArray();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to read InputStream from JAX-RS response", e);
		}
	}
}
