#Si cambias la variable pch por "*", salen mas gordos los puntos que con ".", no se que  queda mejor.

datos = read.table("barabasi_grado.txt")
x <- datos$V1
y <- datos$V2
png('barabasi_grado.png')
plot(x,y, pch="*")
dev.off()

datos = read.table("erdos_grado.txt")
x <- datos$V1
y <- datos$V2
png('erdos_grado.png')
plot(x,y, pch="*")
dev.off()

datos = read.table("twitter_grado.txt")
x <- datos$V1
y <- datos$V2
png('twitter_grado.png')
plot(x,y, pch="*")
dev.off()


datos = read.table("fb_grado.txt")
x <- datos$V1
y <- datos$V2
png('fb_grado.png')
plot(x,y, pch="*")
dev.off()

