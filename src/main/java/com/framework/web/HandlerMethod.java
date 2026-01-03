package com.framework.web;

import java.lang.reflect.Method;

public record HandlerMethod(Object controller, Method method) {
}
