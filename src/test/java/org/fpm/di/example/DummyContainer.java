package org.fpm.di.example;

import org.fpm.di.Container;

import java.lang.reflect.Constructor;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

public class DummyContainer implements Container {
    private final Map<Class<?>, Class<?>> binderMap;
    private final Map<Class<?>, Object> binderMapForInstance;
    public DummyContainer(){
        this.binderMap = new HashMap<>();
        this.binderMapForInstance = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")//Попередження компілятору про можливі неперевірені приведення типів
    public <T> T getComponent(Class<T> clazz) {
        Class<?> implementationClass = resolveImplementationClass(clazz);//Визначення класу реалізації для заданого інтерфейсу
        if (binderMapForInstance.containsKey(implementationClass))
            return (T) binderMapForInstance.get(implementationClass);//Повернення збереженого інстансу, якщо він вже був створений
        T instance = createInstance(implementationClass);
        if (implementationClass.isAnnotationPresent(Singleton.class))
            binderMapForInstance.put(implementationClass, instance);//Збереження інстансу, якщо він позначений анотацією Singleton
        return instance;
    }

    @SuppressWarnings("unchecked")
    private <T> T createInstance(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        List<Constructor<?>> constructorInjection = new ArrayList<>();//Список конструкторів, що мають анотацію Inject
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class))
                constructorInjection.add(constructor);//Додавання конструкторів з анотацією Inject до списку
        }
        if (!constructorInjection.isEmpty()) {
            for (Constructor<?> constructor : constructorInjection) {
                try {
                    Class<?>[] constructorArguments = constructor.getParameterTypes();
                    Object[] arguments = new Object[constructorArguments.length];
                    for (int i = 0; i < constructorArguments.length; i++) {
                        arguments[i] = getComponent(constructorArguments[i]);//Рекурсивний виклик, що дозволяє вирішити залежності та отримати масив
                    }
                    return (T) constructor.newInstance(arguments);//Створення інстансу за допомогою масиву залежностей
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            throw new CannotCreateException("No constructor could create " + clazz);
        } else {
            try {
                return (T) clazz.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Class<?> resolveImplementationClass(Class<?> interfaceClass) {
        for (Map.Entry<Class<?>, Class<?>> element : binderMap.entrySet()) {//Переведення мапи в сет для ітерації
            if (element.getKey() == interfaceClass)
                return element.getValue();//Повернення реалізації для заданого інтерфейсу
        }
        if (binderMapForInstance.containsKey(interfaceClass))
            return interfaceClass;//Повернення самого інтерфейсу, якщо він вже має збережений інстанс
        else
            throw new NoImplementationException("There isn't any implementation for " + interfaceClass.getName());
    }

    public Map<Class<?>, Class<?>> getBinderMap() {
        return binderMap;
    }

    public Map<Class<?>, Object> getBinderMapForInstance() {
        return binderMapForInstance;
    }
}
