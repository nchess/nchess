///node_sort_links(node)
with(argument0)
{
    var dirlist = ds_list_create();
    var dirmap = ds_map_create();
    
    for(var i = 0; i < ds_list_size(neighbors); i++)
    {
        var d = point_direction(x,y, neighbors[|i].x, neighbors[|i].y);
        ds_list_add(dirlist, d);
        ds_map_add(dirmap, d, neighbors[|i]);
    }
    
    ds_list_sort(dirlist, 1);
    ds_list_clear(neighbors);
    for(var i = 0; i < ds_list_size(dirlist); i++)
        ds_list_add(neighbors, dirmap[?dirlist[|i]]);
        
    ds_list_destroy(dirlist);
    ds_map_destroy(dirmap); 
    
    return true; 
}

return false;
