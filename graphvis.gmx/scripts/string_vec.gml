///string_vec(vec)
var ret = "[";

for(var i = 0; i < array_length_1d(argument0); i++)
    ret += string(argument0[i]) + ", ";
    
return string_copy(ret, 0, string_length(ret)-2) + "]";
