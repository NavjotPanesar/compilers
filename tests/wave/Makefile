include ../Makefile.common


# Makefile to generate svg files from wave files

help: commonhelp
	@echo "select a target:"
	@echo "    recognizeregex    -- run regex recognizer on all .w files"
	@echo "    recognizeparb     -- run parboiled recognizer on all .w files"
	@echo "    parseparb         -- run parboiled parser on all .w files"
	@echo "    w2svgtxl          -- transform waves to svgs using TXL"
	@echo "    w2svghand         -- transform waves to svgs using hand parser"
	@echo "    w2svgparb         -- transform waves to svgs using parboiled parser"
	@echo "    classes           -- compile your Java code"
	@echo "    clean             -- delete output files"


########################################################################
#### FILE QUERIES ######################################################
########################################################################

.PHONY: all test classes clean cleantest
.SUFFIXES: .wave .badwave .grep .badgrep .svg .diff

# we call bash to generate the names of the input wave files
WAVES := $(shell find . -type f -name '*.wave')
BADWAVES := $(shell find . -type f -name '*.badwave')
# generate the names of the output files
SVGS := $(WAVES:.wave=.svg)
TXLSVGS := $(addprefix txl.out/,$(SVGS))
HANDSVGS := $(addprefix hand.out/,$(SVGS))
HANDDIFFS := $(addprefix hand.out/,$(SVGS:.svg=.diff))
PARBSVGS := $(addprefix parb.out/,$(SVGS))
PARBDIFFS := $(addprefix parb.out/,$(SVGS:.svg=.diff))
GREPS := $(HANDDIFFS:.diff=.grep)
BADGREPS := $(addprefix hand.out/,$(BADWAVES:.badwave=.badgrep))


########################################################################
#### REGULAR EXPRESSION RECOGNIZER #####################################
########################################################################
recognizeregex: $(GREPS) $(BADGREPS)
	@echo "success!"
# read the regular expression from separate file
include $(JAVADIR)/wave/Makefile.regex
# accept the good waves
# double negative: if no lines fail to match, then accept
hand.out/%.grep : %.wave
	! grep -v -E --line-regexp " *" $< | grep -v -E --quiet --line-regexp "$(REGEX)"
    
# reject the bad waves
# if any line is bad, then reject
hand.out/%.badgrep : %.badwave
	! grep -E --quiet --line-regexp "$(REGEX)" $<

########################################################################
#### RECOGNIZE with PARBOILED ##########################################
########################################################################
recognizeparb: classes $(WAVES:.wave=.wrp) $(BADWAVES:.badwave=.wrpb)
	@echo success!

%.wrp: %.wave
	$(JVM) ece351.w.parboiled.WParboiledRecognizer $< 

%.wrpb: %.badwave
	! $(JVM) ece351.w.parboiled.WParboiledRecognizer $< 

########################################################################
#### PARSE with PARBOILED ##############################################
########################################################################
parseparb: classes $(WAVES:.wave=.wpp) $(BADWAVES:.badwave=.wppb)
	@echo success!

%.wpp: %.wave
	$(JVM) ece351.w.parboiled.WParboiledParser $< 

%.wppb: %.badwave
	! $(JVM) ece351.w.parboiled.WParboiledParser $< 


########################################################################
#### TRANSFORMER #######################################################
########################################################################

# transform waves to svgs by TXL
w2svgtxl: $(TXLSVGS)
	@echo success!
txl.out/%.svg : %.wave
	nice txl $< > $@

# transform waves to svgs using hand-written W parser
w2svghand: classes $(HANDSVGS) $(HANDDIFFS)
	@echo success!

hand.out/%.svg : %.wave
	$(JVM) ece351.w.W2SVG -hand $< > $@

hand.out/%.diff : txl.out/%.svg hand.out/%.svg
	nice diff $(DIFFFLAGS) $^
	nice diff $(DIFFFLAGS) $^ > $@

# transform waves to svgs using W parser written with parboiled
w2svgparb: classes $(PARBSVGS) $(PARBDIFFS)
	@echo success!

parb.out/%.svg : %.wave
	$(JVM) ece351.w.W2SVG -parb $< > $@

parb.out/%.diff : txl.out/%.svg parb.out/%.svg
	nice diff $(DIFFFLAGS) $^
	nice diff $(DIFFFLAGS) $^ > $@



########################################################################
#### CLEAN #############################################################
########################################################################
cleantest:
	$(RM) hand.out/*.diff
	$(RM) hand.out/*.grep
	$(RM) hand.out/*.badgrep
	$(RM) parb.out/*.diff
	
clean: cleantest
	$(RM) hand.out/*.svg
	$(RM) parb.out/*.svg
