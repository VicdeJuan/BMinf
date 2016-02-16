
practica="P1"
mem="Memoria"
name="bmi-p1-12"
mkdir $name

## Generación de los pdfs
cd $mem
zsh *.zsh
cp *.pdf ../$name
cd ..

## Generación de javadoc
cd  $practica
ant javadoc
cd ..

jdoc=$practica"/dist/javadoc"
cp -r $jdoc $name
src="P1/src"
cp -r $src $name

zip -rv $name".zip" $name/*.pdf "$name/"javadoc
zip -ur $name".zip" $name"/src/es/uam/eps/bmi/search"

rm -r  $name
