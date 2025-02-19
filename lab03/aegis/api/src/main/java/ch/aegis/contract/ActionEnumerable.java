package ch.aegis.contract;

/**
 * Base contract for action enumerations, the corresponding enum will be generated automatically.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
public interface ActionEnumerable<E extends Enum<E> & ActionEnumerable<E>> {

    /**
     * Returns the list of attributes that will need to be fetched to evaluate policies for the
     * given action. This will be used when evaluating the policies to collect the required
     * attributes from the context at runtime.
     * <p>
     * Aegis uses two methods of fetching attributes, first it attempts to find a
     * {@link AttributeProvider} for the attribute, if none is found it will attempt to find an
     * {@link AttributeResolver} that can convert any of the intercepted endpoint method arguments
     * to an attribute (though if there are multiple resolvers that can resolve the same argument,
     * it will throw an exception).
     */
    Class<?>[] getRequiredAttributes();
}
