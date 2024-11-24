package com.school_1.api;

import com.school_1.api.Commons.Exceptions.GlobalExceptionHandler;
import com.school_1.api.User.UserRouter;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class SchoolApp extends Application {
}