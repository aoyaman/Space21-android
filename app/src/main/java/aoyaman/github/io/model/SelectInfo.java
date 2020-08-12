package aoyaman.github.io.model;

public class SelectInfo {
//    SpaceType spaceType;
    int angle;
    boolean flip;
//    CellInfo[][] board;
//    KouhoInfo[] kouhoList;
    SpaceInfo spaceInfo;

    public SelectInfo(SpaceInfo spaceInfo, int angle, boolean flip) {
        this.spaceInfo = spaceInfo;
        this.angle = angle;
        this.flip = flip;
    }

    public SpaceInfo getSpaceInfo() {
        return this.spaceInfo;
    }
    public int getAngle() {
        return this.angle;
    }
    public boolean isFlip() {
        return this.flip;
    }
    public char[][] getCells() {
        int f = this.flip ? 1 : 0;
        return this.spaceInfo.type.shapes[this.angle * 2 + f];
    }

    public void doRotate() {
        if (this.flip == false) {
            this.angle++;
            if (this.angle >= 4) {
                this.angle = 0;
            }
        } else {
            this.angle--;
            if (this.angle < 0) {
                this.angle = 3;
            }
        }
    }
    public void doFlip() {
        this.flip = !this.flip;
    }
}
