package org.firpy.keycloakwrapper.config;

import feign.FeignException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FeignExceptionHandler
{
	@ExceptionHandler(FeignException.class)
	public void handleFeignStatusException(FeignException e, HttpServletResponse response)
	{
		response.setStatus(e.status());
	}
}
