#!/usr/bin/ruby

# Copyright (c) 2013 Shane Quigley
# 
# This software is MIT licensed see link for details
# http://www.opensource.org/licenses/MIT

require "open3"

def bash input
	streams = Open3.popen3 input
	out = ""
	while((line=streams[1].gets) != nil)
		out += line
	end
	return out
end

def get_string_xml(current, totranslate, xmltemplate)
	out = xmltemplate
	toreplace = totranslate.split(",")
	c = 0
	current.split(",").each do |translation|
		if(c > 2 && c != toreplace.size-1)
			out = out.sub(toreplace[c],translation)
		end
		c += 1
	end
	out
end

csv = File.open(ARGV[0], "rb")
stringxml = File.open(ARGV[1], "rb").read
lines = csv.readlines
first = lines[0]
bash "mkdir translations"
lines.each_index do |i|
	if(i != 0)
		bash "cd translations; mkdir values-#{lines[i].split(",")[-1]}"
		file_path = "translations/values-#{lines[i].split(",")[-1].chomp}/strings.xml"
		File.open(file_path, "w") do |f| 
			f.write(get_string_xml(lines[i], first, stringxml))
		end
	end
end
