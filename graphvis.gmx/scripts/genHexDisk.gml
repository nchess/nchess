///genHexDisk(x,y, r,nr)
var cx, cy, r, nr, er;
cx = argument0;
cy = argument1;
r  = argument2; //graph radius
nr = argument3; //graph radius in nodes; i.e. how many times to expand
er = r / nr;    //distance per expansion

var nx = 2*nr;
var ny = 2*nr+1;

var nodegrid = ds_grid_create(nx,ny);

//Fill grid with nodes
//Generate a rhombus and shred its two corners
for(var i = 0; i < nx; i++)
{
    for(var j = 0; j <= nr; j++)
    {
        if(j+i < nr)
        {
            nodegrid[#i,j] = noone;
            continue; 
        }
        
        var xf = (i + j/2 - nr/2) / max(nx-1, 1);
        var yf = j / max(ny-1, 1);
        
        var vx = cx + lerp(-r, r, xf);
        var vy = cy + lerp(-r, r, yf);
        
        nodegrid[#i,j] = instance_create(vx, vy, node);
    }
    
    for(var j = nr+1; j < ny; j++)
    {
        if(i >= 3*nr - j)
        {
            nodegrid[#i,j] = noone;
            continue; 
        }
        
        var xf = (i + j/2 - nr/2) / max(nx-1, 1);
        var yf = j / max(ny-1, 1);
        
        var vx = cx + lerp(-r, r, xf);
        var vy = cy + lerp(-r, r, yf);
        
        nodegrid[#i,j] = instance_create(vx, vy, node);
    }
}

//Link nodes
for(var i = 0; i < nx; i++)
{
    for(var j = 0; j < ny; j++)
    {
        if(i+1 < nx) node_bilink(nodegrid[#i,j], nodegrid[#i+1,j]); 
        if(i-1 >= 0) node_bilink(nodegrid[#i,j], nodegrid[#i-1,j]);
        
        if(j-1 >= 0) node_bilink(nodegrid[#i,j], nodegrid[#i,j-1]);
        if(j+1 < ny) node_bilink(nodegrid[#i,j], nodegrid[#i,j+1]);
        
        if(i-1 >= 0 && j+1 < ny)
            node_bilink(nodegrid[#i,j], nodegrid[#i-1, j+1]);
    }
}

//Sort links
for(var i = 0; i < nx; i++)
{
    for(var j = 0; j < ny; j++)
    {
        node_sort_links(nodegrid[#i,j]); 
        with(nodegrid[#i,j]) 
            active = (ds_list_size(neighbors) == 6);
    }
}

ds_grid_destroy(nodegrid); 
