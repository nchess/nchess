///string_args(...)
var ret = "[";

for(var i = 0; i < argument_count; i++)
    ret += string(argument[i]) + ", ";
    
return string_copy(ret, 0, string_length(ret)-2) + "]";
