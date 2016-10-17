package liqp.tags;

import liqp.Template;
import liqp.nodes.LNode;
import liqp.parser.Flavor;

import java.io.File;
import java.util.Map;

public class Include extends Tag {

    public static final String INCLUDES_DIRECTORY_KEY = "liqp@includes_directory";
    public static String DEFAULT_EXTENSION = ".liquid";

    @Override
    public Object render(Map<String, Object> context, LNode... nodes) {

        // This value will always be defined: either a custom file set by the
        // user, or else inside TagNode.
        File includesDirectory = (File)context.get(INCLUDES_DIRECTORY_KEY);

        try {
            String includeResource = super.asString(nodes[0].render(context));
            String extension = DEFAULT_EXTENSION;
            if(includeResource.indexOf('.') > 0) {
                extension = "";
            }
            File includeResourceFile = new File(includesDirectory, includeResource + extension);
            Template include = Template.parse(includeResourceFile, (Flavor) context.get(Flavor.KEY));

            // check if there's a optional "with expression"
            if(nodes.length > 1) {
                Object value = nodes[1].render(context);
                context.put(includeResource, value);
            }

            return include.render(context);

        } catch(Exception e) {
            return "";
        }
    }
}
