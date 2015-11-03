///node_link(from, to)
with(argument0)
{
    if(ds_list_find_index(neighbors, argument1) < 0)
    {
        ds_list_add(neighbors, argument1);
        return true;
    }
    
    return false;
}
