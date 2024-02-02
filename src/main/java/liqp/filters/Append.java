package liqp.filters;

import liqp.TemplateContext;

public class Append extends Filter {

    /*
     * (Object) append(input, string)
     *
     * add one string to another
     */
    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        return super.asString(value, context) + super.asString(super.get(0, params), context);
    }
}
