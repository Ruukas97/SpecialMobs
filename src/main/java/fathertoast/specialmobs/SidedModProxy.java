package fathertoast.specialmobs;

public interface SidedModProxy {
    public void registerRenderers();

    public void loadMobTexture( int entityID, String[] texturePaths );
}
