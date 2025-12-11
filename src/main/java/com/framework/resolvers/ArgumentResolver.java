package com.framework.resolvers;

import java.lang.reflect.Parameter;

public interface ArgumentResolver {
    boolean supports(Parameter parameter);
    Object resolve(Parameter parameter);
}
