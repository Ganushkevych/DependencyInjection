package org.fpm.di.example;

import org.fpm.di.Binder;

import javax.management.openmbean.KeyAlreadyExistsException;//імпорт ексепшна
import java.util.Map;

public class DummyBinder implements Binder/*інтерфейс для зв'язування*/ {
    private final Map<Class<?>, Class<?>> binderMap;//Class<?> - загальний тип даних
    private final Map<Class<?>, Object> binderMapForInstance;
    public DummyBinder(Map<Class<?>, Class<?>> binderMap,Map<Class<?>, Object> binderMapForInstance){
        this.binderMap = binderMap;
        this.binderMapForInstance = binderMapForInstance;
    }
    @Override
    public <T> void bind(Class<T> clazz) {//generic метод, що дозволяє взяти клас будь-якого типу та зв'язати його з контейнером
        containCheck(binderMap,binderMapForInstance,clazz);
        binderMap.put(clazz,clazz);//зв'язування
    }

    @Override
    public <T> void bind(Class<T> clazz, Class<? extends T> implementation) {//...конкретною реалізацією в контейнері
        containCheck(binderMap,binderMapForInstance,clazz);
        binderMap.put(clazz,implementation);
    }

    @Override
    public <T> void bind(Class<T> clazz, T instance) {//...конкретним інстансом в контейнері
        containCheck(binderMap,binderMapForInstance,clazz);
        binderMapForInstance.put(clazz,instance);
    }

    private void containCheck(Map<Class<?>, Class<?>> binderMap,Map<Class<?>, Object> binderMapForInstance, Class<?> clazz){
        if(binderMap.containsKey(clazz)||binderMapForInstance.containsKey(clazz))
            throw new KeyAlreadyExistsException("Implementation for class: "+clazz+" already exist");
    }
}
