package kiss.lang.type;

import kiss.lang.KFn;
import kiss.lang.Mapping;
import kiss.lang.Type;
import clojure.lang.IFn;

/**
 * Type representing a fixed arity function
 * 
 * Covers both Kiss and Clojure functions
 * 
 * @author Mike
 *
 */
public class FunctionType extends Type {

	private final Type returnType;
	private final Type[] paramTypes;
	
	private FunctionType(Type returnType, Type[] paramTypes) {
		this.returnType=returnType;
		this.paramTypes=paramTypes;
	}
	
	public static FunctionType create(Type returnType, Mapping... params) {
		int n=params.length;
		Type[] ptypes=new Type[n];
		for (int i=0; i<n; i++) {
			ptypes[i]=params[i].getType();
		}
		return new FunctionType(returnType,ptypes);
	}
	
	public static FunctionType create(Type returnType, Type... types) {
		int n=types.length;
		Type[] ptypes=new Type[n];
		for (int i=0; i<n; i++) {
			ptypes[i]=types[i];
		}
		return new FunctionType(returnType,ptypes);
	}
	
	public boolean hasArity(int n) {
		// TODO: think about multi-arity?
		return n==getArity();
	}
	
	public int getArity() {
		return paramTypes.length;
	}
	
	private Type getReturnType() {
		return returnType;
	}

	@Override
	public boolean checkInstance(Object o) {
		if (o instanceof KFn) {
			KFn fn=(KFn)o;
			if (!(returnType.contains(fn.getReturnType()))) return false;
		} else {
			if (o instanceof IFn) return true;
		}
		return false;
	}

	@Override
	public Class<?> getJavaType() {
		return IFn.class;
	}

	@Override
	public boolean contains(Type t) {
		if (t==this) return true;
		
		if (t instanceof FunctionType) {
			FunctionType ft=(FunctionType)t;
			
			int n=getArity();
			if (ft.getArity()!=n) return false;
					
			if (!returnType.contains(ft.returnType)) return false;
			
			for (int i=0; i<n; i++) {
				if (!(ft.paramTypes[i].contains(paramTypes[i]))) return false;
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Type intersection(Type t) {
		if (t==this) return this;
		
		if (t instanceof FunctionType) {
			FunctionType ft=(FunctionType)t;
			if (ft.getArity()!=this.getArity()) return Nothing.INSTANCE;
			
			Type rt=getReturnType().intersection(ft.returnType);
			if (rt instanceof Nothing) return Nothing.INSTANCE;
		}
		return t.intersection(this);
	}

}