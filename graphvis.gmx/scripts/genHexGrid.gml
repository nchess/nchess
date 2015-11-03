///genHexGrid(x,y, w,h, nx,ny)
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
        var xf = (i + (j mod 2)/2) / max(nx-1, 1);
        var yf = j / max(ny-1, 1);
        
        var vx = cx + lerp(-w/2, w/2, xf);
        var vy = cy + lerp(-h/2, h/2, yf);
        
        nodegrid[#i,j] = instance_create(vx, vy, node);
        
        if(i == 0 || j == 0)
            nodegrid[#i, j].active = false; 
        if(i == nx-1 || j == ny-1)
            nodegrid[#i, j].active = false; 
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
        
        if((j mod 2) == 1 && i+1 < nx && j+1 < ny)
            node_bilink(nodegrid[#i,j], nodegrid[#i+1, j+1]);
        if((j mod 2) == 0 && i-1 >= 0 && j+1 < ny)
            node_bilink(nodegrid[#i,j], nodegrid[#i-1, j+1]);
    }
}

//Sort links
for(var i = 0; i < nx; i++)
    for(var j = 0; j < ny; j++)
        node_sort_links(nodegrid[#i,j]); 

ds_grid_destroy(nodegrid); 
