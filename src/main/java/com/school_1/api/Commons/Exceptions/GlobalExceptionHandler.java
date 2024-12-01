package com.school_1.api.Commons.Exceptions;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.naming.AuthenticationException;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public Response toResponse(Throwable exception) {
        // Log the exception
        logger.error("Exception caught:", exception);

        // Handle specific exceptions
        if (exception instanceof DuplicationException) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorDTO(exception.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (exception instanceof UnauthorizedException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorDTO(exception.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (exception instanceof AccessDeniedException) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ErrorDTO(exception.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (exception instanceof  NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorDTO(exception.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (exception instanceof BadRequestException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDTO(exception.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (exception instanceof ConstraintViolationException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDTO("Validation failed"))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorDTO("An unexpected error occurred"))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}