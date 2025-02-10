package urdego.io.urdego_notification_service.controller.util;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReflectionUtil {
    private static final Map<Class<?>, Method> methodCache = new ConcurrentHashMap<>();
    private ReflectionUtil(){}

    public static String getRoomIdFromResponse(Object response) {
        if (response == null) {
            throw new IllegalArgumentException("응답 객체가 null입니다.");
        }

        try {
            Method method = methodCache.computeIfAbsent(response.getClass(), clazz -> {
                try {
                    return clazz.getMethod("roomId");
                } catch (NoSuchMethodException e) {
                    throw new IllegalArgumentException("roomId를 찾을 수 없는 응답 타입: " + clazz.getSimpleName(), e);
                }
            });

            return (String) method.invoke(response);

        } catch (Exception e) {
            throw new IllegalArgumentException("roomId 호출 실패: " + response.getClass().getSimpleName(), e);
        }
    }
}
