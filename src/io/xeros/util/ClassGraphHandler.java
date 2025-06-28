package io.xeros.util;

import io.github.classgraph.*;
import io.xeros.Configuration;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.*;

/**
 * @param <T>
 * @Author: Origin
 * @Date: 5/23/24
 */
public class ClassGraphHandler<T> {

    public final ExecutorService service = Executors.newFixedThreadPool(2);
    public final ClassGraph classes = new ClassGraph().enableClassInfo().enableExternalClasses();
    public final Future<ScanResult> async = classes.scanAsync(service, 2);

    @SneakyThrows
    public final ClassGraphHandler<T> submit(final Class<T> instance, final List<T> tList, final LoadType loadType) throws RuntimeException {
        return switch (loadType) {
            case CLASS_ENTRY -> loadClassEntry(instance, tList);
            case CLASS_ENTRY_FILTERED -> loadClassEntryFiltered(instance, tList);
            case SUBCLASS_ENTRY -> loadSubClassEntry(instance, tList);
        };
    }

    @SneakyThrows
    final ClassGraphHandler<T> loadClassEntry(final Class<T> instance, final List<T> tList) throws RuntimeException {
        try (final ScanResult scanResult = async.get()) {
            final List<Class<T>> cached = scanResult.getClassesImplementing(instance).loadClasses(instance);
            for (Class<T> entry : cached) accept(entry, tList);
        } catch (ExecutionException e) {
            handleException(instance, e);
            throw new RuntimeException(e);
        } finally {
            async.get().close();
        }
        return this;
    }

    @SneakyThrows
    final ClassGraphHandler<T> loadClassEntryFiltered(final Class<T> instance, final List<T> tList) throws RuntimeException {
        try (final ScanResult scanResult = async.get()) {
            final List<Class<T>> cached = scanResult.getClassesImplementing(instance).loadClasses(instance);
            for (Class<T> entry : cached) acceptFiltered(entry, tList);
        } catch (ExecutionException e) {
            handleException(instance, e);
            throw new RuntimeException(e);
        } finally {
            async.get().close();
        }
        return this;
    }

    @SneakyThrows
    final ClassGraphHandler<T> loadSubClassEntry(final Class<T> instance, final List<T> tList) throws RuntimeException {
        try (final ScanResult scanResult = async.get()) {
            final List<Class<T>> cached = scanResult.getSubclasses(instance).loadClasses(instance);
            for (Class<T> entry : cached) acceptFiltered(entry, tList);
        } catch (ExecutionException e) {
            handleException(instance, e);
            throw new RuntimeException(e);
        } finally {
            async.get().close();
        }
        return this;
    }

    final void acceptFiltered(Class<T> type, List<T> tList) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!(type.isInterface() || Modifier.isAbstract(type.getModifiers()))) {
            final Constructor<T> constructor = type.getDeclaredConstructor();
            if (!Modifier.isPublic(constructor.getModifiers())) constructor.setAccessible(true);
            tList.add(constructor.newInstance());
        }
    }

    final void accept(Class<T> type, List<T> tList) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final Constructor<T> constructor = type.getDeclaredConstructor();
        if (!Modifier.isPublic(constructor.getModifiers())) constructor.setAccessible(true);
        tList.add(constructor.newInstance());
    }

    final void handleException(Class<T> clazz, Exception e) {
        System.err.println("Error loading class " + clazz.getName() + ": " + e.getMessage());
    }

    public void shutdown() {
        service.shutdown();
        try {
            if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                service.shutdownNow();
                if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Thread pool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread pool shutdown interrupted: " + e.getMessage());
        }
    }

    public enum LoadType {
        CLASS_ENTRY,
        CLASS_ENTRY_FILTERED,
        SUBCLASS_ENTRY
    }
}
