package com.phmc.datamatcher;

import com.phmc.datamatcher.builder.IMatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.ServiceLoader;

public class Matcher {
    private static final String SUFIX = "MatcherImpl";

    private Matcher() {}

    public static <T> boolean matches(T o1, T o2) {
        IMatcher<T> matcher = loadMatcher(o1.getClass());
        return matcher.matches(o1, o2);
    }

    @SuppressWarnings( {"unchecked", "rawtypes"} )
    private static <T> IMatcher<T> loadMatcher(Class<?> objClass) {
        IMatcher<T> matcher = null;
        ClassLoader classLoader = objClass.getClassLoader();
        try {
            Class<IMatcher<T>> matcherClass = (Class<IMatcher<T>>) classLoader.loadClass(objClass.getName() + SUFIX);
            matcher = matcherClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            ServiceLoader<IMatcher> loader = ServiceLoader.load(IMatcher.class, classLoader);
            for ( IMatcher<T> m : loader ) {
                if ( m != null ) {
                    matcher = m;
                }
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return matcher;
    }
}
