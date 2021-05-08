package me.wolfyscript.utilities.api.nms;

abstract class UtilComponent {

    private final NMSUtil nmsUtil;

    protected UtilComponent(NMSUtil nmsUtil) {
        this.nmsUtil = nmsUtil;
    }

    /**
     * @return The current NMSUtil
     */
    public NMSUtil getNmsUtil() {
        return nmsUtil;
    }
}
