
echo "" > tex/precisiones.tex
for f in $(ls -r data/precisiones);do
	ruby precisiones.rb "data/precisiones/$f" $f >> tex/precisiones.tex
done

pdflatex "bmi-p1-12-precisiones" > /dev/null

# Preproceso

#cat data/frecuencias10K | sed -s s/,/\./g > /tmp/aux && cat /tmp/aux data/frecuencias10K
#cat data/frecuencias1K | sed -s s/,/\./g > /tmp/aux && cat /tmp/aux data/frecuencias1K
Rscript generarGraficas.R

echo "" > tex/frecuencias.tex
for f in $(ls img/*.png); do

	echo "\\\begin{center}\\\includegraphics{$f}\n\\\end{center}" >> tex/frecuencias.tex
done

pdflatex "bmi-p1-12-frecuencias" > /dev/null
