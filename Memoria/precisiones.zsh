
for f in $(ls data/precisiones);do
	ruby precisiones.rb "data/precisiones/$f" $f >> tex/precisiones.tex
done

pdflatex "bmi-p1-12-precisiones"
