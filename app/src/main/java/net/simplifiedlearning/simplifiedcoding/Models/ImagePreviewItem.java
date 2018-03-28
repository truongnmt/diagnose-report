package net.simplifiedlearning.simplifiedcoding.Models;

import android.graphics.Bitmap;

/**
 * Created by nhict on 3/22/18.
 */

public class ImagePreviewItem {
    private Bitmap image;

    public ImagePreviewItem(Bitmap image){
        super();
        this.image = image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }
}
