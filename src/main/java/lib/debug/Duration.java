package lib.debug;

import lib.debug.Functions.Func0;
import lib.debug.Functions.Func1;
import lib.debug.Functions.Func10;
import lib.debug.Functions.Func11;
import lib.debug.Functions.Func12;
import lib.debug.Functions.Func13;
import lib.debug.Functions.Func14;
import lib.debug.Functions.Func15;
import lib.debug.Functions.Func2;
import lib.debug.Functions.Func3;
import lib.debug.Functions.Func4;
import lib.debug.Functions.Func5;
import lib.debug.Functions.Func6;
import lib.debug.Functions.Func7;
import lib.debug.Functions.Func8;
import lib.debug.Functions.Func9;

public class Duration {

	public static <R> TimeResult<R> of(Func0<R> func) {
		return wrap(func);
	}
	
	public static <T,R> TimeResult<R> of(Func1<T,R> func, T p) {
		return wrap(() -> func.invoke(p));
	}
	
	public static <T1,T2,R> TimeResult<R> of(Func2<T1,T2,R> func, T1 p1, T2 p2) {
		return wrap(() -> func.invoke(p1, p2));
	}
	
	public static <T1,T2,T3,R> TimeResult<R> of(Func3<T1,T2,T3,R> func, T1 p1, T2 p2, T3 p3) {
		return wrap(() -> func.invoke(p1, p2, p3));
	}
	
	public static <T1,T2,T3,T4,R> TimeResult<R> of(Func4<T1,T2,T3,T4,R> func, T1 p1, T2 p2, T3 p3, T4 p4) {
		return wrap(() -> func.invoke(p1, p2, p3, p4));
	}
	
	public static <T1,T2,T3,T4,T5,R> TimeResult<R> of(Func5<T1,T2,T3,T4,T5,R> func, T1 p1, T2 p2, T3 p3, T4 p4, T5 p5) {
		return wrap(() -> func.invoke(p1, p2, p3, p4, p5));
	}
	
	public static <T1,T2,T3,T4,T5,T6,R> TimeResult<R> of(Func6<T1,T2,T3,T4,T5,T6,R> func, T1 p1, T2 p2, T3 p3, T4 p4, T5 p5, T6 p6) {
		return wrap(() -> func.invoke(p1, p2, p3, p4, p5, p6));
	}
	
	public static <T1,T2,T3,T4,T5,T6,T7,R> TimeResult<R> of(Func7<T1,T2,T3,T4,T5,T6,T7,R> func, T1 p1, T2 p2, T3 p3, T4 p4, T5 p5, T6 p6, T7 p7) {
		return wrap(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7));
	}
	
	public static <T1,T2,T3,T4,T5,T6,T7,T8,R> TimeResult<R> of(Func8<T1,T2,T3,T4,T5,T6,T7,T8,R> func, T1 p1, T2 p2, T3 p3, T4 p4, T5 p5, T6 p6, T7 p7, T8 p8) {
		return wrap(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8));
	}
	
	public static <T1,T2,T3,T4,T5,T6,T7,T8,T9,R> TimeResult<R> of(Func9<T1,T2,T3,T4,T5,T6,T7,T8,T9,R> func, T1 p1, T2 p2, T3 p3, T4 p4, T5 p5, T6 p6, T7 p7, T8 p8, T9 p9) {
		return wrap(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8, p9));
	}
	
	public static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R> TimeResult<R> of(Func10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R> func, T1 p1, T2 p2, T3 p3, T4 p4, T5 p5, T6 p6, T7 p7, T8 p8, T9 p9, T10 p10) {
		return wrap(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10));
	}
	
	public static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R> TimeResult<R> of(Func11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R> func, T1 p1, T2 p2, T3 p3, T4 p4, T5 p5, T6 p6, T7 p7, T8 p8, T9 p9, T10 p10, T11 p11) {
		return wrap(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11));
	}
	
	public static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R> TimeResult<R> of(Func12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R> func, T1 p1, T2 p2, T3 p3, T4 p4, T5 p5, T6 p6, T7 p7, T8 p8, T9 p9, T10 p10, T11 p11, T12 p12) {
		return wrap(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12));
	}
	
	public static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,R> TimeResult<R> of(Func13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,R> func, T1 p1, T2 p2, T3 p3, T4 p4, T5 p5, T6 p6, T7 p7, T8 p8, T9 p9, T10 p10, T11 p11, T12 p12, T13 p13) {
		return wrap(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13));
	}
	
	public static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,R> TimeResult<R> of(Func14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,R> func, T1 p1, T2 p2, T3 p3, T4 p4, T5 p5, T6 p6, T7 p7, T8 p8, T9 p9, T10 p10, T11 p11, T12 p12, T13 p13, T14 p14) {
		return wrap(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14));
	}
	
	public static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,R> TimeResult<R> of(Func15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,R> func, T1 p1, T2 p2, T3 p3, T4 p4, T5 p5, T6 p6, T7 p7, T8 p8, T9 p9, T10 p10, T11 p11, T12 p12, T13 p13, T14 p14, T15 p15) {
		return wrap(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15));
	}
	
	private static <R> TimeResult<R> wrap(Func0<R> func) {
		return new Time().forResult(func::invoke);
	}
	
	public static class TimeResult<R> {
		public final long duration;
		public final R result;
		
		public TimeResult(R result, long duration) {
			this.duration = duration;
			this.result = result;
		}
		
		@Override
		public String toString() {
			return String.format("Got '%s' in %d ms", result, duration);
		}
	}
	
	public static class Time {
		public final long start = System.currentTimeMillis();
		
		public long untilNow() {
			return System.currentTimeMillis() - start;
		}
		
		public <R> TimeResult<R> forResult(Func0<R> func) {
			return new TimeResult<>(func.invoke(), untilNow());
		}
	}
}