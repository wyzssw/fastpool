package com.justdebugit.fastpool;

public interface ObjectFactory<V> {
	//object must not be null
	V  makeObject();
}
