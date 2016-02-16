#Si cambias la variable pch por "*", salen mas gordos los puntos que con ".", no se que  queda mejor.


setwd("~/Escritorio/practica BMinf") #directorio donde se encuentran los txt de frecuencias


#_____________ Frecuencias 1K ___________________
datos = read.table("frecuencias1K.txt")
x <- 1:length(datos$V1)

#frecuencia
y <- datos$V2
png('frecuencias1k.png')
plot(x,log(y), pch="*", col="blue", xlab="termino", ylab="frecuencia", main="frecuencias 1k")
dev.off()

#num docs
y <- datos$V3
png('numdocs1k.png')
plot(x,log(y), pch=".", col="blue", xlab="termino", ylab="nº docs", main="nº docs 1k")
dev.off()

#tf
y <- datos$V4
png('tf1k.png')
plot(x,log(y), pch=".", col="blue", xlab="termino", ylab="tf", main="tf 1k")
dev.off()

#idf
y <- datos$V5
png('idf1k.png')
plot(x,log(y), pch=".", col="blue", xlab="termino", ylab="idf", main="idf 1k")
dev.off()

#_____________ Frecuencias 10K ___________________
datos = read.table("frecuencias10K.txt")
x <- 1:length(datos$V1)

#frecuencia
y <- datos$V2
png('frecuencias10k.png')
plot(x,log(y), pch="*", col="blue", xlab="termino", ylab="frecuencia", main="frecuencias 10k")
dev.off()

#num docs
y <- datos$V3
png('numdocs10k.png')
plot(x,log(y), pch=".", col="blue", xlab="termino", ylab="nº docs", main="nº docs 10k")
dev.off()

#tf
y <- datos$V4
png('tf10k.png')
plot(x,log(y), pch=".", col="blue", xlab="termino", ylab="tf", main="tf 10k")
dev.off()

#idf
y <- datos$V5
png('idf10k.png')
plot(x,log(y), pch=".", col="blue", xlab="termino", ylab="idf", main="idf 10k")
dev.off()

