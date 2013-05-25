include ../Makefile.common

# targets for recognition and parsing
.PHONY: testrecognizeh testrecognizep testparseh testparsep 
# targets for transformations
.PHONY: testsimplifyh testsimplifyp
.PHONY: testsimulatorp
# targets for graphing
.PHONY: testgraphp testgraphh
# targets for cleaning
.PHONY: clean cleansimplified
# real suffixes
.SUFFIXES: .f 
# fake suffixes used to name the output of tests
.SUFFIXES: .frh .frp .fph .fpp .frhb .frpb .fphb .fppb .diff


FORMULAS = $(shell find . -maxdepth 1 -type f -name '*.f' | sort) 
BADFORMULAS = $(shell find ungrammatical -maxdepth 1 -type f -name '*.f' | sort) 
GRAPHS = $(FORMULAS:.f=.dot)

help: commonhelp
	@echo "select a target:"
	@echo "    testrecognizeh   -- run hand recognizer on all .f files"
	@echo "    testrecognizep   -- run parboiled recognizer on all .f files"
	@echo "    testparseh       -- run hand parser on all .f files"
	@echo "    testparsep       -- run parboiled parser on all .f files"
	@echo "    testsimplifyh    -- run hand simplifier on all .f files, compare output"
	@echo "    testsimplifyp    -- run parboiled simplifier on all .f files, compare output"
	@echo "    testgraphp       -- run technologymapper on all .f files, compare output"
	@echo "    testsimulatorp   -- run simulator on all .f files, compare output"
	@echo "    clean            -- clean all generated files"
	@echo "    cleansimplified  -- clean all simplified files"
	@echo "    cleangraph       -- clean all graph files"
	@echo "    cleansimulator   -- clean all simulator files"
	@echo "Use SOLNVARIANT to set a different variant directory for expected outputs"


########################################################################
#### RECOGNIZE & PARSE #################################################
########################################################################

testrecognizeh: classes $(FORMULAS:.f=.frh) $(BADFORMULAS:.f=.frhb)
	@echo success!

testrecognizep: classes $(FORMULAS:.f=.frp) $(BADFORMULAS:.f=.frpb)
	@echo success!

testparseh: classes $(FORMULAS:.f=.fph) $(BADFORMULAS:.f=.fphb)
	@echo success!

testparsep: classes $(FORMULAS:.f=.fpp) $(BADFORMULAS:.f=.fppb)
	@echo success!

%.frh: %.f
	$(JVM) ece351.f.hand.FHandRecognizer $(ARGS) $<

%.frhb: %.f
	! $(JVM) ece351.f.hand.FHandRecognizer $(ARGS) $<

%.frp: %.f
	$(JVM) ece351.f.parboiled.FRecognizer $(ARGS) $<

%.frpb: %.f
	! $(JVM) ece351.f.parboiled.FRecognizer $(ARGS) $<

%.fph: %.f
	$(JVM) ece351.f.hand.FHandParser $(ARGS) $<

%.fphb: %.f
	! $(JVM) ece351.f.hand.FHandParser $(ARGS) $<

%.fpp: %.f
	$(JVM) ece351.f.parboiled.FParser $(ARGS) $<

%.fppb: %.f
	! $(JVM) ece351.f.parboiled.FParser $(ARGS) $<


########################################################################
#### SIMPLIFY ##########################################################
########################################################################

# TODO: these targets are not actually invoking TXL. Instead we are
# running against pre-computed solutions for now.

SimpleFtxl = $(addprefix txl.out/simplified/,$(FORMULAS))
SimpleFhand = classes $(addprefix hand.out/simplified/,$(FORMULAS))
SimpleFparb = classes $(addprefix parb.out/simplified/,$(FORMULAS))
SimpleFdiffHand = $(SimpleFhand:.f=.diff)
SimpleFdiffParb = $(SimpleFparb:.f=.diff)

