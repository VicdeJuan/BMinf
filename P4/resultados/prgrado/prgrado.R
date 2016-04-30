#Si cambias la variable pch por "*", salen mas gordos los puntos que con ".", no se que  queda mejor.


datos = read.table("small2_grado-pr.txt")
x <- datos$V1
y <- datos$V2
png('small2_grado-pr.png')
plot(x,log(y), pch="*")
dev.off()

datos = read.table("small1_grado-pr.txt")
x <- datos$V1
y <- datos$V2
png('small1_grado-pr.png')
plot(x,log(y), pch="*")
dev.off()

datos = read.table("barabasi_grado-pr.txt")
x <- datos$V1
y <- datos$V2
png('barabasi_grado-pr.png')
plot(x,log(y), pch="*")
dev.off()

datos = read.table("erdos_grado-pr.txt")
x <- datos$V1
y <- datos$V2
png('erdos_grado-pr.png')
plot(log(x),y, pch="*")
dev.off()

datos = read.table("twitter_grado-pr.txt")
x <- datos$V1
y <- datos$V2
png('twitter_grado-pr.png')
plot(log(x),y, pch="*")
dev.off()


datos = read.table("fb_grado-pr.txt")
x <- datos$V1
y <- datos$V2
png('fb_grado-pr.png')
plot(log(x),y, pch="*")
dev.off()

