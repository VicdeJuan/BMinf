
echo "" > tex/precisiones.tex
for f in $(ls data/precisiones);do
	ruby precisiones.rb "data/precisiones/$f" $f >> tex/precisiones.tex
done

pdflatex "bmi-p1-12-precisiones"

Rscript generarGraficas.R

echo "" > tex/frecuencias.tex
for f in $(ls img/*.png); do
	echo "\\easyimg{$f}{}{}{0.5}" >> tex/frecuencias.tex
done

pdflatex "bmi-p1-12-frecuencias"
