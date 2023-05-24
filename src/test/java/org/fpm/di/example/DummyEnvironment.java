package org.fpm.di.example;

import org.fpm.di.Binder;
import org.fpm.di.Configuration;
import org.fpm.di.Container;
import org.fpm.di.Environment;

public class DummyEnvironment implements Environment {//Створення і конфігурація контейнера залежностей на основі заданої конфігурації
    @Override
    public Container configure(Configuration configuration) {
        DummyContainer container = new DummyContainer();
        Binder binder = new DummyBinder(container.getBinderMap(),container.getBinderMapForInstance());
        configuration.configure(binder);
        return container;
    }
}
