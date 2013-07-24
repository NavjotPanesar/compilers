package ece351;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

import ece351.f.FParser;
import ece351.f.InlineIntermediateVariables;
import ece351.f.TechnologyMapper;
import ece351.f.TestSimulatorGenerator;
import ece351.f.ast.FProgram;
import ece351.util.CommandLine;
import ece351.util.Utils351;
import ece351.vhdl.DeSugarer;
import ece351.vhdl.Elaborator;
import ece351.vhdl.Splitter;
import ece351.vhdl.Synthesizer;
import ece351.vhdl.VParser;
import ece351.vhdl.ast.VProgram;
import ece351.w.svg.TransformW2SVG;

public class TestEnd2End {

	final static File studentOut = newFile("tests/vhdl/student.out/end2end");
	final static File staffOut = newFile("tests/vhdl/staff.out/end2end/");

	private static File newFile(final String s) {
		return new File(s.replace("/", TestSimulatorGenerator.s));
	}
	
	@Test 
	public void testFullAdder() throws IOException {
		doit("full_adder");
	}
	
	@Test
	public void testRippleCarryAdder() throws IOException {
		final String fbrca = "four_bit_ripple_carry_adder";
		doit(fbrca);
	}
	
	private void doit(final String fbrca) throws IOException {
		final File vhd = Utils351.files("tests/vhdl", fbrca + ".vhd")[0];
		
		// parse and transform VHDL
		VProgram v = VParser.parse(new CommandLine(vhd.getAbsolutePath()).readInputSpec()); assert v.repOk();
		v = DeSugarer.desugar(v); assert v.repOk();
		v = Elaborator.elaborate(v); assert v.repOk();
		v = Splitter.split(v); assert v.repOk();
		// synthesize VHDL to F
		FProgram f = Synthesizer.synthesize(v); assert f.repOk();
		f = f.simplify(); assert f.repOk();
		f = InlineIntermediateVariables.inline(f); assert f.repOk();
		
		// render the circuit
		studentOut.mkdirs();
		final File dot = new File(studentOut, fbrca + ".dot");
		TechnologyMapper.render(f, new PrintWriter(new FileWriter(dot)));
		final File outF = new File(studentOut, fbrca + ".f");
		final PrintWriter outFpw = new PrintWriter(new FileWriter(outF));
		outFpw.print(f.toString());
		outFpw.close();
		
		final String outputWaveName_ = newPath(studentOut, fbrca + "_out.wave");
		
		// generate and test the simulator
		final TestSimulatorGenerator tsg = new TestSimulatorGenerator(outF){
			@Override
			protected void computeFileNames(final String inputSpec, final FProgram fp) {
				waveName = newPath(staffOut, fbrca + "_in.wave");
				outputWaveName = outputWaveName_;
				staffWavePath = newPath(staffOut, fbrca + "_out.wave");
				sourcePath = newPath(studentOut, "Simulator_" + fbrca + ".java");;
			}
		};
		tsg.simgen();
		
		// draw the output wave file in SVG
		final String svgName = newPath(studentOut, fbrca + ".svg");
		final CommandLine csvg = new CommandLine("-p", "-f", svgName, outputWaveName_);
		TransformW2SVG.main(csvg);

	}

	private static String newPath(final File dir, final String fname) {
		return (new File(dir, fname)).getAbsolutePath();
	}
}
