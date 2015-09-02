package de.webthing.desc;

/**
 * Created by Johannes on 02.09.2015.
 */
public class ThingDescription {

    private String name;

    /* I am not sure here, but since it's immutable and can contain unknown ones, I'll go for Arrays */
    private String[] protocols;
    private String[] encodings;

    private ActionDescription[] actions;
    private PropertyDescription[] properties;

    ThingDescription(String name, String[] protocols, String[] encodings, ActionDescription[] actions, PropertyDescription[] properties) {
        this.name = name;
        this.protocols = protocols;
        this.encodings = encodings;
        this.actions = actions;
        this.properties = properties;
    }

    public static ThingDescriptionBuilder getBuilder() {
        return new ThingDescriptionBuilder();
    }

    public static final class PropertyDescription {
        public String name;
        public boolean writable;
        public String outputType;
    }

    public static final class ActionDescription {
        public String name;
        public String outputType;
        public String inputType;
    }
}
