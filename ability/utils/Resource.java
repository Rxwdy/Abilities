package services.coral.ability.utils;

public class Resource {
    private boolean external;
    private SpigotFile file;

    public Resource(SpigotFile file, boolean external){
        this.file = file;
        this.external = external;
    }

    public SpigotFile getFile() {
        return file;
    }

    public void setFile(SpigotFile file) {
        this.file = file;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }
}
