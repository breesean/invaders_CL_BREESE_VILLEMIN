import os
import os.path as p

from PIL import Image

files = [f for f in os.listdir(os.getcwd()) if p.isfile(p.join(os.getcwd(), f)) and f.endswith(".png")]

for file in files:
    print("edit:", file)

    img = Image.open(file)
    img = img.convert("RGBA")

    new_data = []
    for item in img.getdata():
        # If pixel is transparent, make it black
        if item[3] == 0:
            new_data.append((0, 0, 0, 255))
        else:
            new_data.append(item)

    img.putdata(new_data)
    img.save(file, "PNG")
