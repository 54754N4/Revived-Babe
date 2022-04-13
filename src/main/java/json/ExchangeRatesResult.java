package json;

import java.io.Serializable;

public final class ExchangeRatesResult implements Serializable {
	private static final long serialVersionUID = 1L;
	public String license;
	public Rates rates;
	public String disclaimer;
	public int timestamp;
	public String base;

	public ExchangeRatesResult(String license, Rates rates, String disclaimer, int timestamp, String base) {
		this.license = license;
		this.rates = rates;
		this.disclaimer = disclaimer;
		this.timestamp = timestamp;
		this.base = base;
	}

	public String getLicense() {
		return license;
	}

	public Rates getRates() {
		return rates;
	}

	public String getDisclaimer() {
		return disclaimer;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public String getBase() {
		return base;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public void setRates(Rates rates) {
		this.rates = rates;
	}

	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public void setBase(String base) {
		this.base = base;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(getClass().getName()).append("{\n")
			.append("license: ").append(license+",\n")
			.append("rates: ").append(rates+",\n")
			.append("disclaimer: ").append(disclaimer+",\n")
			.append("timestamp: ").append(timestamp+",\n")
			.append("base: ").append(base+",\n")
			.append("\n}").toString();
	}

	public static class Builder {
		public String license;
		public Rates rates;
		public String disclaimer;
		public int timestamp;
		public String base;

		public Builder setLicense(String license) {
			this.license = license;
			return this;
		}

		public Builder setRates(Rates rates) {
			this.rates = rates;
			return this;
		}

		public Builder setDisclaimer(String disclaimer) {
			this.disclaimer = disclaimer;
			return this;
		}

		public Builder setTimestamp(int timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Builder setBase(String base) {
			this.base = base;
			return this;
		}

		public ExchangeRatesResult build() {
			return new ExchangeRatesResult(license,rates,disclaimer,timestamp,base);
		}
	}

	public static final class Rates implements Serializable {
		private static final long serialVersionUID = 1L;
		public double FJD;
		public double MXN;
		public double STD;
		public double SCR;
		public double CDF;
		public double BBD;
		public double GTQ;
		public double CLP;
		public double HNL;
		public double UGX;
		public double ZAR;
		public double TND;
		public double STN;
		public double CUC;
		public double BSD;
		public double SLL;
		public double SDG;
		public double IQD;
		public double CUP;
		public double GMD;
		public double TWD;
		public double RSD;
		public double DOP;
		public double KMF;
		public double MYR;
		public double FKP;
		public double XOF;
		public double GEL;
		public double BTC;
		public double UYU;
		public double MAD;
		public double CVE;
		public double TOP;
		public double AZN;
		public double OMR;
		public double PGK;
		public double KES;
		public double SEK;
		public double CNH;
		public double BTN;
		public double UAH;
		public double GNF;
		public double ERN;
		public double MZN;
		public double SVC;
		public double ARS;
		public double QAR;
		public double IRR;
		public double XPD;
		public double CNY;
		public double THB;
		public double UZS;
		public double XPF;
		public double MRU;
		public double BDT;
		public double LYD;
		public double BMD;
		public double KWD;
		public double PHP;
		public double XPT;
		public double RUB;
		public double PYG;
		public double ISK;
		public double JMD;
		public double COP;
		public double MKD;
		public double USD;
		public double DZD;
		public double PAB;
		public double GGP;
		public double SGD;
		public double ETB;
		public double JEP;
		public double KGS;
		public double SOS;
		public double VUV;
		public double LAK;
		public double BND;
		public double XAF;
		public double LRD;
		public double XAG;
		public double CHF;
		public double HRK;
		public double ALL;
		public double DJF;
		public double VES;
		public double ZMW;
		public double TZS;
		public double VND;
		public double XAU;
		public double AUD;
		public double ILS;
		public double GHS;
		public double GYD;
		public double KPW;
		public double BOB;
		public double KHR;
		public double MDL;
		public double IDR;
		public double KYD;
		public double AMD;
		public double BWP;
		public double SHP;
		public double TRY;
		public double LBP;
		public double TJS;
		public double JOD;
		public double AED;
		public double HKD;
		public double RWF;
		public double EUR;
		public double LSL;
		public double DKK;
		public double CAD;
		public double BGN;
		public double MMK;
		public double MUR;
		public double NOK;
		public double SYP;
		public double IMP;
		public double ZWL;
		public double GIP;
		public double RON;
		public double LKR;
		public double NGN;
		public double CRC;
		public double CZK;
		public double PKR;
		public double XCD;
		public double ANG;
		public double HTG;
		public double BHD;
		public double KZT;
		public double SRD;
		public double SZL;
		public double SAR;
		public double TTD;
		public double YER;
		public double MVR;
		public double AFN;
		public double INR;
		public double AWG;
		public double KRW;
		public double NPR;
		public double JPY;
		public double MNT;
		public double AOA;
		public double PLN;
		public double GBP;
		public double SBD;
		public double BYN;
		public double HUF;
		public double BIF;
		public double MWK;
		public double MGA;
		public double XDR;
		public double BZD;
		public double BAM;
		public double EGP;
		public double MOP;
		public double NAD;
		public double SSP;
		public double NIO;
		public double PEN;
		public double NZD;
		public double WST;
		public double TMT;
		public double CLF;
		public double BRL;

		public Rates() {}	// use a no-arg constructor cause java doesn't support more than 255 parameters in a method/ctor

		public double getFJD() {
			return FJD;
		}

		public double getMXN() {
			return MXN;
		}

		public double getSTD() {
			return STD;
		}

		public double getSCR() {
			return SCR;
		}

		public double getCDF() {
			return CDF;
		}

		public double getBBD() {
			return BBD;
		}

		public double getGTQ() {
			return GTQ;
		}

		public double getCLP() {
			return CLP;
		}

		public double getHNL() {
			return HNL;
		}

		public double getUGX() {
			return UGX;
		}

		public double getZAR() {
			return ZAR;
		}

		public double getTND() {
			return TND;
		}

		public double getSTN() {
			return STN;
		}

		public double getCUC() {
			return CUC;
		}

		public double getBSD() {
			return BSD;
		}

		public double getSLL() {
			return SLL;
		}

		public double getSDG() {
			return SDG;
		}

		public double getIQD() {
			return IQD;
		}

		public double getCUP() {
			return CUP;
		}

		public double getGMD() {
			return GMD;
		}

		public double getTWD() {
			return TWD;
		}

		public double getRSD() {
			return RSD;
		}

		public double getDOP() {
			return DOP;
		}

		public double getKMF() {
			return KMF;
		}

		public double getMYR() {
			return MYR;
		}

		public double getFKP() {
			return FKP;
		}

		public double getXOF() {
			return XOF;
		}

		public double getGEL() {
			return GEL;
		}

		public double getBTC() {
			return BTC;
		}

		public double getUYU() {
			return UYU;
		}

		public double getMAD() {
			return MAD;
		}

		public double getCVE() {
			return CVE;
		}

		public double getTOP() {
			return TOP;
		}

		public double getAZN() {
			return AZN;
		}

		public double getOMR() {
			return OMR;
		}

		public double getPGK() {
			return PGK;
		}

		public double getKES() {
			return KES;
		}

		public double getSEK() {
			return SEK;
		}

		public double getCNH() {
			return CNH;
		}

		public double getBTN() {
			return BTN;
		}

		public double getUAH() {
			return UAH;
		}

		public double getGNF() {
			return GNF;
		}

		public double getERN() {
			return ERN;
		}

		public double getMZN() {
			return MZN;
		}

		public double getSVC() {
			return SVC;
		}

		public double getARS() {
			return ARS;
		}

		public double getQAR() {
			return QAR;
		}

		public double getIRR() {
			return IRR;
		}

		public double getXPD() {
			return XPD;
		}

		public double getCNY() {
			return CNY;
		}

		public double getTHB() {
			return THB;
		}

		public double getUZS() {
			return UZS;
		}

		public double getXPF() {
			return XPF;
		}

		public double getMRU() {
			return MRU;
		}

		public double getBDT() {
			return BDT;
		}

		public double getLYD() {
			return LYD;
		}

		public double getBMD() {
			return BMD;
		}

		public double getKWD() {
			return KWD;
		}

		public double getPHP() {
			return PHP;
		}

		public double getXPT() {
			return XPT;
		}

		public double getRUB() {
			return RUB;
		}

		public double getPYG() {
			return PYG;
		}

		public double getISK() {
			return ISK;
		}

		public double getJMD() {
			return JMD;
		}

		public double getCOP() {
			return COP;
		}

		public double getMKD() {
			return MKD;
		}

		public double getUSD() {
			return USD;
		}

		public double getDZD() {
			return DZD;
		}

		public double getPAB() {
			return PAB;
		}

		public double getGGP() {
			return GGP;
		}

		public double getSGD() {
			return SGD;
		}

		public double getETB() {
			return ETB;
		}

		public double getJEP() {
			return JEP;
		}

		public double getKGS() {
			return KGS;
		}

		public double getSOS() {
			return SOS;
		}

		public double getVUV() {
			return VUV;
		}

		public double getLAK() {
			return LAK;
		}

		public double getBND() {
			return BND;
		}

		public double getXAF() {
			return XAF;
		}

		public double getLRD() {
			return LRD;
		}

		public double getXAG() {
			return XAG;
		}

		public double getCHF() {
			return CHF;
		}

		public double getHRK() {
			return HRK;
		}

		public double getALL() {
			return ALL;
		}

		public double getDJF() {
			return DJF;
		}

		public double getVES() {
			return VES;
		}

		public double getZMW() {
			return ZMW;
		}

		public double getTZS() {
			return TZS;
		}

		public double getVND() {
			return VND;
		}

		public double getXAU() {
			return XAU;
		}

		public double getAUD() {
			return AUD;
		}

		public double getILS() {
			return ILS;
		}

		public double getGHS() {
			return GHS;
		}

		public double getGYD() {
			return GYD;
		}

		public double getKPW() {
			return KPW;
		}

		public double getBOB() {
			return BOB;
		}

		public double getKHR() {
			return KHR;
		}

		public double getMDL() {
			return MDL;
		}

		public double getIDR() {
			return IDR;
		}

		public double getKYD() {
			return KYD;
		}

		public double getAMD() {
			return AMD;
		}

		public double getBWP() {
			return BWP;
		}

		public double getSHP() {
			return SHP;
		}

		public double getTRY() {
			return TRY;
		}

		public double getLBP() {
			return LBP;
		}

		public double getTJS() {
			return TJS;
		}

		public double getJOD() {
			return JOD;
		}

		public double getAED() {
			return AED;
		}

		public double getHKD() {
			return HKD;
		}

		public double getRWF() {
			return RWF;
		}

		public double getEUR() {
			return EUR;
		}

		public double getLSL() {
			return LSL;
		}

		public double getDKK() {
			return DKK;
		}

		public double getCAD() {
			return CAD;
		}

		public double getBGN() {
			return BGN;
		}

		public double getMMK() {
			return MMK;
		}

		public double getMUR() {
			return MUR;
		}

		public double getNOK() {
			return NOK;
		}

		public double getSYP() {
			return SYP;
		}

		public double getIMP() {
			return IMP;
		}

		public double getZWL() {
			return ZWL;
		}

		public double getGIP() {
			return GIP;
		}

		public double getRON() {
			return RON;
		}

		public double getLKR() {
			return LKR;
		}

		public double getNGN() {
			return NGN;
		}

		public double getCRC() {
			return CRC;
		}

		public double getCZK() {
			return CZK;
		}

		public double getPKR() {
			return PKR;
		}

		public double getXCD() {
			return XCD;
		}

		public double getANG() {
			return ANG;
		}

		public double getHTG() {
			return HTG;
		}

		public double getBHD() {
			return BHD;
		}

		public double getKZT() {
			return KZT;
		}

		public double getSRD() {
			return SRD;
		}

		public double getSZL() {
			return SZL;
		}

		public double getSAR() {
			return SAR;
		}

		public double getTTD() {
			return TTD;
		}

		public double getYER() {
			return YER;
		}

		public double getMVR() {
			return MVR;
		}

		public double getAFN() {
			return AFN;
		}

		public double getINR() {
			return INR;
		}

		public double getAWG() {
			return AWG;
		}

		public double getKRW() {
			return KRW;
		}

		public double getNPR() {
			return NPR;
		}

		public double getJPY() {
			return JPY;
		}

		public double getMNT() {
			return MNT;
		}

		public double getAOA() {
			return AOA;
		}

		public double getPLN() {
			return PLN;
		}

		public double getGBP() {
			return GBP;
		}

		public double getSBD() {
			return SBD;
		}

		public double getBYN() {
			return BYN;
		}

		public double getHUF() {
			return HUF;
		}

		public double getBIF() {
			return BIF;
		}

		public double getMWK() {
			return MWK;
		}

		public double getMGA() {
			return MGA;
		}

		public double getXDR() {
			return XDR;
		}

		public double getBZD() {
			return BZD;
		}

		public double getBAM() {
			return BAM;
		}

		public double getEGP() {
			return EGP;
		}

		public double getMOP() {
			return MOP;
		}

		public double getNAD() {
			return NAD;
		}

		public double getSSP() {
			return SSP;
		}

		public double getNIO() {
			return NIO;
		}

		public double getPEN() {
			return PEN;
		}

		public double getNZD() {
			return NZD;
		}

		public double getWST() {
			return WST;
		}

		public double getTMT() {
			return TMT;
		}

		public double getCLF() {
			return CLF;
		}

		public double getBRL() {
			return BRL;
		}

		public void setFJD(double FJD) {
			this.FJD = FJD;
		}

		public void setMXN(double MXN) {
			this.MXN = MXN;
		}

		public void setSTD(double STD) {
			this.STD = STD;
		}

		public void setSCR(double SCR) {
			this.SCR = SCR;
		}

		public void setCDF(double CDF) {
			this.CDF = CDF;
		}

		public void setBBD(double BBD) {
			this.BBD = BBD;
		}

		public void setGTQ(double GTQ) {
			this.GTQ = GTQ;
		}

		public void setCLP(double CLP) {
			this.CLP = CLP;
		}

		public void setHNL(double HNL) {
			this.HNL = HNL;
		}

		public void setUGX(double UGX) {
			this.UGX = UGX;
		}

		public void setZAR(double ZAR) {
			this.ZAR = ZAR;
		}

		public void setTND(double TND) {
			this.TND = TND;
		}

		public void setSTN(double STN) {
			this.STN = STN;
		}

		public void setCUC(double CUC) {
			this.CUC = CUC;
		}

		public void setBSD(double BSD) {
			this.BSD = BSD;
		}

		public void setSLL(double SLL) {
			this.SLL = SLL;
		}

		public void setSDG(double SDG) {
			this.SDG = SDG;
		}

		public void setIQD(double IQD) {
			this.IQD = IQD;
		}

		public void setCUP(double CUP) {
			this.CUP = CUP;
		}

		public void setGMD(double GMD) {
			this.GMD = GMD;
		}

		public void setTWD(double TWD) {
			this.TWD = TWD;
		}

		public void setRSD(double RSD) {
			this.RSD = RSD;
		}

		public void setDOP(double DOP) {
			this.DOP = DOP;
		}

		public void setKMF(double KMF) {
			this.KMF = KMF;
		}

		public void setMYR(double MYR) {
			this.MYR = MYR;
		}

		public void setFKP(double FKP) {
			this.FKP = FKP;
		}

		public void setXOF(double XOF) {
			this.XOF = XOF;
		}

		public void setGEL(double GEL) {
			this.GEL = GEL;
		}

		public void setBTC(double BTC) {
			this.BTC = BTC;
		}

		public void setUYU(double UYU) {
			this.UYU = UYU;
		}

		public void setMAD(double MAD) {
			this.MAD = MAD;
		}

		public void setCVE(double CVE) {
			this.CVE = CVE;
		}

		public void setTOP(double TOP) {
			this.TOP = TOP;
		}

		public void setAZN(double AZN) {
			this.AZN = AZN;
		}

		public void setOMR(double OMR) {
			this.OMR = OMR;
		}

		public void setPGK(double PGK) {
			this.PGK = PGK;
		}

		public void setKES(double KES) {
			this.KES = KES;
		}

		public void setSEK(double SEK) {
			this.SEK = SEK;
		}

		public void setCNH(double CNH) {
			this.CNH = CNH;
		}

		public void setBTN(double BTN) {
			this.BTN = BTN;
		}

		public void setUAH(double UAH) {
			this.UAH = UAH;
		}

		public void setGNF(double GNF) {
			this.GNF = GNF;
		}

		public void setERN(double ERN) {
			this.ERN = ERN;
		}

		public void setMZN(double MZN) {
			this.MZN = MZN;
		}

		public void setSVC(double SVC) {
			this.SVC = SVC;
		}

		public void setARS(double ARS) {
			this.ARS = ARS;
		}

		public void setQAR(double QAR) {
			this.QAR = QAR;
		}

		public void setIRR(double IRR) {
			this.IRR = IRR;
		}

		public void setXPD(double XPD) {
			this.XPD = XPD;
		}

		public void setCNY(double CNY) {
			this.CNY = CNY;
		}

		public void setTHB(double THB) {
			this.THB = THB;
		}

		public void setUZS(double UZS) {
			this.UZS = UZS;
		}

		public void setXPF(double XPF) {
			this.XPF = XPF;
		}

		public void setMRU(double MRU) {
			this.MRU = MRU;
		}

		public void setBDT(double BDT) {
			this.BDT = BDT;
		}

		public void setLYD(double LYD) {
			this.LYD = LYD;
		}

		public void setBMD(double BMD) {
			this.BMD = BMD;
		}

		public void setKWD(double KWD) {
			this.KWD = KWD;
		}

		public void setPHP(double PHP) {
			this.PHP = PHP;
		}

		public void setXPT(double XPT) {
			this.XPT = XPT;
		}

		public void setRUB(double RUB) {
			this.RUB = RUB;
		}

		public void setPYG(double PYG) {
			this.PYG = PYG;
		}

		public void setISK(double ISK) {
			this.ISK = ISK;
		}

		public void setJMD(double JMD) {
			this.JMD = JMD;
		}

		public void setCOP(double COP) {
			this.COP = COP;
		}

		public void setMKD(double MKD) {
			this.MKD = MKD;
		}

		public void setUSD(double USD) {
			this.USD = USD;
		}

		public void setDZD(double DZD) {
			this.DZD = DZD;
		}

		public void setPAB(double PAB) {
			this.PAB = PAB;
		}

		public void setGGP(double GGP) {
			this.GGP = GGP;
		}

		public void setSGD(double SGD) {
			this.SGD = SGD;
		}

		public void setETB(double ETB) {
			this.ETB = ETB;
		}

		public void setJEP(double JEP) {
			this.JEP = JEP;
		}

		public void setKGS(double KGS) {
			this.KGS = KGS;
		}

		public void setSOS(double SOS) {
			this.SOS = SOS;
		}

		public void setVUV(double VUV) {
			this.VUV = VUV;
		}

		public void setLAK(double LAK) {
			this.LAK = LAK;
		}

		public void setBND(double BND) {
			this.BND = BND;
		}

		public void setXAF(double XAF) {
			this.XAF = XAF;
		}

		public void setLRD(double LRD) {
			this.LRD = LRD;
		}

		public void setXAG(double XAG) {
			this.XAG = XAG;
		}

		public void setCHF(double CHF) {
			this.CHF = CHF;
		}

		public void setHRK(double HRK) {
			this.HRK = HRK;
		}

		public void setALL(double ALL) {
			this.ALL = ALL;
		}

		public void setDJF(double DJF) {
			this.DJF = DJF;
		}

		public void setVES(double VES) {
			this.VES = VES;
		}

		public void setZMW(double ZMW) {
			this.ZMW = ZMW;
		}

		public void setTZS(double TZS) {
			this.TZS = TZS;
		}

		public void setVND(double VND) {
			this.VND = VND;
		}

		public void setXAU(double XAU) {
			this.XAU = XAU;
		}

		public void setAUD(double AUD) {
			this.AUD = AUD;
		}

		public void setILS(double ILS) {
			this.ILS = ILS;
		}

		public void setGHS(double GHS) {
			this.GHS = GHS;
		}

		public void setGYD(double GYD) {
			this.GYD = GYD;
		}

		public void setKPW(double KPW) {
			this.KPW = KPW;
		}

		public void setBOB(double BOB) {
			this.BOB = BOB;
		}

		public void setKHR(double KHR) {
			this.KHR = KHR;
		}

		public void setMDL(double MDL) {
			this.MDL = MDL;
		}

		public void setIDR(double IDR) {
			this.IDR = IDR;
		}

		public void setKYD(double KYD) {
			this.KYD = KYD;
		}

		public void setAMD(double AMD) {
			this.AMD = AMD;
		}

		public void setBWP(double BWP) {
			this.BWP = BWP;
		}

		public void setSHP(double SHP) {
			this.SHP = SHP;
		}

		public void setTRY(double TRY) {
			this.TRY = TRY;
		}

		public void setLBP(double LBP) {
			this.LBP = LBP;
		}

		public void setTJS(double TJS) {
			this.TJS = TJS;
		}

		public void setJOD(double JOD) {
			this.JOD = JOD;
		}

		public void setAED(double AED) {
			this.AED = AED;
		}

		public void setHKD(double HKD) {
			this.HKD = HKD;
		}

		public void setRWF(double RWF) {
			this.RWF = RWF;
		}

		public void setEUR(double EUR) {
			this.EUR = EUR;
		}

		public void setLSL(double LSL) {
			this.LSL = LSL;
		}

		public void setDKK(double DKK) {
			this.DKK = DKK;
		}

		public void setCAD(double CAD) {
			this.CAD = CAD;
		}

		public void setBGN(double BGN) {
			this.BGN = BGN;
		}

		public void setMMK(double MMK) {
			this.MMK = MMK;
		}

		public void setMUR(double MUR) {
			this.MUR = MUR;
		}

		public void setNOK(double NOK) {
			this.NOK = NOK;
		}

		public void setSYP(double SYP) {
			this.SYP = SYP;
		}

		public void setIMP(double IMP) {
			this.IMP = IMP;
		}

		public void setZWL(double ZWL) {
			this.ZWL = ZWL;
		}

		public void setGIP(double GIP) {
			this.GIP = GIP;
		}

		public void setRON(double RON) {
			this.RON = RON;
		}

		public void setLKR(double LKR) {
			this.LKR = LKR;
		}

		public void setNGN(double NGN) {
			this.NGN = NGN;
		}

		public void setCRC(double CRC) {
			this.CRC = CRC;
		}

		public void setCZK(double CZK) {
			this.CZK = CZK;
		}

		public void setPKR(double PKR) {
			this.PKR = PKR;
		}

		public void setXCD(double XCD) {
			this.XCD = XCD;
		}

		public void setANG(double ANG) {
			this.ANG = ANG;
		}

		public void setHTG(double HTG) {
			this.HTG = HTG;
		}

		public void setBHD(double BHD) {
			this.BHD = BHD;
		}

		public void setKZT(double KZT) {
			this.KZT = KZT;
		}

		public void setSRD(double SRD) {
			this.SRD = SRD;
		}

		public void setSZL(double SZL) {
			this.SZL = SZL;
		}

		public void setSAR(double SAR) {
			this.SAR = SAR;
		}

		public void setTTD(double TTD) {
			this.TTD = TTD;
		}

		public void setYER(double YER) {
			this.YER = YER;
		}

		public void setMVR(double MVR) {
			this.MVR = MVR;
		}

		public void setAFN(double AFN) {
			this.AFN = AFN;
		}

		public void setINR(double INR) {
			this.INR = INR;
		}

		public void setAWG(double AWG) {
			this.AWG = AWG;
		}

		public void setKRW(double KRW) {
			this.KRW = KRW;
		}

		public void setNPR(double NPR) {
			this.NPR = NPR;
		}

		public void setJPY(double JPY) {
			this.JPY = JPY;
		}

		public void setMNT(double MNT) {
			this.MNT = MNT;
		}

		public void setAOA(double AOA) {
			this.AOA = AOA;
		}

		public void setPLN(double PLN) {
			this.PLN = PLN;
		}

		public void setGBP(double GBP) {
			this.GBP = GBP;
		}

		public void setSBD(double SBD) {
			this.SBD = SBD;
		}

		public void setBYN(double BYN) {
			this.BYN = BYN;
		}

		public void setHUF(double HUF) {
			this.HUF = HUF;
		}

		public void setBIF(double BIF) {
			this.BIF = BIF;
		}

		public void setMWK(double MWK) {
			this.MWK = MWK;
		}

		public void setMGA(double MGA) {
			this.MGA = MGA;
		}

		public void setXDR(double XDR) {
			this.XDR = XDR;
		}

		public void setBZD(double BZD) {
			this.BZD = BZD;
		}

		public void setBAM(double BAM) {
			this.BAM = BAM;
		}

		public void setEGP(double EGP) {
			this.EGP = EGP;
		}

		public void setMOP(double MOP) {
			this.MOP = MOP;
		}

		public void setNAD(double NAD) {
			this.NAD = NAD;
		}

		public void setSSP(double SSP) {
			this.SSP = SSP;
		}

		public void setNIO(double NIO) {
			this.NIO = NIO;
		}

		public void setPEN(double PEN) {
			this.PEN = PEN;
		}

		public void setNZD(double NZD) {
			this.NZD = NZD;
		}

		public void setWST(double WST) {
			this.WST = WST;
		}

		public void setTMT(double TMT) {
			this.TMT = TMT;
		}

		public void setCLF(double CLF) {
			this.CLF = CLF;
		}

		public void setBRL(double BRL) {
			this.BRL = BRL;
		}

		@Override
		public String toString() {
			return new StringBuilder()
				.append(getClass().getName()).append("{\n")
				.append("FJD: ").append(FJD+",\n")
				.append("MXN: ").append(MXN+",\n")
				.append("STD: ").append(STD+",\n")
				.append("SCR: ").append(SCR+",\n")
				.append("CDF: ").append(CDF+",\n")
				.append("BBD: ").append(BBD+",\n")
				.append("GTQ: ").append(GTQ+",\n")
				.append("CLP: ").append(CLP+",\n")
				.append("HNL: ").append(HNL+",\n")
				.append("UGX: ").append(UGX+",\n")
				.append("ZAR: ").append(ZAR+",\n")
				.append("TND: ").append(TND+",\n")
				.append("STN: ").append(STN+",\n")
				.append("CUC: ").append(CUC+",\n")
				.append("BSD: ").append(BSD+",\n")
				.append("SLL: ").append(SLL+",\n")
				.append("SDG: ").append(SDG+",\n")
				.append("IQD: ").append(IQD+",\n")
				.append("CUP: ").append(CUP+",\n")
				.append("GMD: ").append(GMD+",\n")
				.append("TWD: ").append(TWD+",\n")
				.append("RSD: ").append(RSD+",\n")
				.append("DOP: ").append(DOP+",\n")
				.append("KMF: ").append(KMF+",\n")
				.append("MYR: ").append(MYR+",\n")
				.append("FKP: ").append(FKP+",\n")
				.append("XOF: ").append(XOF+",\n")
				.append("GEL: ").append(GEL+",\n")
				.append("BTC: ").append(BTC+",\n")
				.append("UYU: ").append(UYU+",\n")
				.append("MAD: ").append(MAD+",\n")
				.append("CVE: ").append(CVE+",\n")
				.append("TOP: ").append(TOP+",\n")
				.append("AZN: ").append(AZN+",\n")
				.append("OMR: ").append(OMR+",\n")
				.append("PGK: ").append(PGK+",\n")
				.append("KES: ").append(KES+",\n")
				.append("SEK: ").append(SEK+",\n")
				.append("CNH: ").append(CNH+",\n")
				.append("BTN: ").append(BTN+",\n")
				.append("UAH: ").append(UAH+",\n")
				.append("GNF: ").append(GNF+",\n")
				.append("ERN: ").append(ERN+",\n")
				.append("MZN: ").append(MZN+",\n")
				.append("SVC: ").append(SVC+",\n")
				.append("ARS: ").append(ARS+",\n")
				.append("QAR: ").append(QAR+",\n")
				.append("IRR: ").append(IRR+",\n")
				.append("XPD: ").append(XPD+",\n")
				.append("CNY: ").append(CNY+",\n")
				.append("THB: ").append(THB+",\n")
				.append("UZS: ").append(UZS+",\n")
				.append("XPF: ").append(XPF+",\n")
				.append("MRU: ").append(MRU+",\n")
				.append("BDT: ").append(BDT+",\n")
				.append("LYD: ").append(LYD+",\n")
				.append("BMD: ").append(BMD+",\n")
				.append("KWD: ").append(KWD+",\n")
				.append("PHP: ").append(PHP+",\n")
				.append("XPT: ").append(XPT+",\n")
				.append("RUB: ").append(RUB+",\n")
				.append("PYG: ").append(PYG+",\n")
				.append("ISK: ").append(ISK+",\n")
				.append("JMD: ").append(JMD+",\n")
				.append("COP: ").append(COP+",\n")
				.append("MKD: ").append(MKD+",\n")
				.append("USD: ").append(USD+",\n")
				.append("DZD: ").append(DZD+",\n")
				.append("PAB: ").append(PAB+",\n")
				.append("GGP: ").append(GGP+",\n")
				.append("SGD: ").append(SGD+",\n")
				.append("ETB: ").append(ETB+",\n")
				.append("JEP: ").append(JEP+",\n")
				.append("KGS: ").append(KGS+",\n")
				.append("SOS: ").append(SOS+",\n")
				.append("VUV: ").append(VUV+",\n")
				.append("LAK: ").append(LAK+",\n")
				.append("BND: ").append(BND+",\n")
				.append("XAF: ").append(XAF+",\n")
				.append("LRD: ").append(LRD+",\n")
				.append("XAG: ").append(XAG+",\n")
				.append("CHF: ").append(CHF+",\n")
				.append("HRK: ").append(HRK+",\n")
				.append("ALL: ").append(ALL+",\n")
				.append("DJF: ").append(DJF+",\n")
				.append("VES: ").append(VES+",\n")
				.append("ZMW: ").append(ZMW+",\n")
				.append("TZS: ").append(TZS+",\n")
				.append("VND: ").append(VND+",\n")
				.append("XAU: ").append(XAU+",\n")
				.append("AUD: ").append(AUD+",\n")
				.append("ILS: ").append(ILS+",\n")
				.append("GHS: ").append(GHS+",\n")
				.append("GYD: ").append(GYD+",\n")
				.append("KPW: ").append(KPW+",\n")
				.append("BOB: ").append(BOB+",\n")
				.append("KHR: ").append(KHR+",\n")
				.append("MDL: ").append(MDL+",\n")
				.append("IDR: ").append(IDR+",\n")
				.append("KYD: ").append(KYD+",\n")
				.append("AMD: ").append(AMD+",\n")
				.append("BWP: ").append(BWP+",\n")
				.append("SHP: ").append(SHP+",\n")
				.append("TRY: ").append(TRY+",\n")
				.append("LBP: ").append(LBP+",\n")
				.append("TJS: ").append(TJS+",\n")
				.append("JOD: ").append(JOD+",\n")
				.append("AED: ").append(AED+",\n")
				.append("HKD: ").append(HKD+",\n")
				.append("RWF: ").append(RWF+",\n")
				.append("EUR: ").append(EUR+",\n")
				.append("LSL: ").append(LSL+",\n")
				.append("DKK: ").append(DKK+",\n")
				.append("CAD: ").append(CAD+",\n")
				.append("BGN: ").append(BGN+",\n")
				.append("MMK: ").append(MMK+",\n")
				.append("MUR: ").append(MUR+",\n")
				.append("NOK: ").append(NOK+",\n")
				.append("SYP: ").append(SYP+",\n")
				.append("IMP: ").append(IMP+",\n")
				.append("ZWL: ").append(ZWL+",\n")
				.append("GIP: ").append(GIP+",\n")
				.append("RON: ").append(RON+",\n")
				.append("LKR: ").append(LKR+",\n")
				.append("NGN: ").append(NGN+",\n")
				.append("CRC: ").append(CRC+",\n")
				.append("CZK: ").append(CZK+",\n")
				.append("PKR: ").append(PKR+",\n")
				.append("XCD: ").append(XCD+",\n")
				.append("ANG: ").append(ANG+",\n")
				.append("HTG: ").append(HTG+",\n")
				.append("BHD: ").append(BHD+",\n")
				.append("KZT: ").append(KZT+",\n")
				.append("SRD: ").append(SRD+",\n")
				.append("SZL: ").append(SZL+",\n")
				.append("SAR: ").append(SAR+",\n")
				.append("TTD: ").append(TTD+",\n")
				.append("YER: ").append(YER+",\n")
				.append("MVR: ").append(MVR+",\n")
				.append("AFN: ").append(AFN+",\n")
				.append("INR: ").append(INR+",\n")
				.append("AWG: ").append(AWG+",\n")
				.append("KRW: ").append(KRW+",\n")
				.append("NPR: ").append(NPR+",\n")
				.append("JPY: ").append(JPY+",\n")
				.append("MNT: ").append(MNT+",\n")
				.append("AOA: ").append(AOA+",\n")
				.append("PLN: ").append(PLN+",\n")
				.append("GBP: ").append(GBP+",\n")
				.append("SBD: ").append(SBD+",\n")
				.append("BYN: ").append(BYN+",\n")
				.append("HUF: ").append(HUF+",\n")
				.append("BIF: ").append(BIF+",\n")
				.append("MWK: ").append(MWK+",\n")
				.append("MGA: ").append(MGA+",\n")
				.append("XDR: ").append(XDR+",\n")
				.append("BZD: ").append(BZD+",\n")
				.append("BAM: ").append(BAM+",\n")
				.append("EGP: ").append(EGP+",\n")
				.append("MOP: ").append(MOP+",\n")
				.append("NAD: ").append(NAD+",\n")
				.append("SSP: ").append(SSP+",\n")
				.append("NIO: ").append(NIO+",\n")
				.append("PEN: ").append(PEN+",\n")
				.append("NZD: ").append(NZD+",\n")
				.append("WST: ").append(WST+",\n")
				.append("TMT: ").append(TMT+",\n")
				.append("CLF: ").append(CLF+",\n")
				.append("BRL: ").append(BRL+",\n")
				.append("\n}").toString();
		}
	}
}