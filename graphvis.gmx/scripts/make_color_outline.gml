//make_color_outline(color)
var c = argument0;
var hue = colour_get_hue(c);
var value = colour_get_value(c);
var saturation = colour_get_saturation(c);

if(value < 64)
    value += 32;
else 
    value -= 32;
    
return make_color_hsv(hue, saturation, value); 
