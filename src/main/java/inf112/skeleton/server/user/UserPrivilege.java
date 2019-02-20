package inf112.skeleton.server.user;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum UserPrivilege {
    PLAYER(""),
    MODERATOR("MOD"),
    ADMIN("ADMIN");

    String prefix;

    UserPrivilege(String prefix) {
        this.prefix = prefix;
    }

    private static final Set<UserPrivilege> rights = Collections.unmodifiableSet(EnumSet.allOf(UserPrivilege.class));

    public static UserPrivilege getFromName(String name) {
        for (UserPrivilege e : rights) {
            if (e.getPrefix().equalsIgnoreCase(name))
                return e;
        }
        return null;
    }

    public String getPrefix() {
        return prefix;
    }
}