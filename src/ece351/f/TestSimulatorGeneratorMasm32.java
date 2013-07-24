package ece351.f;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Runtime;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Set;
import java.io.*;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import ece351.TestPrelab;
import ece351.f.ast.FProgram;
import ece351.util.CommandLine;
import ece351.util.Debug;
import ece351.util.ExaminableProperties;
import ece351.util.TestInputs351;
import ece351.w.ast.WProgram;
import ece351.w.parboiled.WParboiledParser;


@RunWith(Parameterized.class)
public class TestSimulatorGeneratorMasm32 {

	/** The test parameter. */
	protected final File input;
	
	// computed from the test parameter in computeFileNames()
	protected String waveName;
	protected String outputWaveName;
	protected String staffWavePath;
	protected String sourcePath;

	public final static String s = File.separator;

	public TestSimulatorGeneratorMasm32(final File f) {
		this.input = f;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> files() {
		return TestInputs351.formulaFiles();
	}
	
	@Test
	public void simgen() throws IOException {
		assertTrue(TestPrelab.areAssertionsEnabled());
		
		final String inputSpec = input.getAbsolutePath();
		if (inputSpec.contains("jvarty")) {
			// these files have too many variables in them
			// inconvenient to generate the appropriate wave inputs
			return;
		}
		
		final CommandLine c = new CommandLine("-p", inputSpec);
		final String input = c.readInputSpec();
		System.out.println("processing " + inputSpec);
		System.out.println("input: " + inputSpec);
		System.out.println(input);
		
		// parse from the F file
		final FProgram original = FParser.parse(c);
		assertTrue(original.repOk());
		ExaminableProperties.checkAllUnary(original);
		computeFileNames(inputSpec, original);

		// generate the Java output
		final String output_dir = sourcePath.substring(0, sourcePath.lastIndexOf( "\\" ) );
		final String dllPath = sourcePath.replace( ".java", ".dll" );
		
		final StringWriter swJava = new StringWriter();
		final StringWriter swDef = new StringWriter();
		final StringWriter swAsm = new StringWriter();
		final SimulatorGeneratorMasm32 sg = new SimulatorGeneratorMasm32();
		sg.generate(c.getInputName(), dllPath, original, new PrintWriter(swJava), new PrintWriter(swDef), new PrintWriter(swAsm));
		swJava.close();
		swDef.close();
		swAsm.close();
	
		// write generated Java source to file
		final File fJava = new File(sourcePath);
		{
			final String javasim = swJava.toString();
			System.out.println("output java:");
			System.out.println(javasim);
			final PrintWriter pw = new PrintWriter(new FileWriter(sourcePath));
			pw.write(javasim);
			pw.close();
		}
			
		// write definitions to file
		{
			final String defsim = swDef.toString();
			System.out.println("output definitions:");
			System.out.println(defsim);
			
			// write generated Definitions to file
			final String path = sourcePath.replace(".java", ".def");
			final PrintWriter pw = new PrintWriter(new FileWriter(path));
			pw.write(defsim);
			pw.close();
		}
		
		// write assembly to file
		{
			final String asmsim = swAsm.toString();
			System.out.println("output assembly:");
			System.out.println(asmsim);
			
			// write generated Assembly to file
			final String path = sourcePath.replace(".java", ".asm");
			final PrintWriter pw = new PrintWriter(new FileWriter(path));
			pw.write(asmsim);
			pw.close();
		}

		// compile the generated Java file
		final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		assert javac != null : "javac is null, see http://stackoverflow.com/questions/9107099/null-javacompiler-in-eclipse";
		int compileResult = javac.run(null, null,null, sourcePath);
		assertTrue("Compile Failed! Why? See prelab exercise.",compileResult==0);
		
		// write batch file to invoke JavaH and MASM32
		String batchPath = sourcePath.replace(".java", ".bat");
		try {
			FileWriter fstream = new FileWriter(batchPath);
			BufferedWriter out = new BufferedWriter(fstream);
			final String className = sourcePath.substring(sourcePath.lastIndexOf( "\\" ) + 1, sourcePath.lastIndexOf( ".java" ));
			// final String javaDirectory = "C:\\Program Files (x86)\\Java\\jdk1.7.0_25";
			out.write("cd \"" + output_dir +"\"\n");
			// out.write("\"" + javaDirectory + "\\bin\\javah\" " + className + "\n");
			out.write("ml /c /coff " + className + ".asm\n");
			out.write("Link /SUBSYSTEM:WINDOWS /DLL /DEF:" + className + ".def " + className + ".obj\n");
			out.close();
		}
		catch( Exception e ) {
			Debug.barf("Error writing batch script: " + e.getMessage());
		}
		
		// delete the old dll
		final File fDll = new File( dllPath );
		try {
			if( fDll.exists() )
				fDll.delete();
		}
		catch( Exception e ) {
			Debug.barf("Error deleting old DLL: " + e.getMessage());
		}
		
		// run batch file to assemble the dll
		System.out.println("-----Assembling...-----");
		
		try {
			Process batch = Runtime.getRuntime().exec(batchPath);
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(batch.getInputStream()));
			String text;
			while ((text = inputStream.readLine()) != null) {
				System.out.println(text);
			}
			inputStream.close();
		}
		catch( Exception e ) {
			Debug.barf("Error running MASM32: " + e.getMessage());
		}
		
		// check if dll was assembled
		assertTrue( "DLL was not Assembled!", fDll.exists() );
		
		System.out.println("-----Running...-----");
		// test the compiled output
		final String classPath = "file:///" + fJava.getParent() + s;
		final URLClassLoader loader = new URLClassLoader(new URL[]{new URL(classPath)});
		Class<?> simulatorClass = null;
		try {
			final String className = fJava.getName().replace(".java", "");
			simulatorClass = Class.forName(className, true, loader);
			simulatorClass.getMethod("main", new Class[] { String[].class })
					.invoke(simulatorClass.newInstance(),
							new Object[] { new String[] { waveName, "-f", outputWaveName } });
			
		} catch (ClassNotFoundException e) {
			Debug.barf(e.toString());
		} catch (IllegalAccessException e) {
			Debug.barf(e.toString());
		} catch (IllegalArgumentException e) {
			Debug.barf(e.toString());
		} catch (InvocationTargetException e) {
			//special case since we're getting a exception during execution of main(),
			//and "Invocation Target Exception" is rather unhelpful.
			//this can be caused by running a 32 bit dll with a 64 bit jdk
			Debug.barf(e.getCause().getMessage());
		} catch (NoSuchMethodException e) {
			Debug.barf(e.toString());
		} catch (SecurityException e) {
			Debug.barf(e.toString());
		} catch (InstantiationException e) {
			Debug.barf(e.toString());
		} 
		
		// compare the computed wave outputs
		final CommandLine csw = new CommandLine(outputWaveName);
		final WProgram studentW = WParboiledParser.parse(csw.readInputSpec());

		final CommandLine staffWaveCmd = new CommandLine(staffWavePath);
		final String StaffWave = staffWaveCmd.readInputSpec();
		final WProgram staffWProgram = WParboiledParser.parse(StaffWave);

		// check if staff/student programs are isomorphic
		assertTrue("wave outputs differ for simulation of " + inputSpec,
				staffWProgram.isomorphic(studentW));

		ExaminableProperties.checkAllUnary(staffWProgram);
		ExaminableProperties.checkAllUnary(studentW);
		ExaminableProperties.checkAllBinary(staffWProgram, studentW);

		// success!
		System.out.println("Success! " + inputSpec);
		System.out.println();
	}

