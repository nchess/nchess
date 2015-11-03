///node_link(from, to)
with(argument0)
{
    if(!instance_exists(argument1))
        return false; 
        
    if(ds_list_find_index(neighbors, argument1) < 0)
    {
        ds_list_add(neighbors, argument1);
        dirty = true; 
        
        return true;
    }
    
    return false;
}

return false; 
