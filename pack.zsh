
practica="P3"
mem="Memoria"
name="bmi-p3-12"
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
src=$practica"/src"
cp -r $src $name

zip -rv $name".zip" $name/*.pdf "$name/"javadoc
zip -ur $name".zip" $name"/src/es/uam/eps/bmi/search"

rm -r  $name
