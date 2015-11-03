///genHexDisk(x,y, r,nr)
var cx, cy, r, nr, er;
cx = argument0;
cy = argument1;
r  = argument2; //graph radius
nr = argument3; //graph radius in nodes; i.e. how many times to expand
er = r / nr / sqrt(2);    //distance per expansion

var nx = 2*nr-1;
var ny = 2*nr+1;

var axis_p = dirvec2(0);
var axis_q = dirvec2(120);
var axis_s = dirvec2(240);

var nodegrid = ds_map_create();

for(var p = -nr; p <= nr; p++)
{
    for(var q = -nr; q <= nr; q++)
    {
        var s = -p-q;
        
        if(max(abs(p), abs(q), abs(s)) <= nr)
        {
            with(instance_create(0,0, node))
            {
                x = cx + er*(axis_p[0]*p + axis_q[0]*q + axis_s[0]*s);
                y = cy + er*(axis_p[1]*p + axis_q[1]*q + axis_s[1]*s);
                ds_map_add(nodegrid, string_args(q,p,s), id); 
            }
        }
    }
}

//Link nodes
for(var p = -nr; p <= nr; p++)
{
    for(var q = -nr; q <= nr; q++)
    {
        var s = -p-q;
        
        if(max(abs(p), abs(q), abs(s)) <= nr)
        {
            for(var axis = 0; axis < 3; axis++)
            {
                for(var signs = 0; signs < 4; signs++)
                {
                    var bp = p;
                    var bq = q;
                    var bs = s;
                    
                    if(axis == 0)
                    {
                        bq += lerp(1,-1, (signs&1) != 0);
                        bs += lerp(1,-1, (signs&2) != 0);
                    }
                    else if(axis == 1)
                    {
                        bp += lerp(1,-1, (signs&1) != 0);
                        bs += lerp(1,-1, (signs&2) != 0);
                    }
                    else if(axis == 2)
                    {
                        bp += lerp(1,-1, (signs&1) != 0);
                        bq += lerp(1,-1, (signs&2) != 0);
                    }
                    
                    //rtdbg("Trying to link ", string_args(p,q,s), " to ", string_vec(at), " after offset ", string_vec(offset));
                    
                    if(ds_map_exists(nodegrid, string_args(bp,bq,bs)))
                    {
                        node_bilink(nodegrid[?string_args(p,q,s)], nodegrid[?string_args(bp,bq,bs)]);
                        //rtdbg("Linked ", string_args(p,q,s), " to ", string_vec(at));
                    }
                }
            }
        }
    }
}

//Sort links
for(var k = ds_map_find_first(nodegrid); true; k = ds_map_find_next(nodegrid, k))
{
    node_sort_links(nodegrid[?k]);
    with(nodegrid[?k])
    {
        selectable = (ds_list_size(neighbors) == 6);
        active = selectable; 
    }
    
    if(k == ds_map_find_last(nodegrid))
        break;
}

ds_map_destroy(nodegrid); 
