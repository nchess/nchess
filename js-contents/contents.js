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
        txt += "  ".repeat(heading.depth) + heading.title + "<br/>";
    }
    txt += "</pre>";

    out.html(txt);
}
