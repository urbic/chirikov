.DEFAULT_GOAL=SCM.mp4

THREADS=0
FRAMERATE=30
INITIAL=0
FINAL=6
STEP=.001
RHO=.001
DENSITY=1200
IMAGES=$(shell env LANG=C seq -fSCM-%1.3f.png $(INITIAL) $(STEP) $(FINAL))

%.class: %.java
	@javac $<

SCM-%.png: SCM.class
	@java SCM $(subst SCM-,,$(@:.png=)) $(RHO) $(DENSITY) $(THREADS)

SCM.mp4: $(IMAGES)
	@cat $^ |ffmpeg -y -framerate $(FRAMERATE) -i - -r $(FRAMERATE) -pix_fmt gray -preset veryslow $@
