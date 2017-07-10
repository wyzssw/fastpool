package com.justdebugit.fastpool.pool;

/**
 * Wrapper for objects under management by the pool.
 *
 * GenericObjectPool and GenericKeyedObjectPool maintain references to all
 * objects under management using maps keyed on the objects. This wrapper
 * class ensures that objects can work as hash keys.
 *
 * @param <T> type of objects in the pool
 */
public class IdentityWrapper<T> {
  
  
      /** Wrapped object */
      private final T instance;
      
      /**
       * Create a wrapper for an instance.
       *
       * @param instance object to wrap
       */
      public IdentityWrapper(T instance) {
          this.instance = instance;
      }

      @Override
      public int hashCode() {
          return System.identityHashCode(instance);
      }

      @Override
      @SuppressWarnings("rawtypes")
      public boolean equals(Object other) {
          return ((IdentityWrapper) other).instance == instance;
      }
      
      /**
       * @return the wrapped object
       */
      public T getObject() {
          return instance;
      }

}
