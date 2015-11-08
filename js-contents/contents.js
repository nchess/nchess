function contents(root, out)
{
    findings = new Array();
    pattern = /H[0-9]+/i;

    root.children().each(function() {
        var name = $(this).prop('tagName');

        if(pattern.test(name))
        {
            var depth = parseInt(name.slice(1));
            findings.push(
                {
                    "depth": depth,
                    "title": $(this).html(),
                    "item": $(this)
                }
            );
        }
    });

    var txt = "<pre>";
    for(var i in findings) {
        var heading = findings[i];
        var genId = heading.item.text();
            genId = genId.replace(" ", "-");
            genId = genId.replace(/[^a-zA-Z0-9-]/, "");

        heading.item.attr('id', genId);
        txt += "  ".repeat(heading.depth) + "<a href=\"#"+genId+"\">" + heading.title + "</a><br/>";
    }
    txt += "</pre>";

    out.html(txt);
}
