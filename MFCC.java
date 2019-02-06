import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Vector;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class MFCC {
  public static void main(String[] args) throws IOException, InterruptedException, Exception {
    System.out.println("MFCC Coefficient Extractor");
    // Executables
    String sox = "/usr/bin/sox";
    String x2x = "/usr/local/bin/x2x";
		String frame = "/usr/local/bin/frame";
		String window = "/usr/local/bin/window";
		String mcep = "/usr/local/bin/mcep";
		String swab = "/usr/local/bin/swab";

    // Command Line Options
    String WavFile = "/output/audio.wav";
    String RawFile = WavFile + ".raw";
    String frameLength = "400";
    String frameLengthOutput = "512";
    String framePeriod = "80";
		String mgcOrder = "24";
    String mfccFile = WavFile + ".mfc";

    // Convert original WAV file to RAW
    // String soxcmd = sox + " " + WavFile + "  " + RawFile;
    // launchProc(soxcmd, "sox", WavFile);

    String soxcmd = sox + " " + WavFile + " " + RawFile;
    launchProc(soxcmd, "sox", WavFile);

    // MFCC
    String mfcccmd = x2x + " +sf " + WavFile + " | " + frame + " -l " + frameLength + " -p " + framePeriod + " | " + window
        + " -l " + frameLength + " -L " + frameLengthOutput + " -w 1 -n 1 | " + mcep + " -a 0.42 -m " + mgcOrder
        + " -l " + frameLengthOutput + " | " + swab + " +f > " + mfccFile;

    launchBatchProc(mfcccmd, "getSptkMfcc", WavFile);

		int numFrames;
    DataInputStream mfcData = null;
		Vector<Float> mfc = new Vector<Float>();

		mfcData = new DataInputStream(new BufferedInputStream(new FileInputStream(mfccFile)));
		try {
			while (true) {
				mfc.add(mfcData.readFloat());
			}
		} catch (EOFException e) {
		}
		mfcData.close();

    System.out.println("Coefficient vector length: " + mfc.size());
    System.out.println("The coefficients are: " + mfc);
  }

	private static void launchProc(String cmdLine, String task, String baseName) {
		Process proc = null;
		BufferedReader procStdout = null;
		String line = null;
		try {
			proc = Runtime.getRuntime().exec(cmdLine);

			/* Collect stdout and send it to System.out: */
			procStdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while (true) {
				line = procStdout.readLine();
				if (line == null)
					break;
				System.out.println(line);
			}
			/* Wait and check the exit value */
			proc.waitFor();
			if (proc.exitValue() != 0) {
				throw new RuntimeException(task + " computation failed on file [" + baseName + "]!\n" + "Command line was: ["
						+ cmdLine + "].");
			}
		} catch (IOException e) {
			throw new RuntimeException(task + " computation provoked an IOException on file [" + baseName + "].", e);
		} catch (InterruptedException e) {
			throw new RuntimeException(task + " computation interrupted on file [" + baseName + "].", e);
		}
	}

	private static void launchBatchProc(String cmdLine, String task, String baseName) {

		Process proc = null;
		Process proctmp = null;
		BufferedReader procStdout = null;
		String line = null;
		String tmpFile = "/output/tmp.bat";

		try {
			FileWriter tmp = new FileWriter(tmpFile);
			tmp.write(cmdLine);
			tmp.close();

			/* make it executable... */
			proctmp = Runtime.getRuntime().exec("chmod +x " + tmpFile);
			proctmp.waitFor();
			proc = Runtime.getRuntime().exec(tmpFile);

			/* Collect stdout and send it to System.out: */
			procStdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while (true) {
				line = procStdout.readLine();
				if (line == null)
					break;
				System.out.println(line);
			}
			/* Wait and check the exit value */
			proc.waitFor();
			if (proc.exitValue() != 0) {
				throw new RuntimeException(task + " computation failed on file [" + baseName + "]!\n" + "Command line was: ["
						+ cmdLine + "].");
			}

		} catch (IOException e) {
			throw new RuntimeException(task + " computation provoked an IOException on file [" + baseName + "].", e);
		} catch (InterruptedException e) {
			throw new RuntimeException(task + " computation interrupted on file [" + baseName + "].", e);
		}

	}

}
