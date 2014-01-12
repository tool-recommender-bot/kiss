package kiss.lang.type;

import kiss.lang.Type;

/**
 * The type of a specific non-null value
 * 
 * @author Mike
 *
 * @param <T>
 */
public class Value<T> extends Type {
	private final T value;
	private final Class<T> klass;
	
	private Value(T value) {
		this.value=value;
		this.klass=(Class<T>) value.getClass();
	}
	
	public static <T> Type create(T value) {
		if (value==null) return Null.INSTANCE;
		return new Value<T>(value);
	}
	
	@Override
	public boolean checkInstance(Object o) {
		return value.equals(o);
	}
	
	@Override
	public Class<T> getJavaType() {
		return klass;
	}
	@Override
	public boolean canBeNull() {
		return false;
	}
	
	@Override
	public boolean cantBeNull() {
		return true;
	}
	
	@Override
	public boolean canBeTruthy() {
		return value!=Boolean.FALSE;
	}
	
	@Override
	public boolean canBeFalsey() {
		return value==Boolean.FALSE;
	}
	
	@Override
	public boolean contains(Type t) {
		if (t==this) return true;
		if (t instanceof Nothing) return true;
		if (t instanceof Value) {
			Value<?> ev=(Value<?>) t;
			if (ev.klass!=this.klass) return false;
			return ev.value.equals(this.value);
		}
		return false;
	}
	
	@Override
	public Type intersection(Type t) {
		if (t.checkInstance(value)) return this;
		return Nothing.INSTANCE;
	}

	@Override
	public Type inverse() {
		return Not.createNew(this);
	}

	@Override
	public Type union(Type t) {
		if (t==this) return t;
		if (t.checkInstance(value)) return t;
		if (t instanceof Value) {
			if (((Value<?>)t).value.equals(this.value)) return this;
		}
		return super.union(t);
	}
	
	@Override
	public String toString() {
		return "(Value "+value.toString()+")";
	}

}