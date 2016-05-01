
practica="P4"
mem="P4/Memoria"
name="bmi-p4-12"
mkdir $name


## Generación de javadoc
cd  $practica
ant javadoc
cd ..

jdoc=$practica"/dist/javadoc"
cp -r $jdoc $name
src=$practica"/src"
cp -r $src $name

zip -rv $name".zip" $name/*.pdf "$name/"javadoc
zip -ur $name".zip" $name"/src/es/uam/eps/bmi"

rm -r  $name
