
def convertToTex(file,msg)
	clueweb = msg.split("-")[1].split("\.")[0]
	queries = msg.split("-")[0]["Comparacion".length,msg.split("-")[0].length]
	puts "\\begin{table}"
	puts "\\centering"
	puts "\\begin{tabular}{cc}"
	File.foreach(file) {|x|
		print x.split("\t").join("&")
		puts "\\\\"
	}
	puts "\\end{tabular}"
	puts "\\caption{Precisi\\'{o}n en las #{queries} queries para el clueweb-#{clueweb}}"
	puts "\\end{table}"
end

convertToTex(ARGV[0],ARGV[1])
