import os
import os.path as p

from PIL import Image

files = [f for f in os.listdir(os.getcwd()) if p.isfile(p.join(os.getcwd(), f)) and f.endswith(".png")]

for file in files:
    print("build:", file)

    img = Image.open(file)
    img = img.convert("RGBA")

    new_data = []
    for item in img.getdata():
        # If pixel is black, make it transparent
        if item[0] == 0 and item[1] == 0 and item[2] == 0:
            new_data.append((0, 0, 0, 0))
        else:
            new_data.append(item)

    img.putdata(new_data)
    img.save(file, "PNG")
