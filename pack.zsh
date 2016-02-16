
practica="P1"
mem="Memoria"
name="bmi-p1-12"
## Generación de los pdfs
cd $mem
zsh *.zsh
cp *.pdf ..
cd ..

## Generación de javadoc
cd  $practica
ant javadoc
cd ..

jdoc=$practica"/dist/javadoc"
cp -r $jdoc .
src="P1/src"
cp -r $src .

zip -rv $name *.pdf javadoc
zip -ur $name "src/es/uam/eps/bmi/search"

rm -r "javadoc" *.pdf "src"
