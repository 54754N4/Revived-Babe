package lib;

import java.lang.management.ManagementFactory;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import lib.Introspector.Info.RAM;

public abstract class Introspector {
	public static final long MEGABYTE = 1024L * 1024L;
	
	public static void main(String[] args) throws Exception {
		Thread.sleep(1000);
		while (true) {
			System.out.println(usageCPU());
			Thread.sleep(500);
		}
	} 

	public static String usageCPU() throws Exception {
		return String.format("JVM CPU : %.3f %% ", Info.CPU.getProcessCpuPercentage());
	}
	
	public static String usageMemory() {
		RAM ram = new Info.RAM();
		return String.format("JVM RAM : %d/%dMB (%.2f %%)", 
				bytesToMegabytes(ram.used),
				bytesToMegabytes(ram.total),
				(((float) ram.used / (float) ram.total) * 100));
	}
	
	public static long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}
	
	public static class Info {
		private static final Runtime runtime = Runtime.getRuntime();
		
		public static class RAM {
			public final long total, free, used;	// in bytes
			
			public RAM() {
				total = runtime.totalMemory();
				free = runtime.freeMemory();
				used = total - free;				// at this point in time	
			}
		}
		
		public static class CPU {
			public static double getProcessCpuPercentage() throws MalformedObjectNameException, NullPointerException, InstanceNotFoundException, ReflectionException {
			    MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
			    ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
			    AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });
			    if (list.isEmpty()) return Double.NaN;
			    Attribute att = (Attribute)list.get(0);
			    Double value  = (Double)att.getValue();
			    if (value == -1.0) return Double.NaN;	// usually takes a couple of seconds before we get real values
			    return ((value * 1000) / 10.0);
			}
		}
	}
}
