# this took like 5x longer than just typing it out manually lol
import itertools

variants = [
	[0, 1, 2],
	["north", "south", "east", "west", "up", "down"]
]

s = itertools.product(*variants)
json = """
{
	"variants": {
"""
for i in s:
	json += '\t\t"age=' + str(i[0]) + ",facing=" + i[1] + '": {\n'
	model = ""
	if i[0] == 0:
		model = "corruptworld:block/thorn_blossom_budding"
	elif i[0] == 1:
		model = "corruptworld:block/thorn_blossom_growing"
	elif i[0] == 2:
		model = "corruptworld:block/thorn_blossom_blooming"

	json += '\t\t\t"model": "' + model + '",\n'

	x = 0
	y = 0
	if i[1] == "north":
		x = 90
		y = 0
	elif i[1] == "south":
		x = 90
		y = 180
	elif i[1] == "east":
		x = 90
		y = 90
	elif i[1] == "west":
		x = 90
		y = 270
	elif i[1] == "up":
		x = 0
		y = 0
	elif i[1] == "down":
		x = 180
		y = 0

	json += '\t\t\t"x": ' + str(x) + ",\n"
	json += '\t\t\t"y": ' + str(y) + "\n"
	json += '\t\t},\n'

json = json[:-2] + '\n\t}\n}'
print(json)