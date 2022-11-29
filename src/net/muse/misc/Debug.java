package net.muse.misc;

/**
 * @author mitsuyo
 * 
 */
public class Debug {
	final public static int DEBUG_OFF = 0;

	final public static int DEBUG_ON = 1;

	final public static int DEBUG_PROCESS = 2;

	static private int debugMode = 0;

	public static void setDebugMode(int mode) {
		debugMode = mode;
		System.out.print("Debug Mode: ");
		String str = "";
		switch (debugMode) {
			case DEBUG_OFF:
				str = "OFF";
				break;
			case DEBUG_ON:
				str = "ON";
				break;
			case DEBUG_PROCESS:
				str = "ProcessCount";
				break;
			default:
				System.err.println("Unknown parameter indicated");
				System.exit(0);
		}
		System.out.println(str);
	}

	public static void print(String str) {
		switch (debugMode) {
			case DEBUG_OFF:
				return;
			case DEBUG_ON:
				System.out.println(str);
				break;
			case DEBUG_PROCESS:
				System.out.print(".");
				break;
			default:
				System.err.println("Unknown parameter indicated");
				System.exit(0);
		}
	}

	public static void err(String str) {
		switch (debugMode) {
			case DEBUG_OFF:
				return;
			case DEBUG_ON:
				System.err.println(str);
				break;
			default:
				System.err.println("Unknown parameter indicated");
				System.exit(0);
		}
	}
}