	protected void computeFileNames(final String inputSpec, final FProgram fp) {
		// determine the name of the wave input to use for this formula
		final Set<String> inputVars = DetermineInputVars.inputVars(fp);
		final StringBuilder waveNameBuilder = new StringBuilder("tests/wave/");
		for (final String s : inputVars) {
			waveNameBuilder.append(s);
		}
		if (inputVars.isEmpty()) {
			waveNameBuilder.append("r1");
		}
		waveNameBuilder.append(".wave");
		waveName = waveNameBuilder.toString();
		assert (new File(waveName)).exists() : "input wave file doesn't exist: " + waveName;

		outputWaveName = inputSpec
				.replace(s + "f" + s, s + "f" + s + "student.out" + s + "simulator" + s)
				.replace(".f", ".wave");
		
		staffWavePath = inputSpec
				.replace(s + "f" + s, s + "f" + s + "staff.out" + s + "simulator" + s)
				.replace(".f", ".wave");
		assert ((new File(staffWavePath)).exists()) : "staff wave file does not exist: " + staffWavePath;

		sourcePath = inputSpec.replace(s + "f" + s, s + "f" + s + "student.out" + s + "simulator" + s + "Simulator_").replace(".f", ".java");
		final File f = new File(sourcePath);
		f.getParentFile().mkdirs();

	}

}
