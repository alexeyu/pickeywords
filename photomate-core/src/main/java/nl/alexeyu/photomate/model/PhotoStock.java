package nl.alexeyu.photomate.model;

import java.util.Objects;

public final class PhotoStock {

    private final String name;

    private final String iconUrl;

    private final PhotoStockAccess access;

    public PhotoStock(String name, String iconUrl, PhotoStockAccess access) {
        this.name = name;
        this.iconUrl = iconUrl;
        this.access = access;
    }

    public String name() {
        return name;
    }

    public String iconUrl() {
        return iconUrl;
    }

    public PhotoStockAccess ftpEndpoint() {
        return access;
    }

    @Override
    public String toString() {
        return "PhotoStock [" + name + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof PhotoStock))
            return false;
        PhotoStock other = (PhotoStock) obj;
        return Objects.equals(name, other.name);
    }

}
