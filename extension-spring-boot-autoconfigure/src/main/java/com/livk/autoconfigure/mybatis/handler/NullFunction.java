package com.livk.autoconfigure.mybatis.handler;

/**
 * <p>
 * NullFunction
 * </p>
 *
 * @author livk
 */
public class NullFunction implements FunctionHandle<Object> {

    @Override
    public Object handler() {
        return null;
    }

}
