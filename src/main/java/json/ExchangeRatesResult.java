package json;

import java.io.Serializable;

public final class ExchangeRatesResult implements Serializable {
	private static final long serialVersionUID = 1L;
	public String date;
	public Rates rates;
	public String base;

	public ExchangeRatesResult(String date, Rates rates, String base) {
		this.date = date;
		this.rates = rates;
		this.base = base;
	}

	public String getDate() {
		return date;
	}

	public Rates getRates() {
		return rates;
	}

	public String getBase() {
		return base;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setRates(Rates rates) {
		this.rates = rates;
	}

	public void setBase(String base) {
		this.base = base;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(getClass().getName()).append("{\n")
			.append("date: ").append(date+",\n")
			.append("rates: ").append(rates+",\n")
			.append("base: ").append(base+",\n")
			.append("\n}").toString();
	}

	public static class Builder {
		public String date;
		public Rates rates;
		public String base;

		public Builder setDate(String date) {
			this.date = date;
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

		public ExchangeRatesResult build() {
			return new ExchangeRatesResult(date,rates,base);
		}
	}
	
	public static final class Rates implements Serializable {
		private static final long serialVersionUID = 1L;
		public double CHF;
		public double HRK;
		public double MXN;
		public double ZAR;
		public double INR;
		public double THB;
		public double CNY;
		public double AUD;
		public double ILS;
		public double KRW;
		public double JPY;
		public double PLN;
		public double GBP;
		public double IDR;
		public double HUF;
		public double PHP;
		public double TRY;
		public double RUB;
		public double HKD;
		public double ISK;
		public double DKK;
		public double MYR;
		public double CAD;
		public double USD;
		public double BGN;
		public double NOK;
		public double RON;
		public double SGD;
		public double CZK;
		public double SEK;
		public double NZD;
		public double BRL;

		public Rates(double CHF, double HRK, double MXN, double ZAR, double INR, double THB, double CNY, double AUD, double ILS, double KRW, double JPY, double PLN, double GBP, double IDR, double HUF, double PHP, double TRY, double RUB, double HKD, double ISK, double DKK, double MYR, double CAD, double USD, double BGN, double NOK, double RON, double SGD, double CZK, double SEK, double NZD, double BRL) {
			this.CHF = CHF;
			this.HRK = HRK;
			this.MXN = MXN;
			this.ZAR = ZAR;
			this.INR = INR;
			this.THB = THB;
			this.CNY = CNY;
			this.AUD = AUD;
			this.ILS = ILS;
			this.KRW = KRW;
			this.JPY = JPY;
			this.PLN = PLN;
			this.GBP = GBP;
			this.IDR = IDR;
			this.HUF = HUF;
			this.PHP = PHP;
			this.TRY = TRY;
			this.RUB = RUB;
			this.HKD = HKD;
			this.ISK = ISK;
			this.DKK = DKK;
			this.MYR = MYR;
			this.CAD = CAD;
			this.USD = USD;
			this.BGN = BGN;
			this.NOK = NOK;
			this.RON = RON;
			this.SGD = SGD;
			this.CZK = CZK;
			this.SEK = SEK;
			this.NZD = NZD;
			this.BRL = BRL;
		}

		public double getCHF() {
			return CHF;
		}

		public double getHRK() {
			return HRK;
		}

		public double getMXN() {
			return MXN;
		}

		public double getZAR() {
			return ZAR;
		}

		public double getINR() {
			return INR;
		}

		public double getTHB() {
			return THB;
		}

		public double getCNY() {
			return CNY;
		}

		public double getAUD() {
			return AUD;
		}

		public double getILS() {
			return ILS;
		}

		public double getKRW() {
			return KRW;
		}

		public double getJPY() {
			return JPY;
		}

		public double getPLN() {
			return PLN;
		}

		public double getGBP() {
			return GBP;
		}

		public double getIDR() {
			return IDR;
		}

		public double getHUF() {
			return HUF;
		}

		public double getPHP() {
			return PHP;
		}

		public double getTRY() {
			return TRY;
		}

		public double getRUB() {
			return RUB;
		}

		public double getHKD() {
			return HKD;
		}

		public double getISK() {
			return ISK;
		}

		public double getDKK() {
			return DKK;
		}

		public double getMYR() {
			return MYR;
		}

		public double getCAD() {
			return CAD;
		}

		public double getUSD() {
			return USD;
		}

		public double getBGN() {
			return BGN;
		}

		public double getNOK() {
			return NOK;
		}

		public double getRON() {
			return RON;
		}

		public double getSGD() {
			return SGD;
		}

		public double getCZK() {
			return CZK;
		}

		public double getSEK() {
			return SEK;
		}

		public double getNZD() {
			return NZD;
		}

		public double getBRL() {
			return BRL;
		}

		public void setCHF(double CHF) {
			this.CHF = CHF;
		}

		public void setHRK(double HRK) {
			this.HRK = HRK;
		}

		public void setMXN(double MXN) {
			this.MXN = MXN;
		}

		public void setZAR(double ZAR) {
			this.ZAR = ZAR;
		}

		public void setINR(double INR) {
			this.INR = INR;
		}

		public void setTHB(double THB) {
			this.THB = THB;
		}

		public void setCNY(double CNY) {
			this.CNY = CNY;
		}

		public void setAUD(double AUD) {
			this.AUD = AUD;
		}

		public void setILS(double ILS) {
			this.ILS = ILS;
		}

		public void setKRW(double KRW) {
			this.KRW = KRW;
		}

		public void setJPY(double JPY) {
			this.JPY = JPY;
		}

		public void setPLN(double PLN) {
			this.PLN = PLN;
		}

		public void setGBP(double GBP) {
			this.GBP = GBP;
		}

		public void setIDR(double IDR) {
			this.IDR = IDR;
		}

		public void setHUF(double HUF) {
			this.HUF = HUF;
		}

		public void setPHP(double PHP) {
			this.PHP = PHP;
		}

		public void setTRY(double TRY) {
			this.TRY = TRY;
		}

		public void setRUB(double RUB) {
			this.RUB = RUB;
		}

		public void setHKD(double HKD) {
			this.HKD = HKD;
		}

		public void setISK(double ISK) {
			this.ISK = ISK;
		}

		public void setDKK(double DKK) {
			this.DKK = DKK;
		}

		public void setMYR(double MYR) {
			this.MYR = MYR;
		}

		public void setCAD(double CAD) {
			this.CAD = CAD;
		}

		public void setUSD(double USD) {
			this.USD = USD;
		}

		public void setBGN(double BGN) {
			this.BGN = BGN;
		}

		public void setNOK(double NOK) {
			this.NOK = NOK;
		}

		public void setRON(double RON) {
			this.RON = RON;
		}

		public void setSGD(double SGD) {
			this.SGD = SGD;
		}

		public void setCZK(double CZK) {
			this.CZK = CZK;
		}

		public void setSEK(double SEK) {
			this.SEK = SEK;
		}

		public void setNZD(double NZD) {
			this.NZD = NZD;
		}

		public void setBRL(double BRL) {
			this.BRL = BRL;
		}

		@Override
		public String toString() {
			return new StringBuilder()
				.append(getClass().getName()).append("{\n")
				.append("CHF: ").append(CHF+",\n")
				.append("HRK: ").append(HRK+",\n")
				.append("MXN: ").append(MXN+",\n")
				.append("ZAR: ").append(ZAR+",\n")
				.append("INR: ").append(INR+",\n")
				.append("THB: ").append(THB+",\n")
				.append("CNY: ").append(CNY+",\n")
				.append("AUD: ").append(AUD+",\n")
				.append("ILS: ").append(ILS+",\n")
				.append("KRW: ").append(KRW+",\n")
				.append("JPY: ").append(JPY+",\n")
				.append("PLN: ").append(PLN+",\n")
				.append("GBP: ").append(GBP+",\n")
				.append("IDR: ").append(IDR+",\n")
				.append("HUF: ").append(HUF+",\n")
				.append("PHP: ").append(PHP+",\n")
				.append("TRY: ").append(TRY+",\n")
				.append("RUB: ").append(RUB+",\n")
				.append("HKD: ").append(HKD+",\n")
				.append("ISK: ").append(ISK+",\n")
				.append("DKK: ").append(DKK+",\n")
				.append("MYR: ").append(MYR+",\n")
				.append("CAD: ").append(CAD+",\n")
				.append("USD: ").append(USD+",\n")
				.append("BGN: ").append(BGN+",\n")
				.append("NOK: ").append(NOK+",\n")
				.append("RON: ").append(RON+",\n")
				.append("SGD: ").append(SGD+",\n")
				.append("CZK: ").append(CZK+",\n")
				.append("SEK: ").append(SEK+",\n")
				.append("NZD: ").append(NZD+",\n")
				.append("BRL: ").append(BRL+",\n")
				.append("\n}").toString();
		}

		public static class Builder {
			public double CHF;
			public double HRK;
			public double MXN;
			public double ZAR;
			public double INR;
			public double THB;
			public double CNY;
			public double AUD;
			public double ILS;
			public double KRW;
			public double JPY;
			public double PLN;
			public double GBP;
			public double IDR;
			public double HUF;
			public double PHP;
			public double TRY;
			public double RUB;
			public double HKD;
			public double ISK;
			public double DKK;
			public double MYR;
			public double CAD;
			public double USD;
			public double BGN;
			public double NOK;
			public double RON;
			public double SGD;
			public double CZK;
			public double SEK;
			public double NZD;
			public double BRL;

			public Builder setCHF(double CHF) {
				this.CHF = CHF;
				return this;
			}

			public Builder setHRK(double HRK) {
				this.HRK = HRK;
				return this;
			}

			public Builder setMXN(double MXN) {
				this.MXN = MXN;
				return this;
			}

			public Builder setZAR(double ZAR) {
				this.ZAR = ZAR;
				return this;
			}

			public Builder setINR(double INR) {
				this.INR = INR;
				return this;
			}

			public Builder setTHB(double THB) {
				this.THB = THB;
				return this;
			}

			public Builder setCNY(double CNY) {
				this.CNY = CNY;
				return this;
			}

			public Builder setAUD(double AUD) {
				this.AUD = AUD;
				return this;
			}

			public Builder setILS(double ILS) {
				this.ILS = ILS;
				return this;
			}

			public Builder setKRW(double KRW) {
				this.KRW = KRW;
				return this;
			}

			public Builder setJPY(double JPY) {
				this.JPY = JPY;
				return this;
			}

			public Builder setPLN(double PLN) {
				this.PLN = PLN;
				return this;
			}

			public Builder setGBP(double GBP) {
				this.GBP = GBP;
				return this;
			}

			public Builder setIDR(double IDR) {
				this.IDR = IDR;
				return this;
			}

			public Builder setHUF(double HUF) {
				this.HUF = HUF;
				return this;
			}

			public Builder setPHP(double PHP) {
				this.PHP = PHP;
				return this;
			}

			public Builder setTRY(double TRY) {
				this.TRY = TRY;
				return this;
			}

			public Builder setRUB(double RUB) {
				this.RUB = RUB;
				return this;
			}

			public Builder setHKD(double HKD) {
				this.HKD = HKD;
				return this;
			}

			public Builder setISK(double ISK) {
				this.ISK = ISK;
				return this;
			}

			public Builder setDKK(double DKK) {
				this.DKK = DKK;
				return this;
			}

			public Builder setMYR(double MYR) {
				this.MYR = MYR;
				return this;
			}

			public Builder setCAD(double CAD) {
				this.CAD = CAD;
				return this;
			}

			public Builder setUSD(double USD) {
				this.USD = USD;
				return this;
			}

			public Builder setBGN(double BGN) {
				this.BGN = BGN;
				return this;
			}

			public Builder setNOK(double NOK) {
				this.NOK = NOK;
				return this;
			}

			public Builder setRON(double RON) {
				this.RON = RON;
				return this;
			}

			public Builder setSGD(double SGD) {
				this.SGD = SGD;
				return this;
			}

			public Builder setCZK(double CZK) {
				this.CZK = CZK;
				return this;
			}

			public Builder setSEK(double SEK) {
				this.SEK = SEK;
				return this;
			}

			public Builder setNZD(double NZD) {
				this.NZD = NZD;
				return this;
			}

			public Builder setBRL(double BRL) {
				this.BRL = BRL;
				return this;
			}

			public Rates build() {
				return new Rates(CHF,HRK,MXN,ZAR,INR,THB,CNY,AUD,ILS,KRW,JPY,PLN,GBP,IDR,HUF,PHP,TRY,RUB,HKD,ISK,DKK,MYR,CAD,USD,BGN,NOK,RON,SGD,CZK,SEK,NZD,BRL);
			}
		}
	}
}