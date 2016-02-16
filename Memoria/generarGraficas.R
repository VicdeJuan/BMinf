#Si cambias la variable pch por "*", salen mas gordos los puntos que con ".", no se que  queda mejor.




#_____________ Frecuencias 1K ___________________
datos = read.table("data/frecuencias1K")
x <- 1:length(datos$V1)
print("frecuencias 1K...")
#frecuencia
y <- datos$V2
png('img/frecuencias1k.png')
plot(x,log(y), pch="*", col="blue", xlab="termino", ylab="frecuencia", main="frecuencias 1k")
dev.off()

print("numdocs 1K...")
#num docs
y <- datos$V3
png('img/numdocs1k.png')
plot(x,log(y), pch=".", col="blue", xlab="termino", ylab="nº docs", main="nº docs 1k")
dev.off()

print("tf 1K...")
#tf
y <- datos$V4
png('img/tf1k.png')
plot(x,log(y), pch=".", col="blue", xlab="termino", ylab="tf", main="tf 1k")
dev.off()

#idf
print("idf 1K...")
y <- datos$V5
png('img/idf1k.png')
plot(x,log(y), pch=".", col="blue", xlab="termino", ylab="idf", main="idf 1k")
dev.off()

#_____________ Frecuencias 10K ___________________
datos = read.table("data/frecuencias10K")
x <- 1:length(datos$V1)

#frecuencia
print("frecuencias 10K...")
y <- datos$V2
png('img/frecuencias10k.png')
plot(x,log(y), pch="*", col="blue", xlab="termino", ylab="frecuencia", main="frecuencias 10k")
dev.off()

#num docs
print("numdocs 10K...")
y <- datos$V3
png('img/numdocs10k.png')
plot(x,log(y), pch=".", col="blue", xlab="termino", ylab="nº docs", main="nº docs 10k")
dev.off()

#tf
print("tf 10K...")
y <- datos$V4
png('img/tf10k.png')
plot(x,log(y), pch=".", col="blue", xlab="termino", ylab="tf", main="tf 10k")
dev.off()

#idf
print("idf 10K...")
y <- datos$V5
png('img/idf10k.png')
plot(x,log(y), pch=".", col="blue", xlab="termino", ylab="idf", main="idf 10k")
dev.off()

