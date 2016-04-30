#Si cambias la variable pch por "*", salen mas gordos los puntos que con ".", no se que  queda mejor.

datos = read.table("small2_grado-betweeness.txt")
x <- datos$V1
y <- datos$V2
png('small2_grado-betweeness.png')
plot(x,log(y), pch="*")
dev.off()

datos = read.table("small1_grado-betweeness.txt")
x <- datos$V1
y <- datos$V2
png('small1_grado-betweeness.png')
plot(x,log(y), pch="*")
dev.off()

datos = read.table("barabasi_grado-betweeness.txt")
x <- datos$V1
y <- datos$V2
png('barabasi_grado-betweeness.png')
plot(x,log(y), pch="*")
dev.off()


datos = read.table("erdos_grado-betweeness.txt")
x <- datos$V1
y <- datos$V2
png('erdos_grado-betweeness.png')
plot(x,log(y), pch="*")
dev.off()

datos = read.table("twitter_grado-betweeness.txt")
x <- datos$V1
y <- datos$V2
png('twitter_grado-betweeness.png')
plot(x,log(y), pch="*")
dev.off()


datos = read.table("fb_grado-betweeness.txt")
x <- datos$V1
y <- datos$V2
png('fb_grado-betweeness.png')
plot(x,log(y), pch="*")
dev.off()