testsimplifyh: $(SimpleFhand) $(SimpleFdiffHand)
	@echo success!

testsimplifyp: $(SimpleFparb) $(SimpleFdiffParb)
	@echo success!

txl.out/simplified/%.f : %.f
	nice txl $< absorber.Txl > $@

hand.out/simplified/%.f : %.f
	$(JVM) ece351.f.Simplifier -h $(ARGS) $< > $@

hand.out/simplified/%.diff : hand.out/simplified/%.f soln.out/simplified/%.f 
	nice diff $(DIFFFLAGS) $^

parb.out/simplified/%.f : %.f
	$(JVM) ece351.f.Simplifier -p $(ARGS) $< > $@

parb.out/simplified/%.diff : parb.out/simplified/%.f soln.out/simplified/%.f 
	nice diff $(DIFFFLAGS) $^

cleansimplified: 
	rm -rf hand.out/simplified/*
	rm -rf parb.out/simplified/*
	rm -rf txl.out/simplified/*


########################################################################
#### GRAPH #############################################################
########################################################################

GraphFhand = classes $(addprefix hand.out/graph/,$(GRAPHS))
GraphFparb = classes $(addprefix parb.out/graph/,$(GRAPHS))
GraphFdiff = $(GraphFparb:.dot=.diff)
GraphFpng = $(GraphFparb:.dot=.png)

testgraphp: $(GraphFparb) $(GraphFpng) $(GraphFdiff)
	@echo success!

hand.out/graph/%.dot : %.f
	$(JVM) ece351.f.TechnologyMapper -h $(ARGS) $< > $@

parb.out/graph/%.dot : %.f
	$(JVM) ece351.f.TechnologyMapper $(ARGS) $< > $@

%.png : %.dot
	nice dot -Tpng -o $@ $<

parb.out/graph/%.diff : parb.out/graph/%.dot soln.out/graph$(SOLNVARIANT)/%.dot
	nice diff $(DIFFFLAGS) $^

cleangraph: 
	rm -rf parb.out/graph/*
	rm -rf hand.out/graph/*
	rm -rf txl.out/graph/*

########################################################################
#### SIMULATOR #########################################################
########################################################################

F_FILES = $(wildcard *.f)
SIMULATOR_JAVA_FILES = $(addprefix parb.out/simulator/Simulator_,$(F_FILES:.f=.java))
SIMULATOR_CLASS_FILES = $(SIMULATOR_JAVA_FILES:.java=.class)
SIMULATOR_WAVE_FILES = $(SIMULATOR_JAVA_FILES:.java=.wave)
SIMULATOR_DIFF_FILES = $(SIMULATOR_JAVA_FILES:.java=.diff)

testsimulatorp: classes $(SIMULATOR_JAVA_FILES) $(SIMULATOR_CLASS_FILES) $(SIMULATOR_WAVE_FILES) $(SIMULATOR_DIFF_FILES) 
	@echo success!

parb.out/simulator/Simulator_%.java : %.f
	$(JVM) ece351.f.SimulatorGenerator $(ARGS) $< > $@

parb.out/simulator/Simulator_%.class : parb.out/simulator/Simulator_%.java
	$(JAVAC) $<

# TODO: change generated simulators to use CommandLine class and accept -v
parb.out/simulator/Simulator_%.wave : parb.out/simulator/Simulator_%.class wave.map
	f=$(basename $(notdir $<)); g=$${f//Simulator_/}; $(JVM):parb.out/simulator $$f `gawk "/$$g/ {print \\$$2;}" wave.map` > $@

parb.out/simulator/Simulator_%.diff : parb.out/simulator/Simulator_%.wave soln.out/simulator$(SOLNVARIANT)/Simulator_%.wave
	nice diff $(DIFFFLAGS) $^

cleansimulator: 
	rm -rf parb.out/simulator/*

########################################################################
#### CLEAN #############################################################
########################################################################
clean: cleansimplified cleangraph cleansimulator

