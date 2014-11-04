package com.t3.model.tokenproperties.instance;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.t3.model.tokenproperties.PropertyType;
import com.t3.model.tokenproperties.valuetypes.ValueType;
import com.t3.xstreamversioned.version.SerializationVersion;

/**This is an immutable list of TokenProperty instances*/
@SerializationVersion(0)
public class TokenPropertiesList<T> implements Iterable<T> {
	
	private final Object[] 		elements;

	public TokenPropertiesList(PropertyType type) {
		this(type, new Object[0]);
	}
	
	public TokenPropertiesList(PropertyType type, List<T> elements) {
		this(type, elements.toArray());
	}
	
	private TokenPropertiesList(PropertyType type, Object[] elements) {
		this.type=type.getSignature();
		this.resolvedType=type;
		this.elements=elements;
	}
	
	private TokenPropertiesList(PropertyTypeSignature type, Object[] elements) {
		this.type=type;
		this.elements=elements;
	}
	
	@Override
	public String toString() {
		if(type!=null)
			return type.toString()+"s"+Arrays.toString(elements);
		else
			return Arrays.toString(elements);
	}
	
	public int size() {
		return elements.length;
	}
	
	@SuppressWarnings("unchecked")
	public T get(int i) {
		return (T)elements[i];
	}
	
	public TokenPropertiesList<T> withAdded(T tp) {
		if(!this.getType().isInstance(tp))
			throw new IllegalArgumentException("Was expepcting an element of type "+type.getType().getSimpleName()+", but got "+tp.getClass().getSimpleName());
		return new TokenPropertiesList<T>(type, ArrayUtils.add(elements, tp));
	}

	private ValueType getType() {
		if(resolvedType==null)
			resolvedType=type.getType();
		return resolvedType.getType();
	}

	@Override
	public Iterator<T> iterator() {
		return new TokenPropertyIterator();
	}
	
	private class TokenPropertyIterator implements Iterator<T> {

		private int i=0;
		
		@Override
		public boolean hasNext() {
			return i<elements.length;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			return (T)elements[i++];
		}
	}

	public boolean isEmpty() {
		return elements.length==0;
	}

	public TokenPropertiesList<T> without(int index) {
		return new TokenPropertiesList<>(type, ArrayUtils.remove(elements, index));
	}
}
