package json;
import java.io.Serializable;

public final class ExchangeRate implements Serializable {
	private static final long serialVersionUID = 1L;
	public String date;
	public Object val;
	public OBJECT_PLACEHOLDER0[] arr;
	public double vald;
	public boolean valb;
	public Rates rates;
	public String base;
	public int vali;

	public ExchangeRate(String date, Object val, OBJECT_PLACEHOLDER0[] arr, double vald, boolean valb, Rates rates, String base, int vali) {
		this.date = date;
		this.val = val;
		this.arr = arr;
		this.vald = vald;
		this.valb = valb;
		this.rates = rates;
		this.base = base;
		this.vali = vali;
	}

	public String getDate() {
		return date;
	}

	public Object getVal() {
		return val;
	}

	public OBJECT_PLACEHOLDER0[] getArr() {
		return arr;
	}

	public double getVald() {
		return vald;
	}

	public boolean getValb() {
		return valb;
	}

	public Rates getRates() {
		return rates;
	}

	public String getBase() {
		return base;
	}

	public int getVali() {
		return vali;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setVal(Object val) {
		this.val = val;
	}

	public void setArr(OBJECT_PLACEHOLDER0[] arr) {
		this.arr = arr;
	}

	public void setVald(double vald) {
		this.vald = vald;
	}

	public void setValb(boolean valb) {
		this.valb = valb;
	}

	public void setRates(Rates rates) {
		this.rates = rates;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public void setVali(int vali) {
		this.vali = vali;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(getClass().getName()).append("{\n")
			.append("date: ").append(date+",\n")
			.append("val: ").append(val+",\n")
			.append("arr: ").append(arr+",\n")
			.append("vald: ").append(vald+",\n")
			.append("valb: ").append(valb+",\n")
			.append("rates: ").append(rates+",\n")
			.append("base: ").append(base+",\n")
			.append("vali: ").append(vali+",\n")
			.append("\n}").toString();
	}

	public static class Builder {
		public String date;
		public Object val;
		public OBJECT_PLACEHOLDER0[] arr;
		public double vald;
		public boolean valb;
		public Rates rates;
		public String base;
		public int vali;

		public Builder setDate(String date) {
			this.date = date;
			return this;
		}

		public Builder setVal(Object val) {
			this.val = val;
			return this;
		}

		public Builder setArr(OBJECT_PLACEHOLDER0[] arr) {
			this.arr = arr;
			return this;
		}

		public Builder setVald(double vald) {
			this.vald = vald;
			return this;
		}

		public Builder setValb(boolean valb) {
			this.valb = valb;
			return this;
		}

		public Builder setRates(Rates rates) {
			this.rates = rates;
			return this;
		}

		public Builder setBase(String base) {
			this.base = base;
			return this;
		}

		public Builder setVali(int vali) {
			this.vali = vali;
			return this;
		}

		public ExchangeRate build() {
			return new ExchangeRate(date,val,arr,vald,valb,rates,base,vali);
		}
	}
}

final class OBJECT_PLACEHOLDER0 implements Serializable {
	private static final long serialVersionUID = 1L;
	public String el;

	public OBJECT_PLACEHOLDER0(String el) {
		this.el = el;
	}

	public String getEl() {
		return el;
	}

	public void setEl(String el) {
		this.el = el;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(getClass().getName()).append("{\n")
			.append("el: ").append(el+",\n")
			.append("\n}").toString();
	}

	public static class Builder {
		public String el;

		public Builder setEl(String el) {
			this.el = el;
			return this;
		}

		public OBJECT_PLACEHOLDER0 build() {
			return new OBJECT_PLACEHOLDER0(el);
		}
	}
}

final class Rates implements Serializable {
	private static final long serialVersionUID = 1L;
	public double HKD;
	public double GBP;

	public Rates(double HKD, double GBP) {
		this.HKD = HKD;
		this.GBP = GBP;
	}

	public double getHKD() {
		return HKD;
	}

	public double getGBP() {
		return GBP;
	}

	public void setHKD(double HKD) {
		this.HKD = HKD;
	}

	public void setGBP(double GBP) {
		this.GBP = GBP;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(getClass().getName()).append("{\n")
			.append("HKD: ").append(HKD+",\n")
			.append("GBP: ").append(GBP+",\n")
			.append("\n}").toString();
	}

	public static class Builder {
		public double HKD;
		public double GBP;

		public Builder setHKD(double HKD) {
			this.HKD = HKD;
			return this;
		}

		public Builder setGBP(double GBP) {
			this.GBP = GBP;
			return this;
		}

		public Rates build() {
			return new Rates(HKD,GBP);
		}
	}
}