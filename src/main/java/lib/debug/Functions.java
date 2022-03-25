package lib.debug;

import java.util.function.Function;

/* Follows C#-style generics to create lambdas 
 * for any kind of function (up to 15 arguments) */
public abstract class Functions {
	
	@FunctionalInterface
	public static interface Func0<R> {
		R invoke();
	}
	
	/* Since Func1 is the same as the Function<T,R> 
	 * from std lib, we simply reuse to make Func1 
	 * compatible with native code					*/
	@FunctionalInterface
	public static interface Func1<T,R> extends Function<T, R> {
		default R invoke(T t1) {
			return apply(t1);
		}
	}
	
	@FunctionalInterface
	public static interface Func2<T1,T2,R> {
		R invoke(T1 t1, T2 t2);
	}
	
	@FunctionalInterface
	public static interface Func3<T1,T2,T3,R> {
		R invoke(T1 t1, T2 t2, T3 t3);
	}
	
	@FunctionalInterface
	public static interface Func4<T1,T2,T3,T4,R> {
		R invoke(T1 t1, T2 t2, T3 t3, T4 t4);
	}
	
	@FunctionalInterface
	public static interface Func5<T1,T2,T3,T4,T5,R> {
		R invoke(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
	}
	
	@FunctionalInterface
	public static interface Func6<T1,T2,T3,T4,T5,T6,R> {
		R invoke(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);
	}
	
	@FunctionalInterface
	public static interface Func7<T1,T2,T3,T4,T5,T6,T7,R> {
		R invoke(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);
	}
	
	@FunctionalInterface
	public static interface Func8<T1,T2,T3,T4,T5,T6,T7,T8,R> {
		R invoke(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);
	}
	
	@FunctionalInterface
	public static interface Func9<T1,T2,T3,T4,T5,T6,T7,T8,T9,R> {
		R invoke(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);
	}
	
	@FunctionalInterface
	public static interface Func10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R> {
		R invoke(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);
	}
	
	@FunctionalInterface
	public static interface Func11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R> {
		R invoke(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);
	}
	
	@FunctionalInterface
	public static interface Func12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R> {
		R invoke(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);
	}
	
	@FunctionalInterface
	public static interface Func13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,R> {
		R invoke(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13);
	}
	
	@FunctionalInterface
	public static interface Func14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,R> {
		R invoke(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14);
	}
	
	@FunctionalInterface
	public static interface Func15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,R> {
		R invoke(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15);
	}
}
