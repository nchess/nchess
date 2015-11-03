///genSquareGrid(x,y, w,h, nx,ny)
var cx, cy, w,h, nx,ny;
cx = argument0;
cy = argument1;
w  = argument2;
h  = argument3;
nx = argument4;
ny = argument5;

var nodegrid = ds_grid_create(nx, ny);

//Fill grid with nodes
for(var i = 0; i < nx; i++)
{
    for(var j = 0; j < ny; j++)
    {
        var xf = i / max(nx-1, 1);
        var yf = j / max(ny-1, 1);
        
        var vx = cx + lerp(-w/2, w/2, xf);
        var vy = cy + lerp(-h/2, h/2, yf);
        
        nodegrid[#i,j] = instance_create(vx, vy, node);
    }
}

//Link nodes
for(var i = 0; i < nx; i++)
{
    for(var j = 0; j < ny; j++)
    {
        if(i+1 < nx) node_bilink(nodegrid[#i,j], nodegrid[#i+1,j]);
        if(j-1 >= 0) node_bilink(nodegrid[#i,j], nodegrid[#i,j-1]);
        if(i-1 >= 0) node_bilink(nodegrid[#i,j], nodegrid[#i-1,j]);
        if(j+1 < ny) node_bilink(nodegrid[#i,j], nodegrid[#i,j+1]);
        
        node_sort_links(nodegrid[#i,j]);
    }
}

ds_grid_destroy(nodegrid); 
