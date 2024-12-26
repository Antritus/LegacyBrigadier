package bet.astral.flunkie.registry;

public interface Identifier {
    static Identifier of(String key, String value){
        return new IdentifierImpl(key, value);
    }
    static Identifier of(String value){
        if (value.contains(":")){
            String[] split = value.split(":");
            return new IdentifierImpl(split[0], split[1]);
        }
        return new IdentifierImpl(null, value);
    }

    static Identifier minecraft(String value){
        return new IdentifierImpl("minecraft", value);
    }
    String getKey();
    String getValue();
    default String full(){
        return getKey()+":"+getValue();
    }

    class IdentifierImpl  implements Identifier{
        private final String key;
        private final String value;

        public IdentifierImpl(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }
    }
}
